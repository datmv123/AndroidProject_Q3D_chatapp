package com.example.chatapp.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerListAdapter extends BaseAdapter {
    private ArrayList<User> list;
    private Context context;

    public CustomerListAdapter(ArrayList<User> list, Context context) {
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
        convertView=layoutInflater.inflate(R.layout.customer_list_view_layout,null);
        ImageView photo,status;
        if(convertView==null){
            photo = new ImageView(context);
        }
        User user  = list.get(position);

        photo = convertView.findViewById(R.id.profileImage);
        TextView name=convertView.findViewById(R.id.txtProfileName);
        status=convertView.findViewById(R.id.option);
        loadImgFromUrl(user.getImgUrl(),photo);

        // photo.setImageResource(R.drawable.flash);
        status.setImageResource(R.drawable.active1);
        name.setText(user.getUsername());
        if(user.getStatus().equals("offline")){
            status.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void loadImgFromUrl(String url,ImageView imageView){
        Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(imageView);
    }
}
