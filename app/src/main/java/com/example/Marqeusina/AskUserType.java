package com.example.Marqeusina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

public class AskUserType extends AppCompatActivity {

    public Button marquee_owner, marquee_finder;
    public ImageView insta, fb, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_user_type);

        marquee_owner = (Button) findViewById(R.id.goto_marquee_owner_side);
        marquee_finder = (Button) findViewById(R.id.goto_marquee_finder_side);

        insta = findViewById(R.id.insta);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/invites/contact/?i=1lwzjftpdza&utm_content=3p6bmbl"));
                startActivity(intent);
            }
        });

        fb = findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/NunOp-109403688341021/"));
                    startActivity(intent);
            }
        });

        phone = findViewById(R.id.phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:03015365633"));
                startActivity(intent);
            }
        });

        marquee_owner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent mos = new Intent(AskUserType.this, RegMO.class);
                startActivity(mos);
            }
        });

        marquee_finder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent mfs = new Intent(AskUserType.this, RegMF.class);
                startActivity(mfs);
            }
        });

    }//create view end

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/NunOp-109403688341021/"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/wajid.raees.100"));
        }
    }

}//main end