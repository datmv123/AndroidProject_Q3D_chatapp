package com.example.chatapp.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.messages.MessageActivity;
import com.example.chatapp.messages.MessageFragment;
import com.example.chatapp.messages.constant.DatabaseName;
import com.example.chatapp.messages.model.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchFagment extends Fragment {

    private List<UserInfo> mUsers = new ArrayList<>();
    private BaseAdapter adapter;
    private UserInfo currUserInfo;
    private EditText textSearch;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        String currUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference(DatabaseName.DB_USERS+"/"+currUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currUserInfo = dataSnapshot.getValue(UserInfo.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        adapter = new BaseAdapter() {

            private Activity parentActivity = SearchFagment.this.getActivity();
            private List<UserInfo> mUsers = SearchFagment.this.mUsers;

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
                    convertView = parentActivity.getLayoutInflater().inflate(R.layout.adapter_search_user_layout, null);
                }
                UserInfo uUser = mUsers.get(position);

                convertView.findViewById(R.id.profileImage);
                TextView username = convertView.findViewById(R.id.username);
                username.setText(uUser.getUsername());
                return convertView;
            }
        };

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ListView listView = view.findViewById(R.id.listViewSearchUser);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String friendId = ((UserInfo)listView.getAdapter().getItem(position)).getId();
            Intent intent = new Intent(SearchFagment.this.getContext(), MessageActivity.class);
            intent.putExtra("friendId",friendId);
            SearchFagment.this.getActivity().startActivity(intent);
        });
        textSearch = view.findViewById(R.id.textSearch);
        view.findViewById(R.id.buttonSearch).setOnClickListener(v -> doSearch());
        doSearch();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Search");
    }

    private void doSearch() {
        String text = textSearch.getText() + "";
        FirebaseDatabase.getInstance()
                .getReference(DatabaseName.DB_USERS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUsers.clear();
                        List data = StreamSupport.stream(dataSnapshot.getChildren().spliterator(), false)
                                .parallel()
                                .map(t->t.getValue(UserInfo.class))
                                .filter(t->t.getUsername().toUpperCase().contains(text.toUpperCase()))
                                .filter(t->!t.getId().equals(currUserInfo.getId()))
                                .collect(Collectors.toList());
                        mUsers.addAll(data);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void doRequestFriend(){

    }
}
