package com.example.chatapp.setting;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    List<SettingOb> list;
    CustomerListViewSetting customerListViewSetting;
    private ListView listView;
    private final static int ImageBack =1;
    private StorageReference folder;
    private ImageView photo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         list = new ArrayList<>();
         View view = inflater.inflate(R.layout.fragment_setting, container, false);
         listView = view.findViewById(R.id.listviewSetting);
         photo = view.findViewById(R.id.profileImage);
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
//          folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
//          photo.setOnClickListener(v->uploadImage(view));
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Setting");
    }

//    public void uploadImage(View view){
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent,ImageBack);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == ImageBack){
//            if(resultCode == MainActivity.RESULT_OK) {
//
//                Uri imageData = data.getData();
//                StorageReference Imagename = folder.child("image"+imageData.getLastPathSegment());
//                Imagename.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        System.out.println("uploaded");
//                    }
//                });
//            }
//        }
//    }
}
