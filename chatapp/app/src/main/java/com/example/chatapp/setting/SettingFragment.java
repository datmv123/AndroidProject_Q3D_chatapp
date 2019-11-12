package com.example.chatapp.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;

import com.example.chatapp.friends.User;
import com.example.chatapp.login.Login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    List<SettingOb> list;
    CustomerListViewSetting customerListViewSetting;
    private ListView listView;
    private final static int ImageBack =1;
    private StorageReference folder;
    private ImageView photo;
    private DatabaseReference reference;
    private Context context;
    private TextView txtUsername;
    FirebaseUser firebaseUser;
    private User user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         list = new ArrayList<>();
         View view = inflater.inflate(R.layout.fragment_setting, container, false);
         context= view.getContext();
         listView = view.findViewById(R.id.listviewSetting);
         photo = view.findViewById(R.id.profileImage);
         txtUsername = view.findViewById(R.id.txtProfileName);
         firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();

         //get user
        reference = FirebaseDatabase.getInstance().
                getReference("users").child(firebaseUser.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

             @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   user = dataSnapshot.getValue(User.class);
                   txtUsername.setText(user.getUsername());
                   loadImgFromUrl(user.getImgUrl(),photo);
                 }

             @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
                });
         SettingOb s1 = new SettingOb(1,R.drawable.dark2,"DarkMode");
         SettingOb s2 = new SettingOb(2,R.drawable.username,"Username");
         SettingOb s3 = new SettingOb(3,R.drawable.qr,"QR Code");
         SettingOb s4 = new SettingOb(4,R.drawable.logout,"Logout");
         list.add(s1);
         list.add(s2);
         list.add(s3);
         list.add(s4);
         customerListViewSetting = new CustomerListViewSetting(list,this.getContext());
          listView.setAdapter(customerListViewSetting);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                System.out.println("hello click");
                switch (list.get(position).getId()){
                    case 1:{

                        System.out.println("haha");
                        break;
                    }
                    case 2: {
                        showDialog(user);
                        break;
                    }
                    case 3:
                    case 4: signOut(); break;
                }

            }
        });
          folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
        photo.setOnClickListener(v->uploadImage(view));
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Setting");
    }

    public void uploadImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,ImageBack);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageBack){
            if(resultCode == MainActivity.RESULT_OK) {

                Uri imageData = data.getData();
                StorageReference Imagename = folder.child("image"+imageData.getLastPathSegment());
                Imagename.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                           @Override
                           public void onSuccess(Uri uri) {


                               reference.child("imgUrl").setValue(String.valueOf(uri)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           Toast.makeText(context,"Updated Avatar Success!",Toast.LENGTH_LONG).show();
                                       }
                                   }
                               });
                               reference.addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       User user = dataSnapshot.getValue(User.class);
                                       loadImgFromUrl(user.getImgUrl(),photo);
                                       System.out.println("lister");
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });
                           }
                       });
                    }
                });
            }
        }
    }
    public void loadImgFromUrl(String url,ImageView imageView){
        Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(imageView);
    }
    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        GoogleSignInClient googleSignInClient;
        googleSignInClient = GoogleSignIn.getClient(context,googleSignInOptions);
        googleSignInClient.signOut();
        Intent intent = new Intent(context, Login.class);
            startActivity(intent);

    }

    public void showDialog(User user){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.customer_dialog, null);
        final EditText etUsername =  alertLayout.findViewById(R.id.et_Username);
        etUsername.setText(user.getUsername());
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Update Username");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getApplicationContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // code for matching password
                String username = etUsername.getText().toString();
                reference.child("username").setValue(username);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        txtUsername.setText(user.getUsername());
                        Toast.makeText(getContext(),"Updated Successfully!" , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void restartApp(){
        Intent i = new Intent(getActivity().getApplicationContext(),MainActivity.class);
        startActivity(i);

    }
}
