package com.example.socialapp.ui.adapter;

import android.annotation.SuppressLint;
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
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.webservice.Api;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class FriendsSearchAdapter extends ArrayAdapter<UserDetails> {
    private LayoutInflater mInflater;
    private List<UserDetails> friendList; //used for the search bar
    private int count;
    private String to_user;
    private int grp_id;
//    Typeface type;
    private Context mContext;


    public FriendsSearchAdapter(@NonNull Context context, int x, List<UserDetails>  users) {
        super(context,x, users);
        mInflater = LayoutInflater.from(context);
       this.friendList = users;
       this.mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return friendList.size();
    }

    @Override
    public UserDetails getItem(int position) {
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
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        UserDetails tempUser = friendList.get(position);
        Log.d("In adapter", tempUser.getUserName().toString());
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.friends_listview_layout,null);
            holder = new ViewHolder();
            holder.user_name = (TextView) convertView.findViewById(R.id.found_user_name);
            holder.add_friend = (Button)convertView.findViewById(R.id.add_friend);
            convertView.setTag((holder));

            holder.add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    to_user = holder.user_name.getText().toString();
                    Log.d("TAG in Sending request", to_user);
                    //Get Group ID to send
                    new GetGroupIdAPI(mContext).execute(to_user , to_user+"Friends");
                }
            });
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.user_name.setText(tempUser.getUserName());
//        holder.user_name.setTypeface(type);
//        holder.user_name.setText(tempUser.getUserName());
//        holder.add_friend.setTypeface(type);

        return convertView;
    }

    static class ViewHolder{
        TextView user_name;
        Button add_friend;
    }


    class GetGroupIdAPI extends AsyncTask<String, Void, GroupDetails> {
        Context context;
        public GetGroupIdAPI(Context mContext) {
            context = mContext;
        }

        @Override
        protected void onPostExecute(GroupDetails gid) {
            if (gid != null) {
                grp_id = gid.getgroupid();
                new SendFriendsRequest(mContext).execute();
            }
        }

        @Override
        protected GroupDetails doInBackground(String... params) {
            GroupDetails register = new GroupDetails();
            Call<GroupDetails> getGrpId = Api.getClient().getGroupid(params[0], params[1]); //username, Groupname
            try {
                Response<GroupDetails> response = getGrpId.execute();
                register = response.body();
                return register;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return register;
        }

    }


    class SendFriendsRequest extends AsyncTask<Void, Void , String> {
        Context context;
        GroupInvitationDetails userDetails = new GroupInvitationDetails();
        ProgressDialog pd;
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        public SendFriendsRequest(Context ctx){context = ctx;}

        @Override
        protected void onPreExecute(){
            if (grp_id != 0){
//                Toast.makeText(getContext(), "Group ID: " + String.valueOf(grp_id) ,Toast.LENGTH_SHORT).show();
                userDetails.setGroupid(grp_id);
            }
            else{
                Toast.makeText(getContext(), "Try again" ,Toast.LENGTH_SHORT).show();
            }
            userDetails.setGroupname(to_user + "Friends");
            userDetails.setIsFriend("yes");
            userDetails.setUsername_from(pref.getString("username", "NONE") );
            userDetails.setUsername_to(to_user);

            Log.d("Userdata sent request: ", String.valueOf(userDetails.getGroupid()) + " "+ userDetails.getUsername_from() + " "
            + userDetails.getUsername_to() + " " + userDetails.getGroupname() + " "+ userDetails.getIsFriend());

            pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.setMessage("Sending Friend Request...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

        }

        @Override
        protected void onPostExecute(String statusCode){
            if (statusCode.equals("SENT")) {
                pd.dismiss();
                Toast.makeText(getContext(), "Request " + statusCode, Toast.LENGTH_SHORT).show();
            } else {
                pd.dismiss();
                Toast.makeText(getContext(), "Error while sending request ...!" + statusCode, Toast.LENGTH_SHORT).show();

            }

        }
        @Override
        protected String doInBackground(Void...voids) {
            String status = "failed";
            Call<String> sendRequest = Api.getClient().addRequest(userDetails);
            try{
                Response<String> response = sendRequest.execute();

                status = response.body();
                Log.d("In sending request ", status);
                return status;
            }catch(Exception e){
                e.printStackTrace();
            }

            return status;
        }
    }

}


