package com.example.Marqeusina;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homefragmentMOS#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homefragmentMOS extends Fragment {

    public TextView t1;

    //get marquee's email through shared pref.
    public SharedPreferences marquee_info;
    public String mEmail="";

    //FOR USER COMMENTS ON MARQUEES
    RecyclerView crecview;
    ArrayList<cmodel> cdatalist;
    cmyadapter cadapter;
    //

    FirebaseFirestore dbroot;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homefragmentMOS() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homefragmentMOS.
     */
    // TODO: Rename and change types and number of parameters
    public static homefragmentMOS newInstance(String param1, String param2) {
        homefragmentMOS fragment = new homefragmentMOS();
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
        View view = inflater.inflate(R.layout.fragment_homem_o_s, container, false);
        dbroot = FirebaseFirestore.getInstance();

        t1 = view.findViewById(R.id.t1);
        //--->Store shared pref.
        marquee_info=(SharedPreferences)this.getActivity().getSharedPreferences("marquee_data",MODE_PRIVATE);

        if(!marquee_info.getString("marquee_email","").isEmpty())
        {
            mEmail =  marquee_info.getString("marquee_email","");
        }

        //////////////////////////////////FOR USER COMMENTS ON MARQUEES ///////////////////////////////////

        crecview=(RecyclerView)view.findViewById(R.id.recview);
        crecview.setLayoutManager(new LinearLayoutManager(getContext()));
        cdatalist=new ArrayList<>();
        cadapter=new cmyadapter(cdatalist);
        crecview.setAdapter(cadapter);
        //

        //Qeury
        //// Reference to a document in subcollection "messages"
        if (!mEmail.isEmpty())
        {
            //Qeury
            //// Reference to a document in subcollection "messages"
            Task<QuerySnapshot> document = dbroot.collection("Comments").document(mEmail).collection("user_comments")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot document: queryDocumentSnapshots)
                            {
                                //Toast.makeText(getContext(), "comment: "+document.getString("comment"), Toast.LENGTH_SHORT).show();
                                //Muser_comments.append("User name: "+ document.getString("username")+" Comment: "+document.getString("comment")+"\n");
                                cmodel obj = document.toObject(cmodel.class);
                                cdatalist.add(obj);
                            }
                            cadapter.notifyDataSetChanged();
                            if (cdatalist.isEmpty())
                            {
                                t1.setText("No Comments Yet!");
                            }
                        }
                    });
            cdatalist.clear();
            cadapter.notifyDataSetChanged();
        }

        return  view;
    }//on create end
}