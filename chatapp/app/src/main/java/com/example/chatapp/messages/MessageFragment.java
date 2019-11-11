package com.example.chatapp.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.messages.constant.DatabaseName;
import com.example.chatapp.messages.model.MessageDetail;
import com.example.chatapp.messages.model.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageFragment extends Fragment {

    // info of users whom send message to current user
    private List<UserInfo> mUsers = new ArrayList<>();
    private Set<String> mUserUIDs = new HashSet<>();
    private BaseAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        adapter = new BaseAdapter() {

            private Activity parentActivity = MessageFragment.this.getActivity();
            private List<UserInfo> mUsers = MessageFragment.this.mUsers;

            @Override
            public int getCount() {
                return mUsers.size();
            }

            @Override
            public Object getItem(int position) {
                return mUsers.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = parentActivity.getLayoutInflater().inflate(R.layout.adapter_msg_list_layout, null);
                }
                ImageView prifileImage = convertView.findViewById(R.id.profileImage);
                TextView displayName = convertView.findViewById(R.id.username);
                UserInfo user = mUsers.get(position);
                displayName.setText(user.getUsername());
                return convertView;
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ListView listView = view.findViewById(R.id.listViewMessage);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String friendId = ((UserInfo)listView.getAdapter().getItem(position)).getId();
            Intent intent = new Intent(MessageFragment.this.getContext(), MessageActivity.class);
            intent.putExtra("friendId",friendId);
            MessageFragment.this.getActivity().startActivity(intent);
        });
        FirebaseDatabase.getInstance()
                .getReference(DatabaseName.DB_MESSAGE_DETAIL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUserUIDs.clear();
                        String currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        StreamSupport.stream(dataSnapshot.getChildren().spliterator(), false)
                                .parallel()
                                .forEach(t -> {
                                    MessageDetail messageDetail = t.getValue(MessageDetail.class);
                                    if (messageDetail.getReceiverUID().equals(currUID)) {
                                        mUserUIDs.add(messageDetail.getSenderUID());
                                    }
                                    if (messageDetail.getSenderUID().equals(currUID)) {
                                        mUserUIDs.add(messageDetail.getReceiverUID());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        FirebaseDatabase.getInstance()
                .getReference(DatabaseName.DB_USERS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUsers.clear();
                        List data = StreamSupport.stream(dataSnapshot.getChildren().spliterator(), false)
                                .parallel()
                                .map(t -> t.getValue(UserInfo.class))
                                .filter(t -> mUserUIDs.contains(t.getId()))
                                .collect(Collectors.toList());
                        mUsers.addAll(data);
                        adapter.notifyDataSetChanged();
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
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Messages");
    }
}
