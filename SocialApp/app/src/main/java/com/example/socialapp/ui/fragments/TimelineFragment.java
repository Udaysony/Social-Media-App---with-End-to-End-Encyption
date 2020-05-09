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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.models.GroupStatusDetails;
import com.example.socialapp.models.PostDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.ui.activity.SocialActivity;
import com.example.socialapp.ui.adapter.TimelineAdapter;
import com.example.socialapp.utils.Security_Key_message;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Response;

public class TimelineFragment extends Fragment {

    View myFragementView;
    //    Typeface type;
    ListView searchResults;
    EditText post_data;
    String found = "N";
    Button postBtn;
    List<String> Friendsgroups = new ArrayList<String>();
    private List<GroupDetails> CurrentGroupNamesList;
    SocialActivity get_key_activity = new SocialActivity();
    Security_Key_message key_helper = new Security_Key_message();
    private List<GroupDetails> All_groups;

    public String current_username;
    public String PostSharePrivacy;
    public int grp_id, v_id;
    public String g_name;
    public String post_data_text;
    public String group_key;
    PrivateKey privateKey;
    public String create_encoded_post;
    public String create_encoded_session_key;
    public String create_digital_sign;
    private HashMap<String, Integer> has_group_info = new HashMap<String, Integer>();

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final HomeScreen
//        type = Typeface.createFromAsset(activity.getAssets(), "fonts/book.TTF");


