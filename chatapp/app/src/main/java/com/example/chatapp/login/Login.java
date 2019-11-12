package com.example.chatapp.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.friends.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {
     static final int GOOGLE_SIGNIN= 123;
     FirebaseAuth mAuth;
     Button btnLogin;
     Button btnLogout; GoogleSignInClient googleSignInClient;

     DatabaseReference reference;
     FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            System.out.println("1:"+firebaseUser.getDisplayName());
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        btnLogin.setOnClickListener(v->signIn());
        btnLogout.setOnClickListener(v->signOut());
    }

    public void signIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,GOOGLE_SIGNIN);
    }
    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut().addOnCompleteListener(this,task->{
            System.out.println("logout");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGNIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            System.out.println(task);
            try{
              GoogleSignInAccount account = task.getResult(ApiException.class);
                System.out.println("hahah");
              if(account!=null) fireBaseAuthWithGoogle(account);
            }catch(ApiException e){
                System.out.println("LỖI Ở ĐÂY MÈ");
               e.printStackTrace();
            }
        }
    }
    private void fireBaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,task -> {
            if(task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String userid = firebaseUser.getUid();

                Query userByID = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
                userByID.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
                        //chua có trong database
                        System.out.println(firebaseUser.getPhotoUrl());
                       if(user == null ){

                           HashMap<String, String> hashMap = new HashMap<>();
                           hashMap.put("id", userid);
                           hashMap.put("username", firebaseUser.getDisplayName());
                           hashMap.put("imgUrl", firebaseUser.getPhotoUrl()+"");
                           hashMap.put("status", "offline");


                           reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       Intent intent = new Intent(Login.this, MainActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                               }
                           });
                       }
                       //da có trong database => chuyển offline->online
                       else {
                           reference.child("status").setValue("online");
                           Intent intent = new Intent(Login.this, MainActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(intent);
                           finish();
                       }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
            else {
                Toast.makeText(this,"Login Fail",Toast.LENGTH_LONG).show();
            }
        });
    }
}
