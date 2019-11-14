package com.example.chatapp.friends;

import android.content.Intent;
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
import com.example.chatapp.messages.MessageActivity;
import com.example.chatapp.messages.model.UserInfo;
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
    private User user;
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
        customListView.setOnItemClickListener((parent, view1, position, id) -> {
            String friendId = ((User) customListView.getAdapter().getItem(position)).getId();
            String friendName = ((User) customListView.getAdapter().getItem(position)).getUsername();
            String imageUrl = ((User) customListView.getAdapter().getItem(position)).getImgUrl();
            Intent intent = new Intent(this.getContext(), MessageActivity.class);
            intent.putExtra("friendId", friendId);
            intent.putExtra("friendName", friendName);
            intent.putExtra("imageUrl", imageUrl);
            this.getActivity().startActivity(intent);
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        //get current user
        reference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user =  dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                email.setText(firebaseUser.getEmail());
                loadImgFromUrl(user.getImgUrl(),avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userInfos = new ArrayList<>();


                 for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                     User user_item =  snapshot.getValue(User.class);
                     for (String key : user.getFriends().keySet()) {
                         if(key.equals(user_item.getId())) {
                             userInfos.add(user_item);
                         }
                     }


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
