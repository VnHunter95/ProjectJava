package com.example.hunter95.mynewtrackingapp;

        import android.location.Location;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.Toast;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {
        private GoogleMap mMap;
        public Marker maker;
        public static MapFragment newInstance() {
                MapFragment fragment = new MapFragment();
                return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_map, null, false);

                SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                return view;
        }


        @Override
        public void onStart() {
                super.onStart();
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                Button bt = (Button)
                        getActivity().findViewById(R.id.btcurrentloc);
                final Button btusemarker = (Button)
                        getActivity().findViewById(R.id.btnUseMarker);
                Button btsavemarker= (Button)
                        getActivity().findViewById(R.id.btnSaveWithMarker);;
                Button btsave = (Button)
                        getActivity().findViewById(R.id.btsavecurrentloc);
                final MainActivity main = (MainActivity)getActivity();
                main.setGoogleMap(mMap);
                btsavemarker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                MainActivity main = (MainActivity) getActivity();
                                main.saveMarker();
                        }
                });
                btusemarker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                MainActivity main = (MainActivity) getActivity();
                                if(btusemarker.getText().toString().compareTo(getResources().getString(R.string.cancle))==0)
                                {

                                        main.clearUserMarker();
                                        btusemarker.setText(getResources().getString(R.string.usemarker));
                                        return;
                                }
                                btusemarker.setText(getResources().getString(R.string.cancle));
                                main.useMarker();
                        }
                });
                if(bt!=null)
                {
                        bt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                       MainActivity main = (MainActivity) getActivity();
                                        main.getCurrentLoc();
                                }
                        });
                }
                if(btsave!=null)
                {
                        btsave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                       MainActivity main = (MainActivity) getActivity();
                                        main.saveCurrentLoc();
                                }
                        });
                }
                final Button findRoute = (Button) getActivity().findViewById(R.id.btnFindRuote);
                findRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                MainActivity main = (MainActivity) getActivity();
                                if(findRoute.getText().toString().compareTo(getResources().getString(R.string.cancleFindRoute))==0)
                                {

                                        main.clearMap();
                                        findRoute.setText(getResources().getString(R.string.findRuote));
                                        main.isRouting=false;
                                        return;
                                }
                                findRoute.setText(getResources().getString(R.string.cancleFindRoute));
                                main.findRouteWithMarker();

                        }
                });
                View.OnClickListener bottomTabListen = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                LinearLayout layoutBtnBot1 = (LinearLayout) getActivity().findViewById(R.id.btnBotTab1layout);
                                LinearLayout layoutBtnBot2 = (LinearLayout) getActivity().findViewById(R.id.btnBotTab2layout);
                                LinearLayout layoutBtnBot3 = (LinearLayout) getActivity().findViewById(R.id.btnBotTab3layout);
                                switch(v.getId())
                                {
                                        case R.id.btnBotTab1:
                                                layoutBtnBot1.setVisibility(View.VISIBLE);
                                                layoutBtnBot2.setVisibility(View.GONE);
                                                layoutBtnBot3.setVisibility(View.GONE);
                                                break;
                                        case R.id.btnBotTab2:
                                                layoutBtnBot1.setVisibility(View.GONE);
                                                layoutBtnBot2.setVisibility(View.VISIBLE);
                                                layoutBtnBot3.setVisibility(View.GONE);
                                                break;
                                        case R.id.btnBotTab3:
                                                layoutBtnBot1.setVisibility(View.GONE);
                                                layoutBtnBot2.setVisibility(View.GONE);
                                                layoutBtnBot3.setVisibility(View.VISIBLE);
                                                MainActivity main = (MainActivity) getActivity();
                                                if(main.isRouting)
                                                {

                                                }
                                                break;
                                }
                        }
                };
                Button botTabbtn1 = (Button) getActivity().findViewById(R.id.btnBotTab1);
                Button botTabbtn2 = (Button) getActivity().findViewById(R.id.btnBotTab2);
                Button botTabbtn3 = (Button) getActivity().findViewById(R.id.btnBotTab3);
                botTabbtn1.setOnClickListener(bottomTabListen);
                botTabbtn2.setOnClickListener(bottomTabListen);
                botTabbtn3.setOnClickListener(bottomTabListen);
        }


}