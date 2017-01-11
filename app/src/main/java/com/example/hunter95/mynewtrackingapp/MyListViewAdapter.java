package com.example.hunter95.mynewtrackingapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Hunter95 on 12/12/2016.
 */
public class MyListViewAdapter extends BaseAdapter {

    Context context;
    List<LocationItem> itemList;
    public MyListViewAdapter(Context context, List<LocationItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.indexOf(itemList.get(position));
    }

    private class viewHolder{
        TextView txtname;
        Button btdelete,btTrack;
    };
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder view = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView ==null)
        {
            convertView = mInflater.inflate(R.layout.listview_item,null);
        }
        view = new viewHolder();
        view.txtname = (TextView) convertView.findViewById(R.id.txtname);
        view.btdelete = (Button) convertView.findViewById(R.id.btdeleteitem);
        view.btTrack =  (Button) convertView.findViewById(R.id.bttrack);
        final LocationItem item = itemList.get(position);
        view.txtname.setText(item.getName());
        view.btdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Delete pressed " + item.getName(), Toast.LENGTH_SHORT).show();
                MainActivity main = (MainActivity) context;
                itemList.remove(position);
                notifyDataSetChanged();
                if(!item.removeFromDatabase(context))
                {
                    Toast.makeText(context, "Delete Error " + item.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.btTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) context;
                main.track(item);

            }
        });
        return convertView;
    }
}
