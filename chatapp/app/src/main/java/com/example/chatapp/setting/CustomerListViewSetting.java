package com.example.chatapp.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

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

        photo = convertView.findViewById(R.id.photo);
        TextView name=convertView.findViewById(R.id.name);
        Switch aSwitch =convertView.findViewById(R.id.switch1);
        //photo


         photo.setImageResource(ob.getIcon());
         if(ob.getId()==1)
           aSwitch.setVisibility(View.VISIBLE);
         else
             aSwitch.setVisibility(View.INVISIBLE);
          name.setText(ob.getNameSetting());

        return convertView;
    }
}
