
package com.example.hunter95.mynewtrackingapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LocationListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_location_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = (ListView) getActivity().findViewById(R.id.listview);
        Button bt = (Button) getActivity().findViewById(R.id.bt_test);
        MainActivity main = (MainActivity)getActivity();
        main.setListView(listView, bt);
//        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadData();
    }

//    public void loadData() {
//        itemList.clear();
//        DatabaseHelper MyDb = DatabaseHelper.getInstance(getContext());
//        Cursor res = MyDb.getLocTable();
//        while(res.moveToNext())
//        {
//            Double latidum = res.getDouble(2);
//            Double longtidum = res.getDouble(3);
//            LocationItem item = new LocationItem(res.getInt(0),res.getString(1),res.getDouble(2),res.getDouble(3));
//            itemList.add(item);
//        }
//        if(itemList.size()==0)
//        {
//            Toast.makeText(getContext(),"No Data",Toast.LENGTH_SHORT).show();
//        }
//        adapter = new MyListViewAdapter(getContext(),itemList);
//        listView.setAdapter(adapter);
//    }
}