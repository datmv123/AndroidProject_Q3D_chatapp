package com.example.chatapp.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.chatapp.R;


import java.util.List;

public class CustomerListViewSetting extends BaseAdapter {
    List<SettingOb> list;
    Context context;

    public CustomerListViewSetting(List<SettingOb> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=layoutInflater.inflate(R.layout.setting_item_layout,null);
        ImageView photo;
        if(convertView==null){
            photo = new ImageView(context);
        }
        SettingOb ob  = list.get(position);

        photo = convertView.findViewById(R.id.profileImage);
        TextView name=convertView.findViewById(R.id.txtProfileName);
        Switch aSwitch =convertView.findViewById(R.id.switch1);
        //photo
                        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                            context.setTheme(R.style.darkthem);
                            aSwitch.setChecked(true);
                        }
                        else context.setTheme(R.style.AppTheme);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    System.out.println("checked");
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    System.out.println("unchcked");
                }
            }
        });
         photo.setImageResource(ob.getIcon());
         if(ob.getId()==1)
           aSwitch.setVisibility(View.VISIBLE);
         else
             aSwitch.setVisibility(View.INVISIBLE);
          name.setText(ob.getNameSetting());

        return convertView;
    }
}
