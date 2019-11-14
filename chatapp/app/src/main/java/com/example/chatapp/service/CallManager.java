package com.example.chatapp.service;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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

import java.util.ArrayList;
import java.util.List;

public final class CallManager {

    private static SinchClient sinchClient;
    private static Call call;
    private static AlertDialog dialog;
    private static Context context;

    public static void doCall(String friendUID, String friendName) {
        try {
            call = sinchClient.getCallClient().callUser(friendUID);
            call.addCallListener(new SinchCallListener());
            // open caller dialog
            MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);
            alertDialog.setTitle("Calling: " + friendName);

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dia, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    call.hangup();
                }
            });
            dialog = alertDialog.show();
        } catch (Exception ex) {
            Toast.makeText(context, "Something error", Toast.LENGTH_LONG).show();
        }
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
            private List<AlertDialog> dialogs = new ArrayList<>();
            @Override
            public void onIncomingCall(CallClient callClient, Call incomingCall) {
                MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);
                String userUID = incomingCall.getRemoteUserId();
                incomingCall.addCallListener(new CallListener() {
                    @Override
                    public void onCallProgressing(Call caLL) {
                        Toast.makeText(context, "Ring...", Toast.LENGTH_LONG).show();
                        FirebaseDatabase.getInstance().getReference(DatabaseName.DB_USERS)
                                .child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                alertDialog.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dia, int which) {
                                        dialogs.forEach(t -> t.dismiss());
                                        incomingCall.hangup();
                                        dialogs = new ArrayList<>();
                                    }
                                });
                                alertDialog.setPositiveButton("Pick", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dia, int which) {
                                        call = incomingCall;
                                        call.answer();
                                        call.addCallListener(new SinchCallListener());
                                        Toast.makeText(context, "Call started...", Toast.LENGTH_LONG).show();
                                        dialogs.forEach(t -> t.dismiss());
                                        dialogs = new ArrayList<>();
                                    }
                                });
                                UserInfo user = dataSnapshot.getValue(UserInfo.class);
                                alertDialog.setTitle("Incoming call... ");
                                alertDialog.setMessage("From: " + user.getUsername());
                                AlertDialog dialog = alertDialog.show();
                                dialogs.add(dialog);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCallEstablished(Call call) {
                        FirebaseDatabase.getInstance().getReference(DatabaseName.DB_USERS)
                                .child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserInfo user = dataSnapshot.getValue(UserInfo.class);
                                MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(context);
                                alertDialog.setTitle("From: " + user.getUsername());

                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dia, int which) {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                            dialog = null;
                                        }
                                        call.hangup();
                                    }
                                });
                                dialog = alertDialog.show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCallEnded(Call endCall) {
                        Toast.makeText(context, "Call ended", Toast.LENGTH_LONG).show();
                        call = null;
                        endCall.hangup();
                        dialogs.forEach(t -> t.dismiss());
                        dialogs = new ArrayList<>();
                    }

                    @Override
                    public void onShouldSendPushNotification(Call call, List<PushPair> list) {
                    }
                });
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
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}
