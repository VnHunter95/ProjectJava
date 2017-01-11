package com.example.hunter95.mynewtrackingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class FriendTracker extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_friend_tracker, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                LinearLayout top = (LinearLayout) getActivity().findViewById(R.id.linearTop);
                LinearLayout bot = (LinearLayout) getActivity().findViewById(R.id.linearBottom);
                if (user != null) {
                    top.setVisibility(View.GONE);
                    bot.setVisibility(View.VISIBLE);
                    TextView status = (TextView) getActivity().findViewById(R.id.lbStatus);
                    status.setText("Login as: " + user.getEmail() + " ( " + user.getDisplayName() + " )");
                    MainActivity main = (MainActivity)getActivity();
                    //main.updateFriendList(main.getUserByEmail(user.getEmail()));
                    main.updateFriendList(user.getUid());
                    Log.d("Test", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    top.setVisibility(View.VISIBLE);
                    bot.setVisibility(View.GONE);
                    Log.d("Test", "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);


        Button btresgister = (Button) getActivity().findViewById(R.id.btRegister);
        Button btlogin = (Button) getActivity().findViewById(R.id.btLogin);
        Button btlogout= (Button) getActivity().findViewById(R.id.btLogout);
        Button btAddFriend = (Button) getActivity().findViewById(R.id.btAddFriend);
        Switch swt = (Switch) getActivity().findViewById(R.id.trackMeSwitch);
        swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {   /*Intent intent = new Intent(getActivity(), TrackingService.class);
                    intent.putExtra("Id", mAuth.getCurrentUser().getUid());
                    AlarmManager manager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                    PendingIntent alarmIntent = PendingIntent.getService(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,System.currentTimeMillis() + 1000 , 10000,alarmIntent);
                    getActivity().startService(intent);*/
                    Intent intent = new Intent(getActivity(), LocationService.class);
                    intent.putExtra("Id", mAuth.getCurrentUser().getUid());
                    getActivity().startService(intent);
                }else
                {
                    Intent intent = new Intent(getActivity(), LocationService.class);
                    getActivity().stopService(intent);
                }

            }
        });
        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailET = (EditText) getActivity().findViewById(R.id.txtFriendEmail);
                MainActivity main = (MainActivity) getActivity();
                /*UserInfo user = main.getUserByEmail(mAuth.getCurrentUser().getEmail());
                if(user==null)
                {
                    return;
                }
                main.addFriendEmail(emailET.getText().toString(),user);*/
                ListView listFriendView = (ListView) getActivity().findViewById(R.id.lvFriendList);
                FriendListAdapter adapter = (FriendListAdapter) listFriendView.getAdapter();
                main.addFriendEmail(emailET.getText().toString(),mAuth.getCurrentUser().getUid(),adapter.getFriendList());
            }
        });
        btlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
        btresgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) getActivity().findViewById(R.id.txtEmail);
                EditText pass = (EditText) getActivity().findViewById(R.id.txtEmailPass);
                register(email.getText().toString(),pass.getText().toString());

            }
        });
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) getActivity().findViewById(R.id.txtEmail);
                EditText pass = (EditText) getActivity().findViewById(R.id.txtEmailPass);
                login(email.getText().toString(), pass.getText().toString());
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void register(final String email, String pass)
    {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Test", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Failed Login",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            UserInfo user = new UserInfo();
                            user.setUsername(email);
                            user.setCurAlditude(0);
                            user.setCurLongditude(0);
                            user.setId(task.getResult().getUser().getUid());
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            user.setUpdateTime(formattedDate);
                            MainActivity main = (MainActivity) getActivity();
                            main.pushUserTest(user);
                        }

                        // ...
                    }
                });
    }
    private void login(String email,String pass)
    {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Test", "signInWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("test", "signInWithEmail:failed", task.getException());
                            Toast.makeText(getActivity(), "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
    private void signout()
    {
        mAuth.signOut();
    }

}