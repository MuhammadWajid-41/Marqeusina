package com.example.Marqeusina;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiUpload#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiUpload extends Fragment {

    StorageReference ref;
    List<String> files,status;
    RecyclerView recview;
    ImageView btn_upload;
    MultiPicmyadapter adapter;

    //
    private Uri ImageUri;
    ArrayList ImageList;
    private int upload_count = 0;
    private ProgressDialog progressDialog;
    ArrayList urlStrings;
    public HashMap<String, Object> hashMap = new HashMap<>();

    //get marquee's email through shared pref.
    public SharedPreferences marquee_info;
    public String mEmail="";

    public ImageSlider mainslider;
    FirebaseFirestore dbroot;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MultiUpload() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultiUpload.
     */
    // TODO: Rename and change types and number of parameters
    public static MultiUpload newInstance(String param1, String param2) {
        MultiUpload fragment = new MultiUpload();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_upload, container, false);

        Toasty.info(getContext(),"3 images allowed only",Toasty.LENGTH_LONG).show();

        ImageList = new ArrayList();

        dbroot = FirebaseFirestore.getInstance();
        ref= FirebaseStorage.getInstance().getReference();

        files=new ArrayList<>();
        status=new ArrayList<>();

        btn_upload=(ImageView)view.findViewById(R.id.btn_upload);

        recview=(RecyclerView)view.findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MultiPicmyadapter(files,status);
        recview.setAdapter(adapter);

        //--->Store shared pref.
        marquee_info=(SharedPreferences)this.getActivity().getSharedPreferences("marquee_data",MODE_PRIVATE);

        if(!marquee_info.getString("marquee_email","").isEmpty())
        {
            mEmail =  marquee_info.getString("marquee_email","");
            //Toast.makeText(getContext(), "marquee_email is: "+mEmail, Toast.LENGTH_SHORT).show();
        }

        //*******************************************SLIDE FUNCTION CALL************************************************************

        mainslider=(ImageSlider)view.findViewById(R.id.image_slider);

        /////////////////////////////////////////////////PKG-DELETE//////////////////////////////////////////////
        dbroot.collection("MarqueeOwners")
                .whereEqualTo("email",mEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() > 0)
                            {
                                for (DocumentSnapshot d : task.getResult())
                                {
                                    if (d.exists())
                                    {
                                        if(!d.getString("image0").equals("") && !d.getString("image1").equals("") && !d.getString("image2").equals(""))
                                        {
                                            showMarqueeImages();
                                        }
                                    }
                                }
                            }
                            else if(task.getResult().size() <= 0)
                            {
                                Toasty.error(getContext(), "No Images to show!", Toast.LENGTH_SHORT).show();

                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error", task.getException());
                            Toasty.error(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //*******************************************************************************************************

        //######################################################UPLOAD_BTN_###################################################
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent();
                                intent.setType("image/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"Please Select Multiple Files"),101);
                                //Toast.makeText(getContext(), "permissions given",LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                //Toast.makeText(getContext(), "permissions Denied!",LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                                //Toast.makeText(getContext(), "onPermissionRationaleShouldBeShown",LENGTH_SHORT).show();
                            }
                        }).check();

            }
        });
        //###################################################################################################################

        return  view;
    }//on create end

    //--------------------------------------------SLIDE METHOD-------------------------------------------------//
    public void showMarqueeImages()
    {
        final List<SlideModel> remoteimages=new ArrayList<>();

        dbroot.collection("MarqueeOwners")
                .whereEqualTo("email",mEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list)
                        {
                            if (d.exists())
                            {
                                if(!d.getString("image0").equals(""))
                                {
                                    remoteimages.add(new SlideModel(d.getString("image0").toString().trim(), ScaleTypes.FIT));
                                }
                                if(!d.getString("image1").equals(""))
                                {
                                    remoteimages.add(new SlideModel(d.getString("image1").toString().trim(), ScaleTypes.FIT));
                                }
                                if(!d.getString("image2").equals(""))
                                {
                                    remoteimages.add(new SlideModel(d.getString("image0").toString().trim(), ScaleTypes.FIT));
                                }

                                if(!remoteimages.isEmpty())
                                {
                                    mainslider.setImageList(remoteimages,ScaleTypes.FIT);
                                    mainslider.setItemClickListener(new ItemClickListener() {
                                        @Override
                                        public void onItemSelected(int i) {
                                            //Toast.makeText(getContext(),remoteimages.get(i).getTitle().toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failure due to technical issues!");
                    }
                });
    }
    //--------------------------------------------SLIDE METHOD END-------------------------------------------------//


    //method1(uploaderBtn)

    public void setImageUrls() {

        urlStrings = new ArrayList<>();

        for (upload_count = 0; upload_count < ImageList.size(); upload_count++) {

            Uri IndividualImage = (Uri) ImageList.get(upload_count);
            String filename=getfilenamefromuri(IndividualImage);
            files.add(filename);
            status.add("loading");
            adapter.notifyDataSetChanged();

            final int index=upload_count;

            StorageReference ImageFolder=ref.child("/multiuploads");

            final StorageReference ImageName = ImageFolder.child(filename);

            final int pos=upload_count;

            ImageName.putFile(IndividualImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageName.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            urlStrings.add(String.valueOf(uri));
                                            //Toast.makeText(getContext(), "picDone",LENGTH_SHORT).show();
                                            status.remove(index);
                                            status.add(index,"done");
                                            adapter.notifyDataSetChanged();

                                            //Toast.makeText(MainActivity.this, "OnSuccess setImageUrls()", Toast.LENGTH_SHORT).show();
                                            if (urlStrings.size() == ImageList.size()) {
                                                storeLink(urlStrings);
                                            }

                                        }
                                    }
                            );
                        }

                    }
            );
        }
    }
    //method2 -----> Store Links To Firebase Firestore
    public void storeLink(ArrayList<String> urlStrings) {

        HashMap<String, String> hashMap = new HashMap<>();

        for (int i = 0; i < urlStrings.size(); i++) {
            hashMap.put("image" + i, urlStrings.get(i));
        }

        //qeury
        dbroot.collection("MarqueeOwners").document(mEmail).set(hashMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        showMarqueeImages();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        ImageList.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101 && resultCode==RESULT_OK)
        {
            if(data.getClipData()!=null)
            {
                int countClipData = data.getClipData().getItemCount();
                int currentImageSlect = 0;

                while (currentImageSlect < countClipData) {

                    ImageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                    ImageList.add(ImageUri);
                    currentImageSlect = currentImageSlect + 1;
                }
                setImageUrls();
            }
            else if (data.getClipData() == null) {
                Toast.makeText(getContext(), "No images selected", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getContext(), "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    public String getfilenamefromuri(Uri filepath)
    {
        String result = null;
        if (filepath.getScheme().equals("content")) {
            ContentResolver cr = getContext().getContentResolver();
            Cursor cursor = cr.query(filepath, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = filepath.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}//on main end