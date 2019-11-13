package com.example.chatapp.messages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.example.chatapp.R;
import com.example.chatapp.messages.constant.DatabaseName;
import com.example.chatapp.messages.model.MessageDetail;
import com.example.chatapp.service.CallManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageActivity extends AppCompatActivity {

    private String imageUrl;
    private String friendsUID;
    private String currUID;
    private String friendName;

    private EditText textSend;
    private ListView listView;
    private List<MessageDetail> messageDetails = new ArrayList<>();
    private BaseAdapter messageAdapter = new BaseAdapter() {

        Activity parentActivity = MessageActivity.this;
        List<MessageDetail> messageDetails = MessageActivity.this.messageDetails;

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
                loadImgFromUrl(imageUrl, profileImage);
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
        friendName = intent.getStringExtra("friendName");
        imageUrl = intent.getStringExtra("imageUrl");

        currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        findViewById(R.id.buttonSend).setOnClickListener(t -> sendMessage());
        textSend = findViewById(R.id.textSend);
        listView = findViewById(R.id.listViewMessageDetail);
        listView.setDivider(null);
        listView.setAdapter(messageAdapter);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(friendName);

        CallManager.setUp(this);
        showMessage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuItemCall:
                CallManager.doCall(friendsUID, friendName);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadImgFromUrl(String url, ImageView imageView) {
        Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(imageView);
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
                                .sorted((a, b) -> a.getSendTime() - b.getSendTime() > 0 ? 1 : -1)
                                .collect(Collectors.toList());
                        messageDetails.addAll(data);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_bar_message_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
