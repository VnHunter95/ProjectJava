package com.example.hunter95.mynewtrackingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private ListView listView;
    private Button reload;
    private MyListViewAdapter listViewadapter;
    private List<LocationItem> itemList;
    private PagerAdapter adapter;
    private LocationItem currentLoc;
    private Marker userMarker;
    private Marker maker;
    private ValueEventListener trackingEventListener;
    private  String curUserId;
    private boolean fristLoad=true;
    public boolean isRouting=false;
    public MarkerOptions markerOption;
    private ArrayList<UserInfo> userList;
    private DatabaseReference trackedUserPath;
    LocationManager myManager;
    GoogleMap gMap;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initFireBase();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        markerOption = new MarkerOptions().title("Me");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        itemList = new ArrayList<LocationItem>();
    }

    private void initFireBase() {
        database = FirebaseDatabase.getInstance().getReference();
        /*database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.child("Account").getChildren()) {
                    UserInfo post = postSnapshot.getValue(UserInfo.class);
                    userList.add(post);
                    Log.e("Test", "onDataChange: Email " + post.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

        public void loadData()
        {
        itemList.clear();
        DatabaseHelper MyDb = DatabaseHelper.getInstance(this);
        Cursor res = MyDb.getLocTable();
        while(res.moveToNext())
        {
            Double latidum = res.getDouble(2);
            Double longtidum = res.getDouble(3);
            LocationItem item = new LocationItem(res.getInt(0),res.getString(1),res.getDouble(2),res.getDouble(3));
            itemList.add(item);
        }
        if(itemList.size()==0)
        {
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
        }
        listViewadapter = new MyListViewAdapter(this,itemList);
        listView.setAdapter(listViewadapter);
        listViewadapter.notifyDataSetChanged();
    }

    private static String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }
    public void setGoogleMap(GoogleMap mMap) {
        gMap = mMap;
        if(fristLoad) {
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.771528, 106.698433), 14), 2000, null);
        }
    }

    public void track(LocationItem item)
    {
        if(!getCurrentLoc())
        {
            return;
        }
        gMap.addMarker(new MarkerOptions().title(item.getName()).position(new LatLng(item.getLatitude(), item.getLongitude())));
        String serverKey = "AIzaSyBfijZk0SG_vuEi476wDtFAoyuhPvc-hAU";
        final LatLng origin = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
        LatLng destination = new LatLng(item.getLatitude(), item.getLongitude());
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .language(Language.VIETNAMESE)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Toast.makeText(MainActivity.this, "Get route successful !", Toast.LENGTH_SHORT).show();
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> pointList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(MainActivity.this, pointList, 5, Color.RED);
                            gMap.addPolyline(polylineOptions);
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 14), 2000, null);
                            isRouting = true;
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(MainActivity.this, "Error : " + t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    public boolean getCurrentLoc() {
        TrackGPS gps = new TrackGPS(this);
        if (gps.canGetLocation()) {

            if(maker!=null)
            {
                    maker.remove();
            }
            double longitude = gps.getLongitude();
            double latitude = gps.getLatitude();
            LatLng current = new LatLng( gps.getLatitude(), gps.getLongitude());
            markerOption.position(current);
            maker = gMap.addMarker(markerOption);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 14), 2000, null);
            Toast.makeText(this, "Latitude:" + Double.toString(latitude)+"\nLongitude:" + Double.toString(longitude), Toast.LENGTH_SHORT).show();
            currentLoc = new LocationItem(0,"",latitude,longitude);
            return true;
        } else {

            gps.showSettingsAlert();
            return false;
        }
    }

    public void saveCurrentLoc() {
        if(getCurrentLoc())
        {
            saveLoc(currentLoc);
        }
    }
    private void saveLoc(final LocationItem item)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập Nhãn Vị Trí");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.setName(input.getText().toString());
                item.addToDatabase(MainActivity.this);
                loadData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void setListView(ListView listView, Button bt) {
        this.listView = listView;
        this.reload = bt;
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        loadData();
    }
    public void removeFriendEmail(final String email,String id)
    {
        Query query = database.child("Account").orderByKey().equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    for (DataSnapshot friendlistChild : child.child("friendList").getChildren())
                        if (friendlistChild.getValue().toString().equals(email)) {
                            friendlistChild.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Toast.makeText(MainActivity.this,"Removed " + email,Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void addFriendEmail(final String email, final String id, final ArrayList<String> friendlist)
    {
        /*ArrayList<UserInfo> temp = (ArrayList<UserInfo>) userList.clone();
        for(int i =0; i<temp.size();i++)
        {
            if(temp.get(i).getUsername().equals(user.getUsername()))
            {
                for(int j=0; j<user.getFriendList().size();j++)
                {
                    if(user.getFriendList().get(j).equals(email))
                    {
                        Toast.makeText(this,"This email is already in your friend list !",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                user.getFriendList().add(email);
                pushUserTest(user);
                updateFriendList(user);
                return;
            }

        }*/
        Query query = database.child("Account").orderByChild("username").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    insertFriendEmailOnFireBase(email, id, friendlist);
                    return;
                }
                Toast.makeText(MainActivity.this,"Can't find email !",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void insertFriendEmailOnFireBase(String email,String id,ArrayList<String> friendlist) {
        Map newFriendslist = new HashMap();
        int itemid=0;
        for(String item:friendlist)
        {
            if(item.equals(email))
            {
                Toast.makeText(MainActivity.this,email + " is already in your friend list !",Toast.LENGTH_SHORT).show();
                return;
            }
            newFriendslist.put(String.valueOf(itemid),item);
            itemid++;
        }
        newFriendslist.put(String.valueOf(itemid), email);
        database.child("Account").child(id).child("friendList").updateChildren(newFriendslist);
    }

    public void pushUserTest(UserInfo userInfo) {
        database.child("Account").child(userInfo.getId()).setValue(userInfo);
    }
    public void updateFriendList(String id)
    {
        final ArrayList<String> friendList = new ArrayList<>();
        final ListView lv = (ListView) findViewById(R.id.lvFriendList);
        final FriendListAdapter friendListAdapter = new FriendListAdapter(this, friendList,id);
        lv.setAdapter(friendListAdapter);
        database.child("Account").child(id).child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Test", "onDataChange: Updated");
                FriendListAdapter adapter = (FriendListAdapter) lv.getAdapter();
                ArrayList<String> list = adapter.getFriendList();
                list.clear();
                Iterable<DataSnapshot> child = dataSnapshot.getChildren();
                for (DataSnapshot data : child) {
                    list.add(data.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void trackFriend(String email) {
        Query query = database.child("Account").orderByChild("username").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    setLisnerOnLocationChange(child.child("id").getValue().toString());
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setLisnerOnLocationChange(String id) {
        if(trackingEventListener!=null)
        {
            cancelTracking();
        }
        trackedUserPath = database.child("Account").child(id);
        trackingEventListener = trackedUserPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gMap.clear();
                MarkerOptions marker = new MarkerOptions();
                marker.title(dataSnapshot.child("username").getValue().toString());
                LatLng trace = new LatLng(Double.valueOf(dataSnapshot.child("curAlditude").getValue().toString()), Double.valueOf(dataSnapshot.child("curLongditude").getValue().toString()));
                marker.position(trace);
                gMap.addMarker(marker);
                marker.position(trace);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(trace));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Toast.makeText(this, "Tracking !", Toast.LENGTH_SHORT).show();
    }

    public void useMarker() {
        MarkerOptions marker = new MarkerOptions();
        marker.title("Drag me !");
        marker.draggable(true);
        marker.position(gMap.getCameraPosition().target);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        if(userMarker!=null)
        {
            userMarker.remove();
        }
        userMarker = gMap.addMarker(marker);
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                userMarker = marker;
                Toast.makeText(MainActivity.this, String.format(userMarker.getPosition().latitude + " , " + userMarker.getPosition().longitude), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveMarker() {
        if(userMarker==null)
        {
            Toast.makeText(MainActivity.this,"Error ! No Marker !",Toast.LENGTH_SHORT).show();
            return;
        }
        saveLoc(new LocationItem(0, "", userMarker.getPosition().latitude, userMarker.getPosition().longitude));
        clearUserMarker();
    }
    public void clearMap()
    {
        gMap.clear();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_clear)
        {
            clearMap();
        }
        return super.onOptionsItemSelected(item);

    }

    public void clearUserMarker() {
        if(userMarker!=null)
        {
            userMarker.remove();
            userMarker=null;
        }
    }

    public void findRouteWithMarker() {
        MarkerOptions marker = new MarkerOptions();
        marker.title("Drag me !");
        marker.snippet("Drag and drop to find route to the marker.");
        marker.draggable(true);
        marker.position(gMap.getCameraPosition().target);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        if(userMarker!=null)
        {
            userMarker.remove();
        }
        userMarker = gMap.addMarker(marker);
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                userMarker = marker;
                findRuoteToUserMarker();
            }
        });
    }

    private void findRuoteToUserMarker() {
        if(userMarker==null)
        {
            Toast.makeText(MainActivity.this,"No User Marker !",Toast.LENGTH_SHORT).show();
            return;
        }
        track(new LocationItem(0, getResources().getString(R.string.destination), userMarker.getPosition().latitude, userMarker.getPosition().longitude));
        userMarker.remove();
    }

    public void cancelTracking() {
        if(trackingEventListener!=null) {
            trackedUserPath.removeEventListener(trackingEventListener);
            trackingEventListener=null;
        }else
        {
            Toast.makeText(MainActivity.this,getResources().getString(R.string.isnottracking),Toast.LENGTH_SHORT).show();
        }

    }
}