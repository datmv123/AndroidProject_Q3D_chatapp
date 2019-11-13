package com.example.chatapp.service;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;

import com.example.chatapp.R;
import com.example.chatapp.messages.constant.DatabaseName;
import com.example.chatapp.messages.model.UserInfo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public final class CallManager {

    private static SinchClient sinchClient;
    private static Call call;

    private static Context context;

    public static void doCall(String friendUID,String friendName) {
        call = sinchClient.getCallClient().callUser(friendUID);
        call.addCallListener(new SinchCallListener());
        // open caller dialog
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);
        alertDialog.setTitle("Calling: "+friendName);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                call.hangup();
            }
        });
        alertDialog.show();
    }

    public static void setUp(Context context) {
        CallManager.context = context;
        sinchClient = Sinch.getSinchClientBuilder()
                .context(context)
                .userId(FirebaseAuth.getInstance().getUid())
                .applicationKey("a7bf997d-c8b9-488f-a115-b98a6897e9ce")
                .applicationSecret("31uu8xY9506Ffd+t/dXIuQ==")
                .environmentHost("clientapi.sinch.com")
                .build();
        // setup support calling
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call incomingCall) {
                MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);
                alertDialog.setTitle("Incoming call ...");
                String userUID = incomingCall.getCallId();
                alertDialog.setMessage("");
                FirebaseDatabase.getInstance().getReference(DatabaseName.DB_USERS)
                        .child(userUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserInfo user = dataSnapshot.getValue(UserInfo.class);
                        alertDialog.setMessage(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                alertDialog.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        call.hangup();
                    }
                });
                alertDialog.setPositiveButton("Pick", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        call = incomingCall;
                        call.answer();
                        call.addCallListener(new SinchCallListener());
                        Toast.makeText(context, "Call started...", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        sinchClient.start();
    }

    public static void releaseListener() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

    private static class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(context, "Ring...", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEstablished(Call call) {
            Toast.makeText(context, "Call established", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEnded(Call endCall) {
            Toast.makeText(context, "Call ended", Toast.LENGTH_LONG).show();
            call = null;
            endCall.hangup();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}
