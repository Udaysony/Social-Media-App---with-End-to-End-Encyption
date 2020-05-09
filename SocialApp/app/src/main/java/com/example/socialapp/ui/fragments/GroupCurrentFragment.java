package com.example.socialapp.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.ui.adapter.GroupCurrentAdapter;
import com.example.socialapp.ui.adapter.GroupSearchAdapter;
import com.example.socialapp.utils.Security_Key_message;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Response;

public class GroupCurrentFragment extends Fragment {

    View myFragementView;
    ListView currentGroups;
    Button new_group_create;
    private String m_Text = "";
    private String current_username;


    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final HomeScreen
//        type = Typeface.createFromAsset(activity.getAssets(), "fonts/book.TTF");
        myFragementView = inflater.inflate(R.layout.fragment_current_groups, container, false);
        currentGroups = (ListView) myFragementView.findViewById(R.id.listview_current_group);
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);


        current_username = pref.getString("username","None");

        new_group_create = (Button)myFragementView.findViewById(R.id.create_group_button);
        new_group_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Title");

                // Set up the input
                final EditText input = new EditText(getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        // Call create Group method
                        new CreateGroup(getContext()).execute();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        String searchFor = pref.getString("username", "None");

        Log.d("Current username" , searchFor);
        new GetCurrentGroups(getContext()).execute(searchFor);
        return myFragementView;
    }


    class GetCurrentGroups extends AsyncTask<String, Void, List<GroupDetails>>{

        Context context;
        ProgressDialog pd;
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);



        public GetCurrentGroups (Context ctx){context=ctx;}

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Searching...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

        }

        @Override
        protected void onPostExecute(List<GroupDetails> userDetails) {
            pd.dismiss();
            if(userDetails != null){
                currentGroups.setAdapter(new GroupCurrentAdapter(getActivity(),1,userDetails ));
            }
            else{
                Log.d("CurrentGroups", "Post Execute is NULL");
            }
        }
        @Override
        protected List<GroupDetails> doInBackground(String... params) {
            Call<List<GroupDetails>> call_ = Api.getClient().getCurrentGroups(params[0]);
            try {
                Response<List<GroupDetails>> resp = call_.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    Log.d("GetCurrentGroups_success", "yes");
                    return resp.body();
                }
                else{
                    Log.d("GetCurrentGroups Background", "Something wrong with response");
                    return null;
                }
            } catch (IOException e) {
                Log.d("GetCurrentGroups_Exception in Background", e.toString());
                e.printStackTrace();
                return null;
            }
        }
    }

    class CreateGroup extends AsyncTask<Void, Void, String>{
        Context ctx;
        ProgressDialog pd;
        Security_Key_message key_helper = new Security_Key_message();
        private String group_key = "";
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        public CreateGroup (Context x){ctx=x;}

        @Override
        protected void onPreExecute() {

            byte[] gk = new byte[0];
            String pk = "";
            byte[] pk_bytes = new byte[0];
            try{
                gk = key_helper.getGroupKey();

                pk = pref.getString("RSAPublicKey", "none");
                pk_bytes = Base64.getDecoder().decode(pk);

                PublicKey publicKey =
                        KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pk_bytes));

                group_key = key_helper.encrypt_With_publicKey(gk,publicKey);
                Log.d("Group Create" , " Successfully Generated Key");
            }catch(Exception e){
                Toast.makeText(getContext(), "Something went wrong in Group Key generartion ", Toast.LENGTH_LONG);
            }
            Log.d("Creat Group", "Preparing data");

            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Creating Group...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if(s.equals("ok")){
                Log.d("Create Group", "Success");
                Toast.makeText(getContext()," Group " + m_Text + " Created", Toast.LENGTH_SHORT);
            }
            Log.d("Create Group", "Success");

        }

        @Override
        protected String doInBackground(Void... strings) {
            Call<String> call_ = Api.getClient().CreateGroup(current_username, m_Text, group_key);
            try {
                Response<String> resp = call_.execute();
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Create Group"," Exception ");
            }
            Log.d("Create Group ", "out");
            return null;
        }
    }
}
