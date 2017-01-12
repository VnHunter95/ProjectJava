package com.example.hunter95.mynewtrackingapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hunter95 on 1/2/2017.
 */
public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private String id;
    private ArrayList<String> friendList;

    public String getId() {
        return id;
    }

    public FriendListAdapter(Context context, ArrayList<String> friendList, String id) {
        this.context = context;
        this.friendList = friendList;
        this.id = id;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friendList.indexOf(friendList.get(position));
    }
    private class viewHolder{
        TextView txtemail;
        Button btTrack;
        Button btnremoveFriend;
    };
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder view = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView ==null)
        {
            convertView = mInflater.inflate(R.layout.friendlist_item,null);
        }
        view = new viewHolder();
        view.txtemail = (TextView) convertView.findViewById(R.id.txtfriendemailitem);
        view.btTrack =  (Button) convertView.findViewById(R.id.btTrackFriend);
        view.btnremoveFriend = (Button) convertView.findViewById(R.id.btnRemoveFriend);
        final String email = friendList.get(position);
        view.txtemail.setText(email);

        view.btTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) context;
                main.trackFriend(email);

            }
        });
        view.btnremoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) context;
                main.removeFriendEmail(email,getId());
            }
        });
        return convertView;
    }
}
