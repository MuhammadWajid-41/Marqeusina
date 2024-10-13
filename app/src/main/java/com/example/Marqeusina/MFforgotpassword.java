package com.example.Marqeusina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class MFforgotpassword extends AppCompatActivity
{
    private EditText emailEditText;
    public TextView login;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mfforgotpassword);


        login = (TextView) findViewById (R.id.login);
        emailEditText = (EditText) findViewById (R.id.Email);
        resetPasswordButton = (Button) findViewById (R.id.resetpassword);
        progressBar = (ProgressBar) findViewById (R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mos = new Intent(MFforgotpassword.this, MFUserLoginActivity.class);
                startActivity(mos);
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                resetPassword();
            }
        });

    }//on create method() end

    private void resetPassword()
    {
        int c=0;

        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty())
        {
            c++;
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            c++;
            emailEditText.setError("Please provide valid email!");
            emailEditText.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);

        //send reset link to mail
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toasty.success(MFforgotpassword.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    Toasty.error(MFforgotpassword.this, "Try again! Something wrong happened!", Toast.LENGTH_LONG).show();
                }
            }
        });
        //
    }//reset() end

}//main method end