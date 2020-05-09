package com.example.socialapp.ui.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.socialapp.R;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.ui.adapter.FriendsCurrentAdapter;
import com.example.socialapp.ui.adapter.FriendsSearchAdapter;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Response;

public class FriendsCurrentFragment extends Fragment {

    View myFragementView;
    SearchView search;
    //    Typeface type;
    ListView currentFreinds;
    String found = "N";
    private int grp_id;

    ArrayList<UserDetails> FriendResults = new ArrayList<UserDetails>();

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final HomeScreen
//        type = Typeface.createFromAsset(activity.getAssets(), "fonts/book.TTF");
        myFragementView = inflater.inflate(R.layout.fragment_current_friends_display, container, false);
        currentFreinds = (ListView)myFragementView.findViewById(R.id.listview_current_friends);

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String searchFor = pref.getString("username", "None");
        Log.d(searchFor, searchFor + "Friends");
        new GetCurrentFriends(getContext()).execute(searchFor, searchFor + "Friends");

        return myFragementView;
    }




    class GetCurrentFriends extends AsyncTask<String, Void, List<GroupDetails>>{

        Context context;
        GroupDetails userDetails = new GroupDetails();
        ProgressDialog pd;



        public GetCurrentFriends (Context ctx){context=ctx;}

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
            super.onPostExecute(userDetails);

            if(userDetails != null){
                currentFreinds.setAdapter(new FriendsCurrentAdapter(getActivity(),1,userDetails ));
                pd.dismiss();
            }
            else{
                Log.d("TAG", "Post Execute is NULL");
                pd.dismiss();
            }
        }
        @Override
        protected List<GroupDetails> doInBackground(String... params) {
            List<GroupDetails> gps = new ArrayList<GroupDetails>();
            Call<List<GroupDetails>> callFriends = Api.getClient().getCurrentFriends(params[0], params[1]);
            try {
                Response<List<GroupDetails>> resp = callFriends.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    Log.d("GetCurrentFriends_success", "none");
                    gps =  resp.body();
                    return gps;
                }
                else{
                    Log.d("GetCurrentFriends_in Background", "Something wrong with response");
                    return gps;
                }
            } catch (IOException e) {
                Log.d("GetCurrentFriends_Exception in Background", e.toString());
                e.printStackTrace();
                return gps;
            }
        }
    }




    class GetFriendsAPI extends AsyncTask<String, Void , List<UserDetails>> {
        Context context;
        UserDetails userDetails = new UserDetails();
        ProgressDialog pd;

        public GetFriendsAPI(Context ctx) {
            context = ctx;
        }



        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Searching...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

        }

        @Override
        protected void onPostExecute(List<UserDetails> userDetails) {
            super.onPostExecute(userDetails);

            if(userDetails != null){

                currentFreinds.setAdapter(new FriendsSearchAdapter(getActivity(),1,userDetails ));
                pd.dismiss();
            }
            else{
                Log.d("TAG", "Post Execute is NULL");
                pd.dismiss();
            }
        }

        @Override
        protected List<UserDetails> doInBackground(String... params) {
            // Get the rquired Data
            UserDetails register = new UserDetails();
            Call<List<UserDetails>> callFriends = Api.getClient().getFriends(params[0]);
            try {
                Response<List<UserDetails>> responseFriends = callFriends.execute();
                if (responseFriends.isSuccessful() && responseFriends.body() != null) {
                    Log.d("inbackground", "none");
                    return responseFriends.body();
                }
                else{
                    Log.d("in Background", "Something wrong with response");
                }
            } catch (IOException e) {
                Log.d("Exception in Background", e.toString());
                e.printStackTrace();
                return null;
            }
            Log.d("inbackground", "outside");
            return null;
        }
    }

}