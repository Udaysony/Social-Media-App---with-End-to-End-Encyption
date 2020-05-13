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
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.models.GroupInvitationDetails;
import com.example.socialapp.models.GroupKeyDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.ui.activity.RegisterActivity;
import com.example.socialapp.ui.fragments.FriendsRequestsFragment;
import com.example.socialapp.utils.Security_Key_message;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class FriendsNewRequestsAdapter extends ArrayAdapter<GroupInvitationDetails> {
    private LayoutInflater mInflater;
    private List<GroupInvitationDetails> friendList; //used for the search bar
    private int count;
    //    Typeface type;
    private Context mContext;
    public String NewUsersPublicKey;
    public String new_user;
    public int group_id,version_id;
    String current_username;
    SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);


    public FriendsNewRequestsAdapter(@NonNull Context context, int x, List<GroupInvitationDetails>  users) {
        super(context,x, users);
        mInflater = LayoutInflater.from(context);
        this.friendList = users;
        this.mContext = context;
//        SharedPreferences pref = get
//        Log.d("Username with pref: ", pref.getString("username", "None"));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return friendList.size();
    }

    @Override
    public GroupInvitationDetails getItem(int position) {
        // TODO Auto-generated method stub
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        current_username = pref.getString("username" , "no");

        final GroupInvitationDetails tempUser = friendList.get(position);
        Log.d("TAG_Requests_Friends_Adapter", tempUser.getUsername_from().toString());
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.new_friend_request_listview,null);
            holder = new ViewHolder();
            holder.user_name = (TextView) convertView.findViewById(R.id.new_user_name);
            holder.accept = (Button) convertView.findViewById(R.id.accept_friend);
            holder.not_accept = (Button) convertView.findViewById(R.id.not_accept_friend);
            convertView.setTag((holder));

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new_user = holder.user_name.getText().toString();
                    Log.d("Request", "Accept click");
                        new GetGroupIdAPI(mContext).execute(
                            pref.getString("username","None"),
                            pref.getString("username","None")+"Friends");

                    Log.d("Request", holder.user_name.getText().toString());
                    Toast.makeText(getContext(), "Accepted" , Toast.LENGTH_SHORT);
                    friendList.remove(position);
                    notifyDataSetChanged();


                }
            });

            holder.not_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new_user = holder.user_name.getText().toString();
                    Log.d("Not accept Request", "click");
                    new RemoveInvitation(getContext()).execute( current_username , new_user, tempUser.getGroupname());
                    friendList.remove(position);
                    notifyDataSetChanged();


                }
            });
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.user_name.setText(tempUser.getUsername_from());


        return convertView;
    }

    static class ViewHolder{
        TextView user_name;
        Button accept;
        Button not_accept;
    }

    class AcceptRequest extends AsyncTask<Void , Void, String>{
        Context context;
        GroupKeyDetails groupKeyDetails = new GroupKeyDetails();
        ProgressDialog pd;
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        private String group_owner;
        Security_Key_message keys = new Security_Key_message();

        public AcceptRequest(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            group_owner = pref.getString("username", "None");
            Log.d("Owner of the Group", group_owner);


            String Defaultgk  = pref.getString("DefaultGroupKey", "None");
            byte[] raw = new byte[0];
            PrivateKey privateKey;
            byte[] pk = Base64.getDecoder().decode(NewUsersPublicKey);
            String encoded_gk;
            try {
                KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                ks.load(null);
                //KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(u_name+ "RSAkey", null);

                privateKey = (PrivateKey) ks.getKey(group_owner+ "RSA_Keys",null);

                PublicKey publicKey =
                        KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pk));

                raw = keys.decrept_With_privateKey(Defaultgk, privateKey );

                encoded_gk = keys.encrypt_With_publicKey(raw , publicKey);
                Log.d("Gk encrpted with New USers Public Key: ", "Sucess");
                groupKeyDetails.setGroupKey(encoded_gk);

            } catch (Exception e) {
                e.printStackTrace();
                groupKeyDetails.setGroupKey("encoded_gk");
            }
             //from MyPref
            Log.d("Current Users GroupID: ",String.valueOf(group_id));
            groupKeyDetails.setGroupid(group_id);
            groupKeyDetails.setUsername(new_user);
            groupKeyDetails.setVersion_num(version_id); //Get max version_number api

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
            Log.d("Result of Request ACCEPT: ", s);

        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> acpt = Api.getClient().AcceptRequest(groupKeyDetails, group_owner);

            try{
                Response<String> resp = acpt.execute();
                return resp.body();
            }catch(Exception e){
                e.printStackTrace();
            }
            return "failed";
        }
    }

    class GetGroupIdAPI extends AsyncTask<String, Void, GroupDetails> {
        Context context;
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        public GetGroupIdAPI(Context mContext) {
            context = mContext;
        }

        @Override
        protected void onPostExecute(GroupDetails gid) {
            if (gid != null) {
                    group_id = gid.getgroupid();

                try {
                    version_id = new GetMaxVersion(getContext()).execute().get(); // to hold code here
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                new GetPublicKeyofUser(mContext).execute(new_user);
            }
            Log.d("Error while in Getting group ID","");
        }

        @Override
        protected GroupDetails doInBackground(String... params) {
            GroupDetails register = new GroupDetails();
            Call<GroupDetails> getGrpId = Api.getClient().getGroupid(params[0], params[1]); //username , groupname
            try {
                Response<GroupDetails> response = getGrpId.execute();
                register = response.body();
                Log.d("Group Id fetched", "n");
                return register;
            } catch (Exception e) {
                e.printStackTrace();

            }
            Log.d("Group Id !!!! ? fetched", "Error");
            return register;
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
                NewUsersPublicKey = userDetails.toString();
                Log.d("New users Public Key: ", NewUsersPublicKey);
                new AcceptRequest(ctx).execute();
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
                version_id = vnumber;
                Log.d("Version get", "Success");
            }
            else{
                Log.d("Version get", "Null");
            }
        }

        @Override
        protected Integer doInBackground(Void... strings) {
            Call<Integer> call_ = Api.getClient().getMaxVersion(group_id);

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
    } // yet to implement API in server =-- DONE

}