        myFragementView = inflater.inflate(R.layout.fragment_timeline, container, false);
        post_data = (EditText)myFragementView.findViewById(R.id.share_text);
        postBtn = (Button)myFragementView.findViewById(R.id.share_button);

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        current_username = pref.getString("username","None");
        Log.d("Post user info", current_username);
        // getting All the groups for this user.....
        try {
            All_groups  = new GetAllGroupsAndFriendsAPI(getContext()).execute(current_username).get();
        } catch (ExecutionException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (All_groups != null) {
            for (GroupDetails g : All_groups) {
                String g_name = g.getgroupname();
                if(g_name.contains("Friends") && g_name.equals(current_username+"Friends")){
                    Log.d("Got Group info of", g_name);
                    has_group_info.put(g.getgroupname(), g.getgroupid());
                }
                else if(g_name.contains("Friends")) {
                 //Do nothing;
                }
                else
                    {
                    Log.d("Got Group info of", g_name);
                    has_group_info.put(g.getgroupname(), g.getgroupid());
                }
            }
        }
        else{
            Log.d("All Groups" , "NULL");
        }

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), postBtn);
                popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        post_data_text = post_data.getText().toString();
                        PostSharePrivacy = ""+item.getTitle();
                        Log.d("Clicked on: ",""+item.getTitle());
                        Log.d("Post upload", "selected privacy");
                        Toast.makeText(getContext(), ""+item.getTitle(), Toast.LENGTH_SHORT);
                        // on click post share for public, private, Friends
                        if(!(PostSharePrivacy).equals("Group")) {
                            grp_id = has_group_info.get(current_username + "Friends");
                            v_id = 1;
                            new GetGroupInformation(getContext()).execute(current_username + "Friends");
                        }
                        if((PostSharePrivacy).equals("Group")){
                            ///
                            PopupMenu popupMenu2 = new PopupMenu(getContext(), postBtn);
                            popupMenu2.getMenuInflater().inflate(R.menu.submenu_group, popupMenu2.getMenu());
                            popupMenu2.getMenu().clear();

                            for (GroupDetails g: All_groups){
                                String g_name = g.getgroupname();
                                Log.d("All Groups", " "+ g_name);
//                                has_group_info.put(g.getgroupname(), g.getgroupid());
                                if(!(g_name.contains("Friends"))) {
                                    popupMenu2.getMenu().add(g_name);
                                }

                            }
                            popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    Log.d("Clicked on: ",""+item.getTitle());
                                    Log.d("Post upload", "selected group");
                                    Toast.makeText(getContext(), ""+item.getTitle(), Toast.LENGTH_SHORT);
                                    g_name = ""+item.getTitle();
                                    grp_id = has_group_info.get(item.getTitle());
                                    Log.d("Selected Group info", g_name + " "+ String.valueOf(grp_id));
                                    // post share for Group
                                    new GetGroupKey(getContext()).execute();
                                    return true;
                                }
                            });
                            popupMenu2.show();;

                            //////

                        }
                        return true;
                    }
                });
                popupMenu.show();;
            }
        });

        searchResults = (ListView)myFragementView.findViewById(R.id.listview_timeline);

        GetPostsAPI getPostsapi = new GetPostsAPI(getContext());
        getPostsapi.execute();


        return myFragementView;
    }


    class GetCurrentGroups extends AsyncTask<String, Void, List<GroupDetails>>{

        Context context;
        ProgressDialog pd;



        public GetCurrentGroups (Context ctx){context=ctx;}

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Fetching Information...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

        }

        @Override
        protected void onPostExecute(List<GroupDetails> userDetails) {
            pd.dismiss();
            if(userDetails != null){
                Log.d(" Post CurrentGroups", "Got data");
                All_groups = userDetails;
            }
            else{
                Log.d(" Post CurrentGroups", "Post Execute is NULL");
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

    class GetPostsAPI extends AsyncTask<Void, Void , List<PostDetails>> {
        Context context;
        PostDetails postDetails = new PostDetails();

//        ProgressDialog pd;

        public GetPostsAPI(Context ctx) {
            context = ctx;
        }


        @Override
        protected void onPreExecute() {
//            pd = new ProgressDialog(getActivity());
//            pd.setCancelable(false);
//            pd.setMessage("Searching...");
//            pd.getWindow().setGravity(Gravity.CENTER);
//            pd.show();

        }

        @Override
        protected void onPostExecute(List<PostDetails> postDetails) {
            super.onPostExecute(postDetails);

            if(postDetails != null){
//                pd.dismiss();
                searchResults.setAdapter(new TimelineAdapter(getActivity(),1, postDetails ));

            }
            else{
                Log.d("TAG", "Post Execute is NULL");
//                pd.dismiss();
            }
        }

        @Override
        protected List<PostDetails> doInBackground(Void... voids) {
            // Get the rquired Data
            UserDetails register = new UserDetails();
            Call<List<PostDetails>> callFriends = Api.getClient().getPosts();
            try {
                Response<List<PostDetails>> responseFriends = callFriends.execute();
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


    class PutPostAPI extends AsyncTask<Void ,Void, String>{
        Context ctx;
        PostDetails post = new PostDetails();
        ProgressDialog pd;
        SharedPreferences pref =  getContext().getSharedPreferences("MyPref" , Context.MODE_PRIVATE);
        public PutPostAPI(Context x) {ctx = x;}
        @Override
        protected void onPreExecute() {
            Log.d("Post upload", "pre");
            // post_data_text => encrypt with session key
            byte[] session_key = new byte[1000];
            String DS="DS";
            String encrypted_post_data_text = "data";
            try {
                session_key = key_helper.getSessionKey();
                Log.d("Post Upload Session Key:", Base64.getEncoder().encodeToString(session_key));

                encrypted_post_data_text = key_helper.encrypt(post_data_text,session_key);
                Log.d("Post upload", encrypted_post_data_text);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Post upload", "pre data encryption error");
            }

            if (PostSharePrivacy.equals("Private")){
                // Encrypt session key with private_secret_key
//                post.setTimestamp(new Timestamp(System.currentTimeMillis()));

                String gk = pref.getString("PrivateSecretKey", "Uday");
                byte[] friends_group_key = Base64.getDecoder().decode(gk);

                byte[] encypted_session_key_byte = new byte[0];
                try {
                    encypted_session_key_byte = key_helper.encrypt(friends_group_key, session_key);

                } catch (Exception e) {
                    Log.d("Post upload", "session key encryption error");
                    e.printStackTrace();
                }
                String session_key_encypted_string = Base64.getEncoder().encodeToString(encypted_session_key_byte);

                post.setPostText(encrypted_post_data_text);
                post.setSessionKey(session_key_encypted_string);
                post.setGroupid(grp_id);
                post.setVersion_num(v_id);
                post.setDigitalSignature("DS");
                post.setOwnerusername(current_username);
//                post.setOriginalpostid();
                post.setPrivacy(PostSharePrivacy);
            }

            if (PostSharePrivacy.equals("Public")){
                // Do no encryption
                post.setPostText(post_data_text);
                post.setSessionKey("None");
                post.setGroupid(grp_id);
                post.setVersion_num(v_id);
                post.setDigitalSignature("None");
                post.setOwnerusername(current_username);
                post.setPrivacy(PostSharePrivacy);
            }

            if (PostSharePrivacy.equals("Friends")){
                // Encrypt session key Friends Group key

                String friends_group_key = pref.getString("DefaultGroupKey", "Uday");
                byte[] gk_bytes = new byte[0];
                byte[] encypted_session_key_byte = new byte[0];

                try {
                    KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                    ks.load(null);
                    Log.d("Post Upload" , "Got Private key ");
                    privateKey = (PrivateKey) ks.getKey(current_username+ "RSA_Keys",null);
                    Log.d("Post Upload" , "Got Private key successfuly");

                    gk_bytes = key_helper.decrept_With_privateKey(friends_group_key,privateKey);
                    Log.d("Post Upload" , "Decrypt with Private Key");
                    encypted_session_key_byte = key_helper.encrypt(gk_bytes, session_key);

                    //Generating Signature
                    DS = key_helper.sign(post_data_text, privateKey);

                } catch (Exception e) {
                    Log.d("Post upload", "session key encryption error");
                    e.printStackTrace();
                }
                String session_key_encypted_string = Base64.getEncoder().encodeToString(encypted_session_key_byte);


                post.setPostText(encrypted_post_data_text);
                post.setSessionKey(session_key_encypted_string);
                post.setGroupid(grp_id);
                post.setVersion_num(0);
                post.setDigitalSignature(DS);
                post.setOwnerusername(current_username);
                post.setPrivacy(PostSharePrivacy);
            }

            if (PostSharePrivacy.equals("Group")){
                byte[] gk_bytes = new byte[0];

                byte[] encypted_session_key_byte = new byte[0];
                try {
                    //getting group Key

                    KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                    ks.load(null);
                    Log.d("Post Upload" , "Got Private key Request");
                    privateKey = (PrivateKey) ks.getKey(current_username+ "RSA_Keys",null);
                    Log.d("Post Upload" , "Got Private key successfully");


                    // Encypting Session Key
                    gk_bytes = key_helper.decrept_With_privateKey(group_key,privateKey);
                    Log.d("Post upload","Encrypting session key");
                    encypted_session_key_byte = key_helper.encrypt(gk_bytes, session_key);

                    //Generating Signature
                    DS = key_helper.sign(post_data_text, privateKey);


                } catch (Exception e) {
                    Log.d("Post upload", "session key encryption error");
                    e.printStackTrace();
                }
                String session_key_encypted_string = Base64.getEncoder().encodeToString(encypted_session_key_byte);
                        // Encrypt session key with apprropriate Group Key
                post.setPostText(encrypted_post_data_text);
                post.setSessionKey(session_key_encypted_string);
                post.setGroupid(grp_id);
                post.setVersion_num(v_id);
                post.setDigitalSignature(DS);
                post.setOwnerusername(current_username);
                post.setPrivacy(PostSharePrivacy);
            }
            Log.d("Post upload", "pre done");
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Posting...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if((s).equals("ok")){
                Log.d("Post upload", "Done");
            }
            Log.d("Post upload", s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> call_ = Api.getClient().PutPost(post);
            Response<String> resp = null;
            try {
                resp = call_.execute();
                Log.d("Post upload", "inbackgroup ok");
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Post upload", "Exception");
            }
            Log.d("Post upload", "out");
            return "failed";
        }
    }

    class GetGroupInformation extends AsyncTask<String, Void, GroupStatusDetails>{
        Context c;
        ProgressDialog pd;
        public GetGroupInformation(Context ctx) {c = ctx;}
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Fetching Group data...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(GroupStatusDetails groupStatusDetails) {
            pd.dismiss();
            if (groupStatusDetails != null) {
                grp_id = groupStatusDetails.getGroupid();
//                v_id = groupStatusDetails
                Log.d("Post Get Group info", "in");
                new PutPostAPI(c).execute();
            }
        }

        @Override
        protected GroupStatusDetails doInBackground(String... strings) {
            Call<GroupStatusDetails> call_ = Api.getClient().getGroupInfo(strings[0]);
            Log.d("Post Get Groupinfo calling for ", strings[0]);
            try {
                Response<GroupStatusDetails> resp = call_.execute();
                Log.d("Post Get Group info", "in" + resp.body());
                return resp.body();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Post Get Group info", "Exception");
            }
            Log.d("Post Get Group info", "out");
            return null;
        }
    }


    class GetGroupKey extends AsyncTask<Void, Void, String>{
        Context ctx;

        public GetGroupKey (Context x){ctx=x;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("In upload get Group key Pre" , current_username + " " + String.valueOf(grp_id));
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){
//                new_user_public_key ;
                group_key = s;
                Log.d("Request Accept Got group Keys", s);
                new PutPostAPI(getContext()).execute();
            }
            else {
                Log.d("Request Accept Something wrong with response", "" + s);
            }
        }

        @Override
        protected String doInBackground(Void... voids) // gid, owner name
        {
            Call<String> call_ = Api.getClient().getGroupKey(grp_id,current_username);
            try {
                Response<String> resp = call_.execute();
                Log.d("Request Got group key","in"+resp.body());
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Request Got keys","Exception");
            }
            Log.d("Request Got keys","OUT");
            return null;

        }
    }



    class GetAllGroupsAndFriendsAPI extends AsyncTask<String, Void, List<GroupDetails>>{
        Context c;
        ProgressDialog pd;
        public GetAllGroupsAndFriendsAPI(Context ctx){c =ctx;}

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Fetching All Group data...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(List<GroupDetails> groupDetails) {
            pd.dismiss();
            Log.d("Getting Group info", "In PostExe");
            if(groupDetails != null){
                Log.d("Getting Group info", "In Post Success" + groupDetails);
                //store Group details
                All_groups =  groupDetails;

            }
            else {
                Log.d("Getting Group info", "Post Null eror");
            }
        }

        @Override
        protected List<GroupDetails> doInBackground(String... strings) {
            Call<List<GroupDetails>> call_ = Api.getClient().getAll_Groups(strings[0]);
            try {
                Response<List<GroupDetails>> resp = call_.execute();
                Log.d("Getting Group info", "in "+resp.body());
                return resp.body();
            } catch (IOException e) {
                Log.d("Getting Group info", "Exception");
                e.printStackTrace();
            }
            Log.d("Getting Group info", "Out");
            return null;
        }
    } // not used
}



