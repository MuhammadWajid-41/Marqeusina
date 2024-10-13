package com.example.Marqeusina;


        import static android.content.ContentValues.TAG;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;

        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
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

public class RegMO extends AppCompatActivity implements View.OnClickListener, LocationListener
{
    public String userType="";
    //location message checker
    public int c = 0;
    //

    public Number hall_price_conv=0;

    private TextView banner, alreadyregistered, pgbartxt;
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextContact, hall_price,services, capacity;
    private Button userlocation, registeruser;
    private ProgressBar progressBar;//user regestraion process bar

    private FirebaseAuth mAuth;

    //PSGbar
    ProgressDialog progressDialog; //location fetch progress bar
    private int progressStatus = 0;
    private Handler handler = new Handler();

    //PERMISSONS
    LocationManager locationManager;
    public double longitude=0.0, latitude=0.0;
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
        setContentView(R.layout.activity_reg_mo);




        //PSGBAR
        progressDialog = new ProgressDialog(this);
        //

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


//location###########################################################################################################

        userlocation = (Button) findViewById(R.id.UserLocation); //get user's location
        userlocation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                //permissons asking////////////////////////////////////////////////
                // check condition
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission
                                .ACCESS_FINE_LOCATION)
                        == PackageManager
                        .PERMISSION_GRANTED)
                {
                    // When permission is granted
                    // Call method
                    getLocation();
                }
                else {
                    // When permission is not granted
                    // Call method
                    requestPermissions(
                            new String[] {
                                    Manifest.permission
                                            .ACCESS_FINE_LOCATION},
                            100);
                }
                ///////////////////////////////////////////////////////////////////

                f += 1;//exception handling [CHECKER]

                if(f==1 && ContextCompat.checkSelfPermission(RegMO.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)//exception handling...
                {
                    if(longitude == 0.0 && latitude == 0.0)
                    {
                        getLocation();
                        showProgressDialogWithTitle("System", "fetching location...");
                    }
                    else
                        hideProgressDialogWithTitle();
                }
            }
        });

        //#############################################################################################################

        editTextFullName = (EditText) findViewById(R.id.UserName);
        editTextEmail = (EditText) findViewById(R.id.Email);
        editTextPassword = (EditText) findViewById(R.id.Password);
        editTextConfirmPassword = (EditText) findViewById(R.id.ConfirmPassword);
        editTextContact = (EditText) findViewById(R.id.Contact);
        hall_price = (EditText) findViewById(R.id.hall_price);
        services = (EditText) findViewById(R.id.services);
        capacity = (EditText) findViewById(R.id.capacity);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }//onCreate end

    //----------> onClick methods switch
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appname:
                //startActivity(new Intent(this, UserLoginActivity.class));
                Intent i1 = new Intent(getApplicationContext(), MOUserLoginActivity.class);
                startActivity(i1);
                break;
            case R.id.Register:
                registeruser();
                break;
            case R.id.alreadyRegistered:
                Intent i2 = new Intent(getApplicationContext(), MOUserLoginActivity.class);
                startActivity(i2);
                break;
        }
    }
    //----------> onClick methods switch

    //PSGBARS
    // Method to show Progress bar###########################################################################
    private void showProgressDialogWithTitle(String title,String substring) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();

        // Start Process Operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    try{
                        // This is mock thread using sleep to show progress
                        Thread.sleep(540);
                        progressStatus += 5;
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    // Change percentage in the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            progressDialog.setProgress(progressStatus);
                        }
                    });
                }
                //hide Progressbar after finishing process
                hideProgressDialogWithTitle();
            }
        }).start();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }
    //############################################################################################################

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
        longitude=location.getLongitude();
        latitude=location.getLatitude();

        if (c==1)
        {
            Toasty.success(this, "Your location is fetched. Thanks\nNow we are able to provide you better experience", Toast.LENGTH_SHORT).show();
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

    //REGISTRATION FULL FUNCTION WITH AUTHENTICATION----------------------------------------------------------------
    private void registeruser()
    {
        //
        int c=0;
        final int[] user_exists_counter = { 0 };

        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm_password = editTextConfirmPassword.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();
        String only_hall_price = hall_price.getText().toString().trim();
        String hall_services = services.getText().toString().trim();
        String hall_capacity = capacity.getText().toString().trim();

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
        if (only_hall_price.isEmpty()) {
            c++;
            hall_price.setError("Please Enter Hall Booking Price here!");
            hall_price.requestFocus();
            return;
        }
        if (hall_services.isEmpty()) {
            c++;
            services.setError("Please Enter Marquee's Max. Capacity!");
            services.requestFocus();
            return;
        }
        if (hall_capacity.isEmpty()) {
            c++;
            capacity.setError("Please Enter Marquee's Max. Capacity!");
            capacity.requestFocus();
            return;
        }
        if ((!confirm_password.isEmpty()) && (!confirm_password.equals(password))) {
            c++;
            //Toast.makeText(this, "password: "+password+" confirm password: "+confirm_password,Toast.LENGTH_SHORT).show();
            editTextConfirmPassword.setError("Confirm Password must be same as provided Password!");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (latitude == 0 || longitude == 0) {
            c++;
            Toasty.error(this, "Please Provide Your Location", Toast.LENGTH_SHORT).show();
            //f = 0;
            userlocation.requestFocus();
            return;
        }
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
        dbroot.collection("MarqueeOwners")
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
                                                if(latitude != 0 && longitude != 0 )
                                                {
                                                    items.put("latitude", latitude);
                                                    items.put("longitude", longitude);
                                                }
                                                hall_price_conv = Integer.parseInt(only_hall_price);
                                                items.put("hall_price", hall_price_conv);
                                                items.put("purl", "");
                                                items.put("ratings", "0");
                                                items.put("services", hall_services);
                                                items.put("capacity", hall_capacity);
                                                items.put("image0", "");
                                                items.put("image1", "");
                                                items.put("image2", "");
                                                progressBar.setVisibility(View.VISIBLE);

                                                //adduserdata [register user]
                                                dbroot.collection("MarqueeOwners").document(email)
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
                                    Intent tologin = new Intent(getApplicationContext(), MOUserLoginActivity.class);
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