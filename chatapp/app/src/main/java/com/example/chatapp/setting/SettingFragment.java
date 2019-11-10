package com.example.chatapp.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    List<SettingOb> list;
    CustomerListViewSetting customerListViewSetting;
    private ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         list = new ArrayList<>();
         View view = inflater.inflate(R.layout.fragment_setting, container, false);
         listView = view.findViewById(R.id.listviewSetting);
         SettingOb s1 = new SettingOb(1,R.drawable.dark2,"DarkMode");
         SettingOb s2 = new SettingOb(2,R.drawable.username,"Username");
         SettingOb s3 = new SettingOb(3,R.drawable.qr,"QR Code");
         SettingOb s4 = new SettingOb(4,R.drawable.logout,"Logout");
         list.add(s1);
         list.add(s2);
         list.add(s3);
         list.add(s4);
         customerListViewSetting = new CustomerListViewSetting(list,this.getContext());

         System.out.println(view);
          listView.setAdapter(customerListViewSetting);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Setting");
    }
}
