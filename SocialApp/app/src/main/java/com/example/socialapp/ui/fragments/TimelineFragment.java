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
import com.example.socialapp.models.GroupKeyDetails;
import com.example.socialapp.models.GroupStatusDetails;
import com.example.socialapp.models.PostDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.ui.activity.SocialActivity;
import com.example.socialapp.ui.adapter.TimelineAdapter;
import com.example.socialapp.utils.Security_Key_message;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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
import androidx.recyclerview.widget.RecyclerView;
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

    private String current_username;
    private String PostSharePrivacy;
    private int grp_id, v_id;
    private int new_version;
    private String g_name;
    private String post_data_text;
    private String group_key;
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
            All_groups = new GetAllGroupsAndFriendsAPI(getContext()).execute(current_username).get();
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
//                        Toast.makeText(getContext(), ""+item.getTitle(), Toast.LENGTH_SHORT);
                        // on click post share for public, private, Friends
                        if((PostSharePrivacy).equals("Friends")) {
                            grp_id = has_group_info.get(current_username + "Friends");

                            try {
                                new GetMaxVersion(getContext()).execute().get();
                                Log.d("Fetched  start VID: ", String.valueOf(v_id));
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            new CheckGroupStatus(getContext()).execute();
                            post_data.setText("");

                        }

                        if((PostSharePrivacy).equals("Private")) {
                            grp_id = has_group_info.get(current_username + "Friends");
                            v_id = 1;
                            new PutPostAPI(getContext()).execute();
                            post_data.setText("");
                        }


                        if((PostSharePrivacy).equals("Public")) {
                            grp_id = has_group_info.get(current_username + "Friends");
                            v_id = 1;
                            new PutPostAPI(getContext()).execute();
                            post_data.setText("");

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
                                    grp_id = has_group_info.get(item.getTitle());
                                    try {
                                        v_id = new GetMaxVersion(getContext()).execute().get();
                                        Log.d("Fetched  start VID: ", String.valueOf(v_id));
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    g_name = ""+item.getTitle();

                                    Log.d("Selected Group info", g_name + " "+ String.valueOf(grp_id));
                                    // post share for Group
                                    new CheckGroupStatus(getContext()).execute();
                                    post_data.setText("");
                                    return true;
                                }
                            });
                            popupMenu2.show();

                        }

                        return true;
                    }
                });
                popupMenu.show();

            }
        });

        searchResults = (ListView) myFragementView.findViewById(R.id.recycler_timeline);

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
        boolean first = true;

        ProgressDialog pd;

        public GetPostsAPI(Context ctx) {
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
        protected void onPostExecute(List<PostDetails> postDetails) {

            if(postDetails != null){
                pd.dismiss();
                    searchResults.setAdapter(new TimelineAdapter(getActivity(), postDetails));
                    first = false;
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
            Call<List<PostDetails>> callFriends = Api.getClient().getPosts(current_username);
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
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Posting...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

            Log.d("Post upload", "pre");
            post.setTimestamp(new Timestamp(System.currentTimeMillis()));

            // post_data_text => encrypt with session key
            byte[] session_key = new byte[1000];
            String DS="DS";
            String encrypted_post_data_text = "data";
            try {
                session_key = key_helper.getSessionKey();
                Log.d("Post Upload Session Key:", Base64.getEncoder().encodeToString(session_key));

                encrypted_post_data_text = key_helper.encrypt(post_data_text,session_key);
                Log.d("Post upload excrypted text", encrypted_post_data_text);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Post upload", "pre data encryption error");
            }

            if (PostSharePrivacy.equals("Private")){
                // Encrypt session key with private_secret_key
//                post.setTimestamp(new Timestamp(System.currentTimeMillis()));

                String gk = pref.getString("PrivateSecretKey", "Uday");
                Log.d("Private Secret key in upload", gk);
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
                post.setDigitalSignature(DS);
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

//                String friends_group_key = pref.getString("DefaultGroupKey", "Uday");
                byte[] gk_bytes = new byte[0];
                byte[] encypted_session_key_byte = new byte[0];

                try {
                    KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                    ks.load(null);
                    Log.d("Post Upload" , "Got Private key ");
                    privateKey = (PrivateKey) ks.getKey(current_username+ "RSA_Keys",null);
                    Log.d("Post Upload" , "Got Private key successfuly");

                    gk_bytes = key_helper.decrept_With_privateKey(group_key,privateKey);
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
                post.setVersion_num(v_id);
                post.setDigitalSignature(DS);
                post.setOwnerusername(current_username);
                post.setPrivacy(PostSharePrivacy);

                Log.d("TIMESTAMP", (new Timestamp(System.currentTimeMillis())).toString());
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
                    Log.d("Post upload", "DS throwsException");

                } catch (Exception e) {
                    Log.d("Post upload", "session key encryption error");
                    e.printStackTrace();
                }
                String session_key_encypted_string = Base64.getEncoder().encodeToString(encypted_session_key_byte);
                        // Encrypt session key with apprropriate Group Key
                post.setPostText(encrypted_post_data_text);
                post.setSessionKey(session_key_encypted_string);
                post.setGroupid(grp_id);
                Log.d("Fetched  End VID: ", String.valueOf(v_id));
                post.setVersion_num(v_id);
                post.setDigitalSignature(DS);
                post.setOwnerusername(current_username);
                post.setPrivacy(PostSharePrivacy);
            }
            Log.d("Post upload", "pre done");

        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if((s).equals("ok")){
                Toast.makeText(getContext(), "Posted...", Toast.LENGTH_SHORT).show();
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
//                v_id = groupStatusDetails.get
                Log.d("Post Get Group info", "onPost");
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
            Call<String> call_ = Api.getClient().getGroupKey(grp_id, 0 ,current_username);
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


    class GetMyGroupKey extends AsyncTask<Void, Void, List<GroupKeyDetails>>{
        Context ctx;

        public GetMyGroupKey (Context x){ctx=x;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("In upload get Group key Pre" , current_username + " " + String.valueOf(grp_id));
        }

        @Override
        protected void onPostExecute(List<GroupKeyDetails> s) {
            if (s != null){
//                new_user_public_key ;
                group_key = s.get(0).getGroupKey();
                Log.d("Request Accept Got group Keys", "ok");
                new PutPostAPI(getContext()).execute();
            }
            else {
                Log.d("Request Accept Something wrong with response", "" + s);
            }
        }

        @Override
        protected List<GroupKeyDetails> doInBackground(Void... voids) // gid, owner name
        {
            Call<List<GroupKeyDetails>> call_ = Api.getClient().getMyGroupKey(current_username);
            try {
                Response<List<GroupKeyDetails>> resp = call_.execute();
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
            pd.setMessage("Fetching data...");
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


    class CheckGroupStatus extends AsyncTask<Void, Void, List<UserDetails>>{
        Context ctx;
        Security_Key_message key_help = new Security_Key_message();
        byte[] new_group_key = new byte[0];

        public CheckGroupStatus(Context c){ctx = c;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<UserDetails> userDetails) {

            if (userDetails != null){

                v_id = new_version + 1;
                Log.d("Fetched  Dirty after VID: ", String.valueOf(v_id));

                try {
                    new_group_key = key_helper.getGroupKey();
                    Log.d("Version Key dat update", "got group key");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Version Key dat update", "got public key Exception" + e.toString());
                }
                //Get version #---------------------------------------------------------------------------
                new GetMaxVersion(ctx).execute();
                //
                //Start updating public key of users and put it in Database--------------------------------
//                incrementing version number
//                v_id = new_version+1;

                for(UserDetails u : userDetails){
                    // Generate the keys.........
//                    new PutNewGroupKey(ctx).execute(); //username,grp_id,g_name,KEY, (new_version+1) // pass all as string for better PreExecute

                    String users_pk = u.getUserPublicKey();
                    byte[] users_pk_byte = Base64.getDecoder().decode(users_pk);
                    try {
                        PublicKey users_publicKey =
                                KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(users_pk_byte));
                        Log.d("Version Key dat update", "got public key for " + u.getUserName());

                        String new_group_key_encryprted = key_helper.encrypt_With_publicKey(new_group_key, users_publicKey);

                        String hold = new AddNewKeyData(ctx).execute(u.getUserName(), String.valueOf(grp_id),
                                new_group_key_encryprted , String.valueOf(v_id)).get();


                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                        Log.d("Version Key dat update", "Exception" + e.toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        Log.d(" Version Key dat update", "Exception" + e.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Version Key dat update", "Exception" + e.toString());
                    }

                Log.d("Version Key update for All users completed","ok");
                }

                //------------------------------------- This for loop makes sure that all the users get updated Group Key------------------

                try {
                    String update_status = new UpdateStatusToclean(getContext()).execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //----------------------------after this call PutPostApi ------------------------------

                if (PostSharePrivacy.equals("Friends")) {
                    new GetGroupKey(getContext()).execute();
                }

                else
                {
                    new GetGroupKey(getContext()).execute();
                }


            }
            else{
                //post as normal -------------------------------------------- status is Clean == 0
                Log.d("Version no update", "userdetails ?");
                if (PostSharePrivacy.equals("Friends")) {
                    new GetGroupKey(getContext()).execute();
                }

                 else
                {
                    new GetGroupKey(getContext()).execute();
                }
            }
        }

        @Override
        protected List<UserDetails> doInBackground(Void... voids) {
            Log.d("Version call for ", String.valueOf(grp_id));
            Call<List<UserDetails>> call_ = Api.getClient().getStatusAndDetails(grp_id);

            try {
                Response<List<UserDetails>> resp = call_.execute();
                Log.d("Version Get user data to update key", "Background");
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Version data To update key", "Exception");
            }
            Log.d("Version data To update key", "Out");
            return null;
        }
    } // yet to implement API in server


    class GetMaxVersion extends AsyncTask<Void, Void, Integer>{
        Context ctx;

        public GetMaxVersion(Context c){ctx = c;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer vnumber) {
            if(vnumber != null)
            {
                new_version = vnumber;
                v_id = vnumber;
                Log.d("Version get", "Success");
            }
            else{
                Log.d("Version get", "Null");
            }
        }

        @Override
        protected Integer doInBackground(Void... strings) {
            Call<Integer> call_ = Api.getClient().getMaxVersion(grp_id);

            try {
                Response<Integer> resp = call_.execute();
                Log.d("Version get", "Background");
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Version get", "Exception");
            }
            Log.d("Version get", "Out");
            return null;
        }
    }


    class AddNewKeyData extends AsyncTask<String, Void, String>{
        Context ctx;
        GroupKeyDetails gk = new GroupKeyDetails();

        public AddNewKeyData(Context c){ctx=c;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("ok"))
            {
                Log.d(" VersionGroup Key dataupdate", "success  " + s);
            }
            else{
                Log.d(" VersionGroup Key dataupdate", "failed..." + s);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("Version data update got" , strings[0] +" " + strings[1] +" " + strings[2] + " " + strings[3]);
            Call<String> call_ = Api.getClient().AddNewKeyTableData(strings[0], strings[1], strings[2], strings[3]);

            try {
                Response<String> resp = call_.execute();
                Log.d("Version Group Key dataupdate", "in background" + resp.body());
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Version Group Key dataupdate", "exception");
            }
            Log.d(" Version Group Key dataupdate", "out");
            return null;
        }
    }

    class UpdateStatusToclean extends AsyncTask<Void, Void, String>{
        Context ctx;

        public UpdateStatusToclean(Context x){ctx=x;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("ok")){
                Log.d("Version updated to clean  ", "o");
            }
            else{
                Log.d("Version update clean problem ", s);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> call_ = Api.getClient().updateStatusclean(grp_id);

            try {
                Response<String> resp = call_.execute();
                Log.d("Version updated to clean  ", "in background");
                return resp.body();
            } catch (IOException e) {
                Log.d("Version updated to clean  ", "Exception");
                e.printStackTrace();
            }
            Log.d("Version updated to clean  ", "Out");
            return null;
        }
    }
}



