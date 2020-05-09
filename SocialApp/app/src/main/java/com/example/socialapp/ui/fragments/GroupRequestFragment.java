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
import com.example.socialapp.models.GroupInvitationDetails;
import com.example.socialapp.ui.adapter.GroupRequestAdapter;
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

public class GroupRequestFragment extends Fragment {

    View myFragementView;
    SearchView search;
    //    Typeface type;
    ListView groupResults;
    String found = "N";


    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final HomeScreen
//        type = Typeface.createFromAsset(activity.getAssets(), "fonts/book.TTF");
        myFragementView = inflater.inflate(R.layout.fragment_group_join_requests, container, false);

        groupResults = (ListView) myFragementView.findViewById(R.id.listview_group_join_requests);
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        String searchFor = pref.getString("username", "None");
        Log.d("Group_Requests username", searchFor);
        new GetGroupsRequestsAPI(getContext()).execute(searchFor);
        return myFragementView;
    }


    class GetGroupsRequestsAPI extends AsyncTask<String, Void , List<GroupInvitationDetails>> {
        Context context;
        ProgressDialog pd;
        public GetGroupsRequestsAPI(Context ctx) {
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
        protected void onPostExecute(List<GroupInvitationDetails> userDetails) {
            super.onPostExecute(userDetails);
            pd.dismiss();
            if(userDetails != null){
                groupResults.setAdapter(new GroupRequestAdapter(getActivity(),1,userDetails ));

            }
            else{
                Log.d("Group_Requests", "onPost NULL");
            }
        }

        @Override
        protected List<GroupInvitationDetails> doInBackground(String... params) {
            // Get the rquired Data
            GroupDetails register = new GroupDetails();
            Call<List<GroupInvitationDetails>> callFriends = Api.getClient().getGroupJoinRequests(params[0]);
            try {
                Response<List<GroupInvitationDetails>> responseFriends = callFriends.execute();
                if (responseFriends.isSuccessful() && responseFriends.body() != null) {
                    Log.d("Group_Requests", "none");
                    return responseFriends.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Group_Requests", "Exception");
                return null;
            }
            Log.d("Group_Requests", "out");
            return null;
        }
    }
}
