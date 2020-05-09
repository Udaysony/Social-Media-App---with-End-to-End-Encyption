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
import com.example.socialapp.models.GroupInvitationDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.ui.adapter.FriendsNewRequestsAdapter;
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

public class FriendsRequestsFragment extends Fragment {

    View myFragementView;
    //    Typeface type;
    ListView reqResults;
    String found = "N";
    private int grp_id;

    ArrayList<UserDetails> FriendResults = new ArrayList<UserDetails>();

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final HomeScreen
//        type = Typeface.createFromAsset(activity.getAssets(), "fonts/book.TTF");
        myFragementView = inflater.inflate(R.layout.new_friends_fragement, container, false);


        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String searchFor = pref.getString("username", "None");

        reqResults = (ListView) myFragementView.findViewById(R.id.listview_new_friends);
       new GetFriendRequestsAPI(getContext()).execute(searchFor);
        return myFragementView;
    }
//.................................................
//YET TO CHANGE FOR REQUESTS.......................
//.................................................
    // Get our Group Key (from MyPref)
    //Encrypt it with users public key
    //get Max_version
    //Put everything in Group_key_Table
    //Delete this Entry of request

    class GetFriendRequestsAPI extends AsyncTask<String, Void , List<GroupInvitationDetails>> {
        Context context;
        GroupInvitationDetails invitationDetailsetails = new GroupInvitationDetails();
        ProgressDialog pd;

        public GetFriendRequestsAPI(Context ctx) {
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
        protected void onPostExecute(List<GroupInvitationDetails> invitationDetails) {
            super.onPostExecute(invitationDetails);

            if(invitationDetails != null){

                reqResults.setAdapter(new FriendsNewRequestsAdapter(getActivity(),1, invitationDetails ));
                pd.dismiss();
            }
            else{
                Log.d("TAG", "Post Execute is NULL");
                pd.dismiss();
            }
        }

        @Override
        protected List<GroupInvitationDetails> doInBackground(String... params) {
            // Get the rquired Data
            Call<List<GroupInvitationDetails>> getReqs = Api.getClient().getFriendRequests(params[0]);
            try {
                Response<List<GroupInvitationDetails>> responseFriends = getReqs.execute();
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
