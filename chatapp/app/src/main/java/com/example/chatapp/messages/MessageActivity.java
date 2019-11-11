package com.example.chatapp.messages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chatapp.R;
import com.example.chatapp.messages.constant.DatabaseName;
import com.example.chatapp.messages.model.MessageDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageActivity extends AppCompatActivity {

    private String friendsUID;
    private String currUID;

    private TextView title;
    private EditText textSend;
    private ListView listView;
    private List<MessageDetail> messageDetails = new ArrayList<>();
    private String imageUrl;

    private BaseAdapter messageAdapter = new BaseAdapter() {

        Activity parentActivity = MessageActivity.this;
        List<MessageDetail> messageDetails = MessageActivity.this.messageDetails;
        String imageUrl = MessageActivity.this.imageUrl;

        @Override
        public int getCount() {
            return messageDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return messageDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageDetail messageDetail = messageDetails.get(position);

            ImageView profileImage = null;
            TextView showMessage = null;
            if (messageDetails.get(position).getSenderUID().equals(currUID)) {
                convertView = parentActivity.getLayoutInflater().inflate(R.layout.adapter_msg_right_layout, null);
            } else {
                convertView = parentActivity.getLayoutInflater().inflate(R.layout.adapter_msg_left_layout, null);
                profileImage = convertView.findViewById(R.id.profileImage);
            }
            showMessage = convertView.findViewById(R.id.showMessage);
            if (profileImage != null) {
                profileImage.setImageResource(R.mipmap.ic_launcher);
            }

            showMessage.setText(messageDetail.getContent());

            return convertView;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        friendsUID = intent.getStringExtra("friendId");
        currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //back to message list screen
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        title = findViewById(R.id.textTitle);

        findViewById(R.id.buttonSend).setOnClickListener(t -> sendMessage());
        textSend = findViewById(R.id.textSend);
        listView = findViewById(R.id.listViewMessageDetail);
        listView.setDivider(null);
        listView.setAdapter(messageAdapter);
        loadFriendInfo();
        showMessage();
    }

    private void loadFriendInfo() {
        title.setText("Friend's name");
    }

    private void sendMessage() {
        String msg = textSend.getText().toString();
        if (msg.equals("")) {
            Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        MessageDetail message = new MessageDetail(currUID, friendsUID, msg, new Date().getTime());
        reference.child(DatabaseName.DB_MESSAGE_DETAIL).push().setValue(message);
        textSend.setText("");
    }

    private void showMessage() {
        FirebaseDatabase.getInstance()
                .getReference(DatabaseName.DB_MESSAGE_DETAIL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messageDetails.clear();
                        List data = StreamSupport.stream(dataSnapshot.getChildren().spliterator(), false)
                                .parallel()
                                .map(t -> t.getValue(MessageDetail.class))
                                .filter(t -> t.getReceiverUID().equals(currUID) && t.getSenderUID().equals(friendsUID)
                                        || t.getReceiverUID().equals(friendsUID) && t.getSenderUID().equals(currUID))
                                .sorted((a, b) -> Long.compare(a.getSendTime(), b.getSendTime()))
                                .collect(Collectors.toList());
                        messageDetails.addAll(data);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
