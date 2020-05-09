package com.example.socialapp.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.R;
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
    SocialActivity sc = new SocialActivity();
    private HashMap<String,String> group_key_has = new HashMap<String, String>();
    Security_Key_message key_helper = new Security_Key_message();
    SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);




    public TimelineAdapter(@NonNull Context context, int x, List<PostDetails>  posts) {
        super(context, x, posts);
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


        PostDetails tempUser = postsList.get(position);
        Log.d("In adapter getting text  ", tempUser.getPostText());
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.timeline_listview_layout,null);
            holder = new ViewHolder();
            holder.user_name = (TextView) convertView.findViewById(R.id.post_user_name);
            holder.post_time = (TextView)convertView.findViewById(R.id.post_time);
            holder.post_text = (TextView)convertView.findViewById(R.id.post_text);
            holder.post_btn = (Button) convertView.findViewById(R.id.post_share);

            convertView.setTag((holder));

            holder.post_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Share this post event triggure",  Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.user_name.setText(tempUser.getOwnerusername());
        Log.d("Decoding ", "POSTS");
        String  error_ = "Start ";
        // Get group key for gid and username
        grp_id = tempUser.getGroupid();
        // start decrypting process
        if (group_key_has.containsKey(String.valueOf(grp_id))){
            group_key = group_key_has.get(String.valueOf(grp_id));
        }
        else{
            try {
                group_key = new GetGroupKey(getContext()).execute().get(); // to get over with Async task of fetching group key and continueing the rest of the code
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("PostDecode", "Got Group key form database" + " " + group_key);
        //Got the post Text
        String encrypted_post_text = tempUser.getPostText();
        String encrypted_session_key = tempUser.getSessionKey();
        // decrypt group with private key
        byte[] decrypt_group_key = new byte[0];
        try {

            decrypt_group_key = key_helper.decrept_With_privateKey(group_key, privateKey);
            Log.d("PostDecode", "Private key sucess");
        } catch (Exception e) {
            Log.d("PostDecode", "Private Key Exception");
            e.printStackTrace();
        }

        byte[] encrypted_sessKey = Base64.getDecoder().decode(encrypted_session_key);
        byte[] sess_key = new byte[0];

        try {
            sess_key = key_helper.decrypt(decrypt_group_key, encrypted_sessKey);
            Log.d("PostDecode", "Got sess key  sucess");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PostDecode", "GSss key Exception");
        }

        String out_text = "Decode error";

        try {
             out_text = key_helper.decrypt(encrypted_post_text,sess_key);
            Log.d("PostDecode", "Post text Sucess");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("PostDecode", "Post text Exception");
        }


        //Code to decrypt post ,1 get session key, 2 get post

        holder.post_text.setText(out_text);
        String timestampAsString = tempUser.getTimestamp().toString();
        Log.d("Timestamp", timestampAsString);
        holder.post_time.setText(timestampAsString);

//        holder.user_name.setTypeface(type);
//        holder.user_name.setText(tempUser.getUserName());
//        holder.add_friend.setTypeface(type);

        return convertView;
    }

    static class ViewHolder{
        TextView user_name;
        TextView post_time;
        TextView post_text;
        Button post_btn;
    }

    class GetGroupKey extends AsyncTask<Void, Void, String> {
        Context ctx;

        public GetGroupKey (Context x){ctx=x;}

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
                group_key_has.put(String.valueOf(grp_id), s);

            }
            Log.d("Request Accept Something wrong with response", ""+s);
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
}
