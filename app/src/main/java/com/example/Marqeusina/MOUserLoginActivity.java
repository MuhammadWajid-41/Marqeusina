package com.example.Marqeusina;


        import static android.content.ContentValues.TAG;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
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

public class MOUserLoginActivity extends AppCompatActivity implements View.OnClickListener
{
    //shared preferences to remember login
    public SharedPreferences s;
    public SharedPreferences.Editor se;

    public SharedPreferences marquee_info;
    public SharedPreferences.Editor marquee_info_editor;

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
        setContentView(R.layout.activity_mouser_login);

        dbroot = FirebaseFirestore.getInstance();

        //checkbox
        ch=(CheckBox)findViewById(R.id.ch1);
        //shared pref.
        s=(SharedPreferences)getSharedPreferences("marquees_data",MODE_PRIVATE);
        se=s.edit();

        //--->Store shared pref.
        marquee_info=(SharedPreferences)getSharedPreferences("marquee_data",MODE_PRIVATE);
        marquee_info_editor=marquee_info.edit();

        editTextEmail = (EditText) findViewById (R.id.Email);
        editTextPassword = (EditText) findViewById (R.id.Password);

        if(!s.getString("marquees_email","").isEmpty() && !s.getString("marquees_password","").isEmpty())
        {
            editTextEmail.setText(s.getString("marquees_email",""));
            editTextPassword.setText(s.getString("marquees_password",""));

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
                startActivity(new Intent(this, RegMO.class));
                break;
            case R.id.Login:
                userLogin();
                break;
            case R.id.ForgotPassword:
                startActivity(new Intent( this, MOforgotpassword.class));
                break;
        }
    }

    private void userLogin()
    {
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
            marquee_info_editor.putString("marquee_email", editTextEmail.getText().toString().trim());
            marquee_info_editor.apply();

            if(ch.isChecked())
            {
               // Toast.makeText(this, "checked", Toast.LENGTH_SHORT).show();
                se.putString("marquees_email", editTextEmail.getText().toString().trim());
                se.putString("marquees_password", editTextPassword.getText().toString().trim());
                se.apply();
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
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                        }
                                        Toasty.success(MOUserLoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Log.d(TAG, "No such user!", task.getException());
                                    }
                                    //********************************************************************************************

                                    startActivity(new Intent(MOUserLoginActivity.this, StartMarqueeOwnerSide.class));
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toasty.error(MOUserLoginActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        //

    }//user login function end

}//end main