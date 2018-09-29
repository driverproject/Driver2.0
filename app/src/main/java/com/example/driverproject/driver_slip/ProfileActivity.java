package com.example.driverproject.driver_slip;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;


    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private ListView listview;
    Context context;

    static int i;

    static ArrayList<String> slipID = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Vehicle");

        context = ProfileActivity.this;

        Log.d("yesh2", "hsey2");
        //Toast.makeText(ProfileActivity.this,"test",Toast.LENGTH_LONG).show();
        dateView = (TextView) findViewById(R.id.text_view_date);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);

        i = 0;

        dl=(DrawerLayout) findViewById(R.id.dl);
        abdt= new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();

//        populateListView();
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id= menuItem.getItemId();

                if(id== R.id.myprofile)
                {
                    Toast.makeText(ProfileActivity.this,"Your Profile !" ,Toast.LENGTH_SHORT).show();
                }

                if(id == R.id.signout)
                {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                if(id == R.id.editprofile)
                {
                    startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                }

                if(id == R.id.settings)
                {
                    Toast.makeText(ProfileActivity.this,"Settings !" ,Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

//        database=FirebaseDatabase.getInstance();
//        myRef = database.getReference("Vehicle");
//
//        populateListView();

    }

//    private void populateListView()
//    {   test();
//        Toast.makeText(context,"test12",Toast.LENGTH_LONG).show();
//        Log.d("yesh2","hsey");
//        ValueEventListener eventListener = new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Toast.makeText(context,"test1",Toast.LENGTH_LONG).show();
//                Log.d("yesh","hsey");
//                for (DataSnapshot ds : dataSnapshot.getChildren())
//                {
//                    slipID.add(ds.child("vehicle_Number").getValue(String.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("yesh1","hsey");
//                Toast.makeText(context,"test2",Toast.LENGTH_LONG).show();
//            }
//        };
//        Toast.makeText(context,"test13",Toast.LENGTH_LONG).show();
////        myRef.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
////                {
////                    slipID.add((String)postSnapshot.getValue());
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//
//        listview = (ListView)findViewById(R.id.list_view);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.sliplist, slipID);
//
//        listview.setAdapter(arrayAdapter);
//
//    }

    public void floatButton(View view)
    {
        Toast.makeText(this,"New Slip ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,Slip.class));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        String date = String.format("%02d", month) + "/" + String.format("%02d", day) + "/" + String.format("%02d", year);
        //String date = month+"/"+day+"/"+year;
        dateView.setText(date);
        test(date);
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem menuItem)
    {
        return abdt.onOptionsItemSelected(menuItem) || super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View v) {

    }

    public void test(final String date) {
        myRef.addChildEventListener(new ChildEventListener() {

            final ArrayList<String> slipID = new ArrayList<String>();

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //FirebaseUser uid = firebaseAuth.getCurrentUser();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Vehicle v = dataSnapshot.getValue(Vehicle.class);
                    if (!slipID.contains(v.getVehicle_Type())) {

//                        if (uid.equals(v.getUser_id()))
                        {
                            Log.i("TAG", v.getDate_journey() + " and " + date + " and " + v.getVehicle_Type() + " uid " + v.getUser_id());
                            slipID.add(v.getVehicle_Type());
                        }
                        String all = "";
                        for (String a : slipID) {
                            all += " " + a;
                        }
                        all = all + (i++) + v.getVehicle_Type();
                    }
                }

                listview = (ListView) findViewById(R.id.list_view);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.sliplist, slipID);

                listview.setAdapter(arrayAdapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

        });

    }

    public void pdflink(View view) {

    }
}
