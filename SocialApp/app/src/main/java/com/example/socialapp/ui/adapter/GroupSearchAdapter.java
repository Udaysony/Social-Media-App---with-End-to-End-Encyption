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
import com.example.socialapp.models.GroupStatusDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.webservice.Api;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class GroupSearchAdapter extends ArrayAdapter<GroupDetails> {

    private LayoutInflater mInflater;
    private List<GroupDetails> groupList; //used for the search bar
    private int count;
    //    Typeface type;
    private Context mContext;
    private int g_id;
    private String from_user;
    private String to_user;
    private String gname;
    public GroupSearchAdapter(@NonNull Context context, int x, List<GroupDetails>  users) {
        super(context,x, users);
        mInflater = LayoutInflater.from(context);
        this.groupList = users;
        this.mContext = context;
        Log.d("TAG Group Search Adapter", "Called Adapter");
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    @Override
    public GroupDetails getItem(int position) {
        // TODO Auto-generated method stub
        return groupList.get(position);
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
        final GroupDetails tempUser = groupList.get(position);
        Log.d("In adapter", tempUser.getgroupname().toString());
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.new_group_search_listview_layout,null);
            holder = new GroupSearchAdapter.ViewHolder();
            holder.group_name = (TextView) convertView.findViewById(R.id.found_group_user_name);
            holder.add_btn = (Button)convertView.findViewById(R.id.join_group);
            Log.d("In adapter", holder.group_name.toString());
            convertView.setTag((holder));

            holder.add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    g_id = tempUser.getgroupid();
                    to_user = tempUser.getUserName();
                    gname = tempUser.getgroupname();
                    Toast.makeText(getContext(), "Add group " + gname,  Toast.LENGTH_SHORT).show();
                    new sendJoinRequest(getContext()).execute();

                }
            });
        }
        else{
            holder = (GroupSearchAdapter.ViewHolder) convertView.getTag();
        }

        holder.group_name.setText(tempUser.getgroupname());
//        holder.user_name.setTypeface(type);
//        holder.user_name.setText(tempUser.getUserName());
//        holder.add_friend.setTypeface(type);

        return convertView;
    }

    static class ViewHolder{
        TextView group_name;
        Button add_btn;
    }


    class sendJoinRequest extends AsyncTask<Void, Void , String> {
        Context context;
        GroupInvitationDetails userDetails = new GroupInvitationDetails();
        ProgressDialog pd;
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        public sendJoinRequest(Context ctx){context = ctx;}

        @Override
        protected void onPreExecute(){
            userDetails.setGroupid(g_id);
            userDetails.setGroupname(gname);
            userDetails.setIsFriend("no");
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
