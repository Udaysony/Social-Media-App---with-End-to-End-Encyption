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
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.ui.adapter.FriendsSearchAdapter;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Response;

public class FriendsSearchFragment extends Fragment {

    View myFragementView;
    SearchView search;
    //    Typeface type;
    ListView searchResults;
    String found = "N";
    private int grp_id;

    ArrayList<UserDetails> FriendResults = new ArrayList<UserDetails>();

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final HomeScreen
//        type = Typeface.createFromAsset(activity.getAssets(), "fonts/book.TTF");
        myFragementView = inflater.inflate(R.layout.fragment_friends_search, container, false);
        search = (SearchView) myFragementView.findViewById(R.id.friend_search_text);
        search.setQueryHint("Search for new Friends... ");

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String searchFor = pref.getString("username", "None");

        searchResults = (ListView) myFragementView.findViewById(R.id.listview_friends);
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    searchResults.setVisibility(myFragementView.VISIBLE);
                    Log.d("trigure api", "here");
                    GetFriendsAPI getFriends = new GetFriendsAPI(getContext());
                    getFriends.execute(newText);
                } else {
                    searchResults.setVisibility(myFragementView.INVISIBLE);
                    // call api to get all current friends

                }
                return false;
            }
        });
        return myFragementView;
    }

    class GetFriendsAPI extends AsyncTask<String, Void , List<UserDetails>> {
        Context context;
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
            if(userDetails != null){

                searchResults.setAdapter(new FriendsSearchAdapter(getActivity(),1,userDetails ));
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
