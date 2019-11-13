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
import com.squareup.picasso.Picasso;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ListView listView = view.findViewById(R.id.listViewMessage);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String friendId = ((UserInfo) listView.getAdapter().getItem(position)).getId();
            String friendName = ((UserInfo) listView.getAdapter().getItem(position)).getUsername();
            String imageUrl = ((UserInfo) listView.getAdapter().getItem(position)).getImgUrl();
            Intent intent = new Intent(MessageFragment.this.getContext(), MessageActivity.class);
            intent.putExtra("friendId", friendId);
            intent.putExtra("friendName", friendName);
            intent.putExtra("imageUrl", imageUrl);
            MessageFragment.this.getActivity().startActivity(intent);
        });
        // show user whom interact message with current user
        FirebaseDatabase.getInstance()
                .getReference(DatabaseName.DB_MESSAGE_DETAIL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUserUIDs.clear();
                        String currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        // get UIDs
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
                        // get detail infomation from UIDs
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return view;
    }

    public void loadImgFromUrl(String url, ImageView imageView) {
        Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(imageView);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Messages");
    }

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
                    convertView = parentActivity.getLayoutInflater().inflate(R.layout.adapter_msg_user_list_layout, null);
                }
                ImageView profileImage = convertView.findViewById(R.id.profileImage);
                TextView displayName = convertView.findViewById(R.id.username);
                TextView lastMessage = convertView.findViewById(R.id.lastMessage);
                lastMessage.setText("");
                UserInfo user = mUsers.get(position);
                loadImgFromUrl(user.getImgUrl(), profileImage);
                displayName.setText(user.getUsername());

                String currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String friendUID = user.getId();
                //get last message
                FirebaseDatabase.getInstance()
                        .getReference(DatabaseName.DB_MESSAGE_DETAIL)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               String content = StreamSupport.stream(dataSnapshot.getChildren().spliterator(), false)
                                        .parallel()
                                        .map(t -> t.getValue(MessageDetail.class))
                                        .filter(t -> t.getReceiverUID().equals(currUID) && t.getSenderUID().equals(friendUID)
                                                || t.getReceiverUID().equals(friendUID) && t.getSenderUID().equals(currUID))
                                        .sorted((a, b) -> a.getSendTime() - b.getSendTime() > 0 ? -1 : 1)
                                        .findFirst()
                                        .orElse(new MessageDetail())
                                        .getContent();
                               lastMessage.setText(content);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                return convertView;
            }

        };
    }
}
