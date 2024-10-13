package com.example.Marqeusina;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegMF extends AppCompatActivity implements View.OnClickListener, LocationListener
{
    public String userType="";
    //location message checker
    public int c = 0;
    //

    private TextView banner, alreadyregistered, pgbartxt;
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextContact;
    private Button userlocation, registeruser;
    private ProgressBar progressBar;//user regestraion process bar

    private FirebaseAuth mAuth;

    //PERMISSONS
    LocationManager locationManager;
    //

    FirebaseFirestore dbroot;
    public  int f = 0;

    //
    Map<String,Object> items = new HashMap<>();
    //
    //-------> MAIN ONCREATE()
    @Override
    protected void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_reg_mf);

        mAuth = FirebaseAuth.getInstance();
        dbroot = FirebaseFirestore.getInstance();

        banner = (TextView) findViewById(R.id.appname); //appname clicl to login page
        banner.setOnClickListener(this);
        //pgbar
        pgbartxt = (TextView) findViewById(R.id.tv);
        pgbartxt.setVisibility(View.GONE);

        alreadyregistered = (TextView) findViewById(R.id.alreadyRegistered); //goto login page
        alreadyregistered.setOnClickListener(this);
        /*{
            @Override
            public void onClick(View view)
            {
                //goto login page
                Intent i = new Intent(getApplicationContext(), UserLoginActivity.class);
                //s = t1.getText().toString();
                //Toast.makeText(MainActivity.this, "->" + t1.getText().toString()+" "+s, Toast.LENGTH_SHORT).show();
                //i.putExtra("UN",t1.getText().toString());
                startActivity(i);
            }
        });*/

        registeruser = (Button) findViewById(R.id.Register); //register user
        registeruser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.UserName);
        editTextEmail = (EditText) findViewById(R.id.Email);
        editTextPassword = (EditText) findViewById(R.id.Password);
        editTextConfirmPassword = (EditText) findViewById(R.id.ConfirmPassword);
        editTextContact = (EditText) findViewById(R.id.Contact);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }//onCreate end

    //----------> onClick methods switch
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appname:
                //startActivity(new Intent(this, UserLoginActivity.class));
                Intent i1 = new Intent(getApplicationContext(), MFUserLoginActivity.class);
                startActivity(i1);
                break;
            case R.id.Register:
                registeruser();
                break;
            case R.id.alreadyRegistered:
                Intent i2 = new Intent(getApplicationContext(), MFUserLoginActivity.class);
                startActivity(i2);
                break;
        }
    }
    //----------> onClick methods switch

    //MAP METHODS$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    @SuppressLint("MissingPermission")
    public void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        c++;

        if (c==1)
        {
            Toasty.info(this, "Your location is fetched. Thanks\nNow we are able to provide you better experience", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "LAT: "+latitude+" LONG: "+longitude, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

    //CHECK INTERNET CONENCTION METHOD
    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public boolean isNetworkAvailablePlusoConnencted(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    //REGISTRATION FULL FUNCTION WITH AUTHENTICATION----------------------------------------------------------------
    private void registeruser()
    {

        if (isNetworkAvailable(getApplicationContext())) {
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            // code here
        } else {
            // code
            Toasty.error(getApplicationContext(), "No Connection To the Internet\nPlease Connect to the Internet", Toast.LENGTH_SHORT, true).show();
        }

        //
        int c=0;
        final int[] user_exists_counter = { 0 };

        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm_password = editTextConfirmPassword.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();

        if (fullName.isEmpty()) {
            c++;
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            c++;
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            c++;
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            c++;
            editTextPassword.setError("Min password length should be 8 characters!");
            editTextPassword.requestFocus();
            return;
        }
        if (confirm_password.isEmpty()) {
            c++;
            editTextConfirmPassword.setError("Please Confirm Your Password!");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if ((!confirm_password.isEmpty()) && (!confirm_password.equals(password))) {
            c++;
            //Toast.makeText(this, "password: "+password+" confirm password: "+confirm_password,Toast.LENGTH_SHORT).show();
            editTextConfirmPassword.setError("Confirm Password must be same as provided Password!");
            editTextConfirmPassword.requestFocus();
            return;
        }
       // if (latitude == 0 || longitude == 0) {
           // c++;
           // Toast.makeText(this, "Please Provide Your Location", Toast.LENGTH_SHORT).show();
            //f = 0;
           // userlocation.requestFocus();
          //  return;
        //}
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            c++;
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
        }

        //if(c==0) //all above conditions check
        //{
        progressBar.setVisibility(View.VISIBLE);
        pgbartxt.setVisibility(View.VISIBLE);
        // }

        //ALREADY USER EXISTS###############################################################
        dbroot.collection("MarqueeFinders")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                user_exists_counter[0] += 1;
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        //add Data to firestore if user doesn't already exists
                        if (user_exists_counter[0] == 0)
                        {
                            //Toast.makeText(getApplicationContext(), "if > counter: " + user_exists_counter[0], Toast.LENGTH_SHORT).show();

                            //UTILITY_VARIABLES [i.e., USER REG. DATA]---->fullName, email, password, [confirm_password], contact, latitude, longitude

                            mAuth.createUserWithEmailAndPassword(email, password) //add user inside authentication feature of firebase
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                items.put("name", fullName);
                                                items.put("email", email);
                                                // items.put("userpassword", password);
                                                items.put("contact", contact);
                                                progressBar.setVisibility(View.VISIBLE);

                                                //adduserdata [register user]
                                                dbroot.collection("MarqueeFinders").document(email)
                                                        .set(items)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                                        {
                                                            @Override
                                                            public void onSuccess(Void unused)
                                                            {
                                                                progressBar.setVisibility(View.GONE);
                                                                pgbartxt.setVisibility(View.GONE);
                                                                Toasty.success(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Registration Un-Successful due to some technical reasons!", e);
                                                                Toasty.error(getApplicationContext(), "Registration Un-Successful", Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.GONE);
                                                                pgbartxt.setVisibility(View.GONE);
                                                            }
                                                        });
                                            }
                                        }
                                    });
                            //
                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Intent tologin = new Intent(getApplicationContext(), MFUserLoginActivity.class);
                                    startActivity(tologin);

                                    progressBar.setVisibility(View.GONE);
                                    pgbartxt.setVisibility(View.GONE);
                                }
                            }, 5000);
                            //
                        }//already user exists condition end
                        else if (user_exists_counter[0] >= 1)
                        {
                            //Toast.makeText(getApplicationContext(), "already: " + user_exists_counter[0], Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            pgbartxt.setVisibility(View.GONE);
                            Toasty.error(getApplicationContext(), "Already exists! try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        user_exists_counter[0] = 0;
    }//user reg function end-----------------------------------------------------------------------------------------------------
}//main end