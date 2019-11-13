package com.example.chatapp.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class  FriendsFragment extends Fragment {
    private ArrayList<User> userInfos;
    private CustomerListAdapter customListAdapter;
    private ListView customListView;
    private TextView username;
    private TextView email;
    private ImageView avatar;
    private FirebaseUser firebaseUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        customListView = view.findViewById(R.id.custom_list_view);
        userInfos = new ArrayList<>();

        username = view.findViewById(R.id.txtProfileName);
        email = view.findViewById(R.id.txtProfileEmail);
        avatar = view.findViewById(R.id.profileImage);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println(firebaseUser.getDisplayName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                     User user =  snapshot.getValue(User.class);

                     if(user.getId().equals(firebaseUser.getUid())){
                         username.setText(user.getUsername());
                         email.setText(firebaseUser.getEmail());
                         loadImgFromUrl(user.getImgUrl(),avatar);
                     }
                     else
                       userInfos.add(user);


                 }
                customListAdapter = new CustomerListAdapter(userInfos,view.getContext());
                customListView.setAdapter(customListAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Friends");
    }

    public void loadImgFromUrl(String url,ImageView imageView){
        Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(imageView);
    }
}
