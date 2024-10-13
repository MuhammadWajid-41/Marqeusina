package com.example.Marqeusina;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartMarqueeOwnerSide extends AppCompatActivity {

    //get marquee's email through shared pref.
    public SharedPreferences marquee_info;
    public SharedPreferences.Editor marquee_info_editor;
    public String mEmail="";

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    //user profile in nav bar
    private TextView mName, nav_user;

    private String name;
    private Uri photoUrl;
    private ImageView mPic;

    FirebaseFirestore dbroot;
    private String Mname, Mpurl;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_marquee_owner_side);

        //--->Store shared pref.
        marquee_info=(SharedPreferences)getSharedPreferences("marquee_data",MODE_PRIVATE);
        marquee_info_editor=marquee_info.edit();

        if(!marquee_info.getString("marquee_email","").isEmpty())
        {
            mEmail =  marquee_info.getString("marquee_email","");
           // Toast.makeText(this, "marquee_email is: "+mEmail, Toast.LENGTH_SHORT).show();
        }


        dbroot = FirebaseFirestore.getInstance();

        getCurrentinfo();

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav=(NavigationView)findViewById(R.id.navmenu);

        nav.setItemIconTintList(null);

        //https://stackoverflow.com/questions/33560219/in-android-how-to-set-navigation-drawer-header-image-and-name-programmatically-i
        View hView =  nav.inflateHeaderView(R.layout.navheadermos);
        mPic = (ImageView)hView.findViewById(R.id.marquee_profile);
        nav_user = (TextView)hView.findViewById(R.id.marquee_name);


        //
        //

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new homefragmentMOS()).commit();
        nav.setCheckedItem(R.id.menu_home);


        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            Fragment temp;
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home :
                        temp=new homefragmentMOS();
                        break;
                    case R.id.menu_update_profile :
                        temp=new moprofileupdate();
                        break;
                    case R.id.bookings:
                        temp=new Bookings();
                        break;
                    case R.id.visited_users:
                        temp=new VisitedUsers();
                        break;
                    case R.id.packages :
                        temp=new Packages();
                        break;
                    case R.id.disaster_alert:
                        temp=new DisasterAlert();
                        break;
                    case R.id.menu_logout :
                        temp=new MOlogout();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container,temp).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }//ON CREATE() END

    //get user data method
    private void getCurrentinfo() {

        //Toast.makeText(getApplicationContext(), "entered in getCurrentinfo : ", Toast.LENGTH_SHORT).show();

        DocumentReference docRef = dbroot.collection("MarqueeOwners").document(mEmail); //shared pref
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Mname = document.getString("name").toString().trim(); //user name
                        if(document.getString("purl") != null) //check if field exists
                        {
                            Mpurl = document.getString("purl").toString().trim(); //user profile
                            //Picasso.get().load(Upurl).into(mPic);
                       RequestOptions requestOptions = new RequestOptions();
                        Glide.with(getApplicationContext()).load(Mpurl)
                                .apply(requestOptions).thumbnail(0.5f).into(mPic);
                        }
                        else
                        {
                            mPic.setImageResource(R.drawable.ic_baseline_account_circle_24);
                        }
                        nav_user.setText(Mname);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}//MAIN END