package com.example.socialapp.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.models.GroupKeyDetails;
import com.example.socialapp.models.PostDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.ui.activity.SocialActivity;
import com.example.socialapp.ui.fragments.TimelineFragment;
import com.example.socialapp.utils.Security_Key_message;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class TimelineAdapter extends ArrayAdapter<PostDetails> {

    private LayoutInflater mInflater;
    private List<PostDetails> postsList; //used for the search bar
    private int count;
    //    Typeface type;
    private Context mContext;
    private String current_username;
    private PrivateKey privateKey;
    private int grp_id, v_id;
    private String group_key;
    private String post_got_privacy;

    private int share_gid,share_vid, original_post_id;
    private String share_text;
    private String sharing_group_key;

    SocialActivity sc = new SocialActivity();
    private HashMap<String,String> group_key_has = new HashMap<String, String>();
    Security_Key_message key_helper = new Security_Key_message();
    SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
    ProgressDialog pd;



    public TimelineAdapter(@NonNull Context context, List<PostDetails>  posts) {
        super(context, 0, posts);
        mInflater = LayoutInflater.from(context);
        this.postsList = posts;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return postsList.size();
    }

    @Override
    public PostDetails getItem(int position) {
        // TODO Auto-generated method stub
        return postsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        current_username = pref.getString("username", "None");


        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            //KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(u_name+ "RSAkey", null);

            privateKey = (PrivateKey) ks.getKey(current_username+ "RSA_Keys",null);
            //pkEntry.getPrivateKey();
//            Log.d("PrivateKey", myPrivateKey == null)));
//            Log.d("Getting Private key:", Base64.getEncoder().encodeToString(myPrivateKey.getEncoded()));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        final PostDetails tempUser = postsList.get(position);
        Log.d("In adapter getting text  ", tempUser.getPostText());
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.timeline_listview_layout,null);
            holder = new ViewHolder();
            holder.user_name = (TextView) convertView.findViewById(R.id.post_user_name);
            holder.post_time = (TextView)convertView.findViewById(R.id.post_time);
            holder.post_text = (TextView)convertView.findViewById(R.id.post_text);
            holder.post_privacy = (TextView)convertView.findViewById(R.id.post_privacy);
            holder.post_share = (Button) convertView.findViewById(R.id.post_share);

            convertView.setTag((holder));




        }
        else{
            holder = (ViewHolder) convertView.getTag();
            convertView.setTag(holder);
        }


        holder.user_name.setText(tempUser.getOwnerusername());


        Log.d("Decoding ", "POSTS");
        String  error_ = "Start ";
        // Get group key for gid and username
        grp_id = tempUser.getGroupid();
        v_id = tempUser.getVersion_num();
//         start decrypting process
        String post_key_master_index =  String.valueOf(grp_id) + "_"+ String.valueOf(v_id);
        Log.d("TAG KEYS index", post_key_master_index);

        //Got the post Text
        String encrypted_post_text = tempUser.getPostText();
        String encrypted_session_key = tempUser.getSessionKey();
        post_got_privacy = tempUser.getPrivacy();
        String out_text = "Decode_Error";


        // decrypt group with private key
        if (!(post_got_privacy.equals("Public"))) {

                //Getting related Group Key
                if (group_key_has.containsKey(String.valueOf(post_key_master_index))){
                    group_key = group_key_has.get(post_key_master_index);
                    Log.d("Post Decode", "Got Group key form Dictionary" + " " + group_key);
                }
                else{
                    try {
                        group_key = new GetGroupKey(getContext()).execute().get(); // to get over with Async task of fetching group key and continueing the rest of the code
                        group_key_has.put(post_key_master_index, group_key);
                        Log.d("Post Decode", "Got Group key form database" + " " + group_key);

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            // Starting Decryption process
            // Decrypting Group key using private key
                byte[] decrypt_group_key = new byte[0];
                try {

                    decrypt_group_key = key_helper.decrept_With_privateKey(group_key, privateKey);
                    Log.d("PostDecode", "Private key success");
                } catch (Exception e) {
                    Log.d("PostDecode", "Private Key Exception");
                    e.printStackTrace();
                }

                byte[] encrypted_sessKey = Base64.getDecoder().decode(encrypted_session_key);
                byte[] sess_key = new byte[0];

                // Decrypting Session_key using decrypted group key

                        if (post_got_privacy.equals("Private")) // If post is Private, Session Key is encrypted with PrivateSecret key
                        {
                            group_key = pref.getString("PrivateSecretKey" , "null");
                            decrypt_group_key = Base64.getDecoder().decode(group_key);
                        }

                try {
                    sess_key = key_helper.decrypt(decrypt_group_key, encrypted_sessKey);
                    Log.d("Post Decode", "Got sess key  sucess");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Post Decode", "GSss key Exception");
                }


                // Decrypting text using decrypted session key
                try {
                    out_text = key_helper.decrypt(encrypted_post_text, sess_key);
                    Log.d("Post Decode", "Post text Success");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Post Decode", "Post text Exception");
                }


                //Code to decrypt post ,1 get session key, 2 get post
                if (out_text.equals("Decode_Error")) {
                    postsList.remove(position);
                    notifyDataSetChanged();
                }
            }
            else{
                out_text = encrypted_post_text;
            }
        holder.post_text.setText(out_text);
        String timestampAsString = tempUser.getTimestamp().toString();
        Log.d("Timestamp", timestampAsString);
        holder.post_privacy.setText(post_got_privacy);
        holder.post_time.setText(timestampAsString.substring(0,16));

//        holder.user_name.setTypeface(type);
//        holder.user_name.setText(tempUser.getUserName());
//        holder.add_friend.setTypeface(type);


        holder.post_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Share this post event triggered",  Toast.LENGTH_SHORT).show();
                    share_text = holder.post_text.getText().toString();
                     original_post_id =  tempUser.getPostId();
                     Log.d("text to share:::: >  "+share_text , "original_post_id" + String.valueOf(original_post_id));
//                    new PostShare(getContext(), tempUser.getPostId(), share_text ).execute();
                    new GetMYGroupKey(getContext()).execute();

            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView user_name;
        TextView post_time;
        TextView post_text;
        TextView post_privacy;
        Button post_share;
    }

    class GetGroupKey extends AsyncTask<Void, Void, String> {
        Context ctx;

        public GetGroupKey (Context x){
            ctx=x;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){
//                new_user_public_key ;
                Log.d("Request Accept Got group Keys", s);
                group_key = s;

            }
            Log.d("Request Accept Something wrong with response", ""+s);
        }

        @Override
        protected String doInBackground(Void... voids) // gid, owner name
        {
            Call<String> call_ = Api.getClient().getGroupKey(grp_id, v_id,current_username);
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


    class PostShare extends AsyncTask<Void, Void, String>{
        Context context;
        PostDetails post = new PostDetails();
        String raw_text;
        int post_id;
        String DS = "DS";
        Security_Key_message key_helper = new Security_Key_message();
        ProgressDialog pd;
        private  String encrypted_sess_key, encrypted_text;

        public PostShare (Context c, int post_id, String text) {
            this.context = c;
            this.post_id = post_id;
            this.raw_text = text;
        }


        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.setMessage("Sharing post...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

//            encrypt data
            byte[] gk_bytes = new byte[0];
            byte[] encypted_session_key_byte = new byte[0];

            try {
                KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                ks.load(null);
                Log.d("Share Post Upload" , "Got Private key ");
                privateKey = (PrivateKey) ks.getKey(current_username+ "RSA_Keys",null);
                Log.d("Share Post Upload" , "Got Private key successfully");

                gk_bytes = key_helper.decrept_With_privateKey(sharing_group_key,privateKey);

                byte[] session_key = key_helper.getSessionKey();

                encrypted_text = key_helper.encrypt(raw_text, session_key);

                Log.d("Share Post Upload" , "Decrypt with Private Key");
                encypted_session_key_byte = key_helper.encrypt(gk_bytes, session_key);

                //Generating Signature
                DS = key_helper.sign(raw_text, privateKey);

            } catch (Exception e) {
                Log.d("Share Post upload", "session key encryption error");
                e.printStackTrace();
            }
            encrypted_sess_key = Base64.getEncoder().encodeToString(encypted_session_key_byte);
            Log.d("Share Encrypted session key: ", encrypted_sess_key);


            post.setPostText(encrypted_text);
            post.setSessionKey(encrypted_sess_key);
            post.setGroupid(share_gid);
            post.setVersion_num(share_vid);
            post.setDigitalSignature(DS);
            post.setOwnerusername(current_username);
            post.setOriginalpostid(original_post_id);
            post.setPrivacy("Friends");
            post.setTimestamp(new Timestamp(System.currentTimeMillis()));


        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if(s.equals("ok")){
                Log.d("Share Post", "got ok" );
                Toast.makeText(getContext(), "Shared Post", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Not Shared...Got some Error ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> call_ = Api.getClient().PutPost(post);

            try {
                Response<String> resp = call_.execute();
                Log.d("Share Post", "In background" );
                return  resp.body();
            } catch (IOException e) {
                Log.d("Share Post", "In Exception" );
                e.printStackTrace();
            }
            Log.d("Share Post", "In out" );
            return  null;
        }
    }


    class GetMYGroupKey extends AsyncTask<Void, Void, List<GroupKeyDetails>> {
        Context ctx;

        public GetMYGroupKey (Context x){
            ctx=x;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<GroupKeyDetails> gk) {
            if (gk!= null){
//                new_user_public_key ;
                Log.d(" Share RequestAccept Got group Keys", "ok");
                sharing_group_key = gk.get(0).getGroupKey();
                share_gid = gk.get(0).getGroupid();
                share_vid = gk.get(0).getVersion_num();
                new PostShare(getContext(), original_post_id, share_text ).execute();

            }
            Log.d(" Share RequestAccept Something wrong with response", ""+gk);
        }

        @Override
        protected List<GroupKeyDetails> doInBackground(Void... voids) // gid, owner name
        {
            Call<List<GroupKeyDetails>> call_ = Api.getClient().getMyGroupKey(current_username);
            try {
                Response<List<GroupKeyDetails>> resp = call_.execute();
                Log.d(" Share RequestGot group key","in"+resp.body());
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(" Share RequestGot keys","Exception");
            }
            Log.d("Share Request Got keys","OUT");
            return null;

        }
    }

}
