package com.example.socialapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.socialapp.R;
import com.example.socialapp.models.PostDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragement extends Fragment {

    View myFragmentView;
    //    Typeface type;
    ListView searchResults;
    TextView username_;
    TextView first_name;
    TextView last_name;
    TextView email_id;
//    TextView phone_number;
    String current_username;
    List<UserDetails> userinfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView =  inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        current_username = pref.getString("username","None");

        try {
            userinfo = new GetUserInfo(getContext()).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        username_ = (TextView) myFragmentView.findViewById(R.id.show_username);
        first_name = (TextView) myFragmentView.findViewById(R.id.show_firstname);
        last_name = (TextView) myFragmentView.findViewById(R.id.show_lastname);
        email_id = (TextView) myFragmentView.findViewById(R.id.show_email);

        username_.setText(userinfo.get(0).getUserName());
        first_name.setText(userinfo.get(0).getFirstName());
        last_name.setText(userinfo.get(0).getLastName());
        email_id.setText(userinfo.get(0).getEmailId());



//        new GetMy_posts(getContext()).execute();

        return  myFragmentView;
    }

    class GetUserInfo extends AsyncTask<Void, Void, List<UserDetails>>{
        Context c;

        public GetUserInfo(Context x){c=x;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<UserDetails> userDetails) {
            if (userDetails!=null){
                Log.d("Got user", " Information");
                userinfo = userDetails;
            }
            Log.d("Got user", " can not get Information");
        }

        @Override
        protected List<UserDetails> doInBackground(Void... voids) {
            Call<List<UserDetails>> call_ = Api.getClient().getProfile(current_username);

            try {
                Response<List<UserDetails>> resp = call_.execute();
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }
    }

    class GetMy_posts extends AsyncTask<Void, Void, List<PostDetails>>{
        Context c;

        public GetMy_posts(Context ctx){c=ctx;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<PostDetails> postDetails) {
            super.onPostExecute(postDetails);
        }

        @Override
        protected List<PostDetails> doInBackground(Void... voids) {
            return null;
        }
    }

}
