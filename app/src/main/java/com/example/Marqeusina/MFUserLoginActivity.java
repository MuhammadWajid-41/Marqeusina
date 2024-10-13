package com.example.Marqeusina;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.widget.CheckBox;
import android.content.SharedPreferences;

import es.dmoral.toasty.Toasty;

public class MFUserLoginActivity extends AppCompatActivity implements View.OnClickListener
{
    //shared preferences to remember login
    public SharedPreferences mfs;
    public SharedPreferences.Editor mfse;

    public SharedPreferences user_info;
    public SharedPreferences.Editor user_info_editor;

    CheckBox ch;  //----->[checkbox]
    //

    private TextView register, forgotpassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    FirebaseFirestore dbroot;

    @Override
    protected void onCreate(Bundle savedInstancestate)
    {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_mfuser_login);

        dbroot = FirebaseFirestore.getInstance();

        //checkbox
        ch=(CheckBox)findViewById(R.id.ch1);
        //shared pref.
        mfs=(SharedPreferences)getSharedPreferences("users_data",MODE_PRIVATE);
        mfse=mfs.edit();

        //--->Store shared pref.
        user_info=(SharedPreferences)getSharedPreferences("user_data",MODE_PRIVATE);
        user_info_editor=user_info.edit();

        editTextEmail = (EditText) findViewById (R.id.Email);
        editTextPassword = (EditText) findViewById (R.id.Password);

        if(!mfs.getString("users_email","").isEmpty() && !mfs.getString("users_password","").isEmpty())
        {
            editTextEmail.setText(mfs.getString("users_email",""));
            editTextPassword.setText(mfs.getString("users_password",""));

            ch.setChecked(true);
        }

        /**/

        register = (TextView) findViewById(R.id.Register);
        register.setOnClickListener(this);

        forgotpassword = (TextView) findViewById(R.id.ForgotPassword);
        forgotpassword.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.Login); //login button
        signIn.setOnClickListener(this);

        //pgbar
        progressBar = (ProgressBar) findViewById (R.id.progressBar);
        //

        //initialize mAuth
        mAuth = FirebaseAuth.getInstance();
        //

    }//end onCreate Method()

    //switch to manage all onClicks()
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.Register:
                startActivity(new Intent(this, RegMF.class));
                break;
            case R.id.Login:
                userLogin();
                break;
            case R.id.ForgotPassword:
                startActivity(new Intent( this, MFforgotpassword.class));
                break;
        }
    }

    //CHECK INTERNET CONENCTION METHOD
    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public boolean isNetworkAvailablePlusoConnencted(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void userLogin()
    {

        if (isNetworkAvailable(getApplicationContext())) {
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            // code here
        } else {
            // code
            Toasty.error(getApplicationContext(), "No Connection To the Internet\nPlease Connect to the Internet", Toast.LENGTH_SHORT, true).show();
        }

        int c=0;

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            c++;
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            c++;
            editTextEmail.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            c++;
            editTextPassword.setError("Min password length should be 8 characters!");
            editTextPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            c++;
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
        }

        if(c==0) //all above conditions check
        {
            //shared pref. storage username & passwrod

            //store maruqee's info in shared pref here
            user_info_editor.putString("user_email", editTextEmail.getText().toString().trim());
            user_info_editor.apply();

            if(ch.isChecked())
            {
                //Toast.makeText(this, "checked", Toast.LENGTH_SHORT).show();
                mfse.putString("users_email", editTextEmail.getText().toString().trim());
                mfse.putString("users_password", editTextPassword.getText().toString().trim());
                mfse.apply();
            }
            //
            progressBar.setVisibility(View.VISIBLE);
            //pgbartxt.setVisibility(View.VISIBLE);
        }

        //AUTH...
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    // redirect to user [HomepageMFS]
                    progressBar.setVisibility(View.GONE);

                    //exception handling login check**************************************************************
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
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                        }
                                       // Toast.makeText(MFUserLoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                        Toasty.success(MFUserLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT, true).show();
                                    }
                                    else
                                    {
                                        Toasty.error(MFUserLoginActivity.this, "No such user!", Toast.LENGTH_SHORT, true).show();
                                        Log.d(TAG, "No such user!", task.getException());
                                    }
                                    //********************************************************************************************

                                    startActivity(new Intent(MFUserLoginActivity.this, StartMarqueeFinderSide.class));
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toasty.error(MFUserLoginActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        //

    }//user login function end

}//end main