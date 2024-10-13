package com.example.Marqeusina;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mfprofileupdate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mfprofileupdate extends Fragment {


    //get marquee's email through shared pref.
    public SharedPreferences user_info;
    public String uEmail="";


    public String username = "";
    public String usercontact = "";
    //
    Map<String,Object> user_purl_link = new HashMap<>();
    //

    //widgets
    private Button selectBtn, uploadBtn, update_account;
    private ImageView imageView;
    private ProgressBar progressBar;
    private EditText UserName, UserContact;

    public TextView for_error_show;
    //vars
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    FirebaseFirestore dbroot;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mfprofileupdate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment callfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mfprofileupdate newInstance(String param1, String param2) {
        mfprofileupdate fragment = new mfprofileupdate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mfprofileupdate, container, false);

        //for_error_show
        for_error_show = view.findViewById(R.id.for_error_show);

        dbroot = FirebaseFirestore.getInstance();

        //--->Store shared pref.
        user_info=(SharedPreferences)this.getActivity().getSharedPreferences("user_data", MODE_PRIVATE);

        if(!user_info.getString("user_email","").isEmpty())
        {
            uEmail =  user_info.getString("user_email","");
        }

        selectBtn = view.findViewById(R.id.select_btn);
        uploadBtn = view.findViewById(R.id.upload_btn);
        progressBar = view.findViewById(R.id.progressBar);
        imageView = view.findViewById(R.id.imageView);

        //------->update user info
        UserName = (EditText)view.findViewById(R.id.UserName);
        UserContact = (EditText)view.findViewById(R.id.UserContact);
        update_account = (Button)view.findViewById(R.id.update_account);

        //set onclick listener
        update_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = UserName.getText().toString().trim();
                usercontact = UserContact.getText().toString().trim();

                if(username.isEmpty() && usercontact.isEmpty())
                {
                    for_error_show.setError("Please fill any of the below fields in order to update!");
                    for_error_show.requestFocus();
                    return;
                }
                else
                {
                    for_error_show.setError(null);
                    for_error_show.clearFocus();

                    if(!username.isEmpty())
                    {
                        user_purl_link.put("name",UserName.getText().toString().trim());
                    }
                    if(!usercontact.isEmpty())
                    {
                        user_purl_link.put("contact",UserContact.getText().toString().trim());
                    }


                    if(!uEmail.isEmpty())
                    {
                        dbroot.collection("MarqueeFinders").document(uEmail).set(user_purl_link, SetOptions.merge());

                        UserName.setText("");
                        UserContact.setText("");
                        Toasty.success(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toasty.error(getContext(), "!!!Error occurred while updating!!!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        progressBar.setVisibility(View.INVISIBLE);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    uploadToFirebase(imageUri);
                }else{
                    Toasty.info(getContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }//on createView end

    //-------------------------------------------------------METHODS---------------------------------------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadToFirebase(Uri uri){

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //Model model = new Model(uri.toString());
                        //String modelId = root.push().getKey();
                        //root.child(modelId).setValue(model);

                        //add uploaded picture link to firestore [take suername {document} from shared pref.]
                        String Upurl = uri.toString().trim();
                        user_purl_link.put("purl",Upurl);

                        if(!user_purl_link.isEmpty())
                        {
                            dbroot.collection("MarqueeFinders").document(uEmail).set(user_purl_link, SetOptions.merge());
                        }
                        //

                        progressBar.setVisibility(View.INVISIBLE);
                        Toasty.success(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();


                        //imageView.setImageResource(R.drawable.ic_baseline_verified_user_24);

                        //set image
                        RequestOptions requestOptions = new RequestOptions();
                        Glide.with(getContext()).load(Upurl)
                                .apply(requestOptions).thumbnail(0.5f).into(imageView);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toasty.error(getContext(), "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    //-----------------------------------------------------------------------------------------------------------------------//

}//main end