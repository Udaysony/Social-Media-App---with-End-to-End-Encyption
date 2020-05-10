package com.example.socialapp.ui.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.AbstractThreadedSyncAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.models.BothKeys;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.models.GroupInvitationDetails;
import com.example.socialapp.models.GroupKeyDetails;
import com.example.socialapp.utils.Security_Key_message;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class GroupRequestAdapter extends ArrayAdapter<GroupInvitationDetails> {
    private LayoutInflater mInflater;
    private List<GroupInvitationDetails> groupList; //used for the search bar
    private int count;
    //    Typeface type;
    private Context mContext;
    private String new_user_public_key;
    private String group_key;
    private int gid;
    private String new_user;
    private String group_owner;
    private String group_name_;

    SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
    SharedPreferences.Editor editor = pref.edit();



    public GroupRequestAdapter(@NonNull Context context, int x, List<GroupInvitationDetails>  users) {
        super(context,x, users);
        mInflater = LayoutInflater.from(context);
        this.groupList = users;
        this.mContext = context;
//        SharedPreferences pref = get
//        Log.d("Username with pref: ", pref.getString("username", "None"));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    @Override
    public GroupInvitationDetails getItem(int position) {
        // TODO Auto-generated method stub
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        final GroupInvitationDetails tempUser = groupList.get(position);


//        Log.d("TAG_Current_Group_Adapter", tempUser.getUserName().toString());
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.new_group_join_listview_layout,null);
            holder = new ViewHolder();
            holder.user_name = (TextView) convertView.findViewById(R.id.new_group_user_name);
            holder.accept_join = (Button) convertView.findViewById(R.id.accept_in_group);
            holder.remove_btn = (Button) convertView.findViewById(R.id.not_accepted_in_group);
            convertView.setTag((holder));

            holder.accept_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new_user = tempUser.getUsername_from();
                    Log.d("RequestAccept", ""+new_user);
                    gid = tempUser.getGroupid();
                    group_owner = tempUser.getUsername_to();
                    group_name_ = tempUser.getGroupname();
                    Log.d("Request", "Accept click");
                    new GetPublicKeyofUser(getContext()).execute(new_user);

                    groupList.remove(position);
                    notifyDataSetChanged();

                }
            });

            holder.remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new_user = tempUser.getUsername_from();
                    Log.d("RequestAccept", ""+new_user);
                    gid = tempUser.getGroupid();
                    group_owner = tempUser.getUsername_to();
                    group_name_ = tempUser.getGroupname();
                    Log.d("Request", "Reject click");
                    new RemoveInvitation(getContext()).execute(group_owner, new_user, group_name_);

                    groupList.remove(position);
                    notifyDataSetChanged();

                }
            });
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }


        holder.user_name.setText(tempUser.getUsername_from() +" For "+tempUser.getGroupname());
        return convertView;
    }

    static class ViewHolder{
        TextView user_name;
        Button accept_join;
        Button remove_btn;
    }
    // on join press

    class AcceptRequest extends AsyncTask<Void , Void, String> {
        Context context;
        GroupKeyDetails groupKeyDetails = new GroupKeyDetails();
        ProgressDialog pd;
        Security_Key_message keys = new Security_Key_message();

        public AcceptRequest(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            PrivateKey privateKey;
            byte[] raw = new byte[0];
            byte[] pk = Base64.getDecoder().decode(new_user_public_key);
            String encoded_gk = "Encryption error";
            try {
                KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                ks.load(null);
                privateKey = (PrivateKey) ks.getKey(group_owner+ "RSA_Keys",null);

                PublicKey publicKey =
                        KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pk));

                raw = keys.decrept_With_privateKey(group_key,privateKey);
                encoded_gk = keys.encrypt_With_publicKey( raw, publicKey);

                Log.d(" Request Accept Gk encrypted with New Users Public Key: ", "success "+encoded_gk);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(" Request Accept Gk encrypted with New users Public Key: ", "Failed");
//                groupKeyDetails.setGroupKey("RequestAccept_encoded_gk");
            }

            groupKeyDetails.setGroupKey(encoded_gk);
            groupKeyDetails.setGroupid(gid); //from MyPref
            groupKeyDetails.setUsername(new_user);
            groupKeyDetails.setVersion_num(1); //Get max version_number api

            pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.setMessage("Accepting Request...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            pd.dismiss();
            if(s.contains("ok")) {
                Log.d("Request Accept: ", s);
                Toast.makeText(getContext(), "Accepted" , Toast.LENGTH_SHORT);
            }
            else{
                Log.d("Request Accept: ", "unsuccessful"+s);
            }

        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> acpt = Api.getClient().AcceptGroupRequest(groupKeyDetails, group_name_,group_owner);

            try{
                Response<String> resp = acpt.execute();
                return resp.body();
            }catch(Exception e){
                e.printStackTrace();
            }
            return "failed";
        }
    }

    class GetGroupKey extends AsyncTask<Void, Void, String>{
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
                group_key = s;
                Log.d("Request Accept Got group Keys", s);
                new AcceptRequest(getContext()).execute();
            }
            Log.d("Request Accept Something wrong with response", ""+s);
        }

        @Override
        protected String doInBackground(Void... voids) // gid, owner name
        {
            Call<String> call_ = Api.getClient().getGroupKey(gid, group_owner);
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

    class GetPublicKeyofUser extends AsyncTask<String, Void, String>{
        Context ctx;
        public GetPublicKeyofUser (Context c){ctx = c;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String userDetails) {
            if(userDetails != null) {
                new_user_public_key = userDetails;
                Log.d("Got public key", " OK "+new_user_public_key);
                new GetGroupKey(ctx).execute();
            }
            else{
                Log.d("Error in Public key Get:", "Error");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> call_ = Api.getClient().getUserPublicKey(params[0]);
            try{
                Response<String> resp = call_.execute();
                if (resp.body() != null ) {
                    return resp.body();
                }
            }catch(Exception e){
                e.printStackTrace();
                Log.d("TAG in Getting Public key","inBackground catch exception");
            }
            return "NO kry Fetched";
        }
    }
    class RemoveInvitation extends AsyncTask<String, Void, String>{
        Context ctx;

        public RemoveInvitation(Context x){ctx=x;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("ok")){
                Log.d("Removed friend request" , "ok");
            }
            else{
                Log.d("Something is wrong", "Could not remove friend request " + s);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Call<String> call_ = Api.getClient().removeInvitation(strings[0], strings[1], strings[2]);

            try {
                Response<String> resp = call_.execute();
                Log.d("Removed friend request" , "in background");
                return resp.body();
            } catch (IOException e) {
                Log.d("Removed friend request" , "Exception ");
                e.printStackTrace();
            }
            Log.d("Removed friend request" , "out");
            return "faild out";
        }
    }

}
