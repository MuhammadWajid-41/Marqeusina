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

public class StartMarqueeFinderSide extends AppCompatActivity {

    //get marquee's email through shared pref.
    public SharedPreferences user_info;
    public SharedPreferences.Editor user_info_editor;
    public String uEmail="";

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    //user profile in nav bar
    private TextView mName, nav_user;

    private String name;
    private Uri photoUrl;
    private ImageView uPic;

    FirebaseFirestore dbroot;
    private String Uname, Upurl;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_marquee_finder_side);

        //--->Store shared pref.
        user_info=(SharedPreferences)getSharedPreferences("user_data",MODE_PRIVATE);
        user_info_editor=user_info.edit();

        if(!user_info.getString("user_email","").isEmpty())
        {
            uEmail =  user_info.getString("user_email","");
            //Toast.makeText(this, "user_email is: "+uEmail, Toast.LENGTH_SHORT).show();
        }


        dbroot = FirebaseFirestore.getInstance();

        getCurrentinfo();

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav=(NavigationView)findViewById(R.id.navmenu);

        nav.setItemIconTintList(null);

        //https://stackoverflow.com/questions/33560219/in-android-how-to-set-navigation-drawer-header-image-and-name-programmatically-i
        View hView =  nav.inflateHeaderView(R.layout.navheader);
        uPic = (ImageView)hView.findViewById(R.id.user_profile);
        nav_user = (TextView)hView.findViewById(R.id.user_name);


        //
        //

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new homefragment()).commit();
        nav.setCheckedItem(R.id.menu_home);


        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            Fragment temp;
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home :
                        temp=new homefragment();
                        break;
                    case R.id.ai_recommendations :
                        temp=new OurRecommendations();
                        break;
                    case R.id.menu_update_profile :
                        temp=new mfprofileupdate();
                        break;
                    case R.id.menu_add_comment :
                        temp=new commentsONmarquee();
                        break;
                    case R.id.menu_logout :
                        temp=new logout();
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

        DocumentReference docRef = dbroot.collection("MarqueeFinders").document(uEmail); //shared pref
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Uname = document.getString("name").toString().trim(); //user name
                        if(document.getString("purl") != null) //check if field exists
                        {
                            Upurl = document.getString("purl").toString().trim(); //user profile
                            //Picasso.get().load(Upurl).into(uPic);
                            RequestOptions requestOptions = new RequestOptions();
                            Glide.with(getApplicationContext()).load(Upurl)
                                    .apply(requestOptions).thumbnail(0.5f).into(uPic);
                        }
                        else
                        {
                            uPic.setImageResource(R.drawable.ic_baseline_account_circle_24);
                        }
                        nav_user.setText(Uname);

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