package com.example.socialapp.ui.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.socialapp.ui.adapter.GroupSearchAdapter;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Response;

public class GroupSearchFragment extends Fragment {

    View myFragementView;
    SearchView search;
    //    Typeface type;
    ListView searchResults;
    String found = "N";

    ArrayList<GroupDetails> GroupResults = new ArrayList<GroupDetails>();

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final HomeScreen
//        type = Typeface.createFromAsset(activity.getAssets(), "fonts/book.TTF");
        myFragementView = inflater.inflate(R.layout.fragment_group_search, container, false);

        search = (SearchView) myFragementView.findViewById(R.id.group_search_text);
        search.setQueryHint("Enter text here ");

        searchResults = (ListView) myFragementView.findViewById(R.id.listview_group);
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
                    GetGroupsAPI getGroups = new GetGroupsAPI(getContext());
                    getGroups.execute(newText);
                } else {
                    searchResults.setVisibility(myFragementView.INVISIBLE);
                }
                return false;
            }
        });
        return myFragementView;
    }


    class GetGroupsAPI extends AsyncTask<String, Void , List<GroupDetails>> {
        Context context;
        ProgressDialog pd;
        public GetGroupsAPI(Context ctx) {
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
        protected void onPostExecute(List<GroupDetails> userDetails) {
            pd.dismiss();
            if(userDetails != null){

                searchResults.setAdapter(new GroupSearchAdapter(getActivity(),1,userDetails ));

            }
            else{
                Log.d("GroupSearch", "Post Execute is NULL");

            }
        }

        @Override
        protected List<GroupDetails> doInBackground(String... params) {
            Call<List<GroupDetails>> callFriends = Api.getClient().getGroups(params[0]);
            try {
                Response<List<GroupDetails>> responseFriends = callFriends.execute();
                if (responseFriends.isSuccessful() && responseFriends.body() != null) {
                    Log.d("GroupSearch","inbackground");
                    return responseFriends.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("GroupSearch","Exception");
                return null;
            }
            Log.d("GroupSearch","out");
            return null;
        }
    }
}
