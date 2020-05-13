package com.example.socialapp.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Response;

public class GroupCurrentAdapter extends ArrayAdapter<GroupDetails> {

    private LayoutInflater mInflater;
    private List<GroupDetails> groupList; //used for the search bar
    private int count;
    //    Typeface type;
    private Context mContext;
    private int gid,vid;
    private String gname;
    private String user_name;


    public GroupCurrentAdapter(@NonNull Context context, int x, List<GroupDetails>  users) {
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
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        final GroupDetails tempUser = groupList.get(position);
//        Log.d("TAG_Current_Group_Adapter", tempUser.getUserName().toString());
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.groups_listview_layout,null);
            holder = new ViewHolder();
            holder.user_name = (TextView) convertView.findViewById(R.id.found_group_name);
            holder.remove_btn = (Button)convertView.findViewById(R.id.remove_group);
            convertView.setTag((holder));

            holder.remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gid = tempUser.getgroupid();
                    gname = tempUser.getgroupname();
                    user_name = tempUser.getUserName();
                    new RemoveGroupAPI(getContext()).execute();

                    groupList.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.user_name.setText(tempUser.getgroupname());
        return convertView;
    }

    static class ViewHolder{
        TextView user_name;
        Button remove_btn;
    }


    class RemoveGroupAPI extends AsyncTask<Void, Void, String> {
        Context ctx;
        ProgressDialog pd;

        public RemoveGroupAPI (Context c){ctx=c;}

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getContext());
            pd.setCancelable(false);
            pd.setMessage("Removing Friend...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if(s.equals("removed")){
//                Toast.makeText(getContext(), "Removed " + gname + " successfully!", Toast.LENGTH_SHORT);
                Log.d("Friend Remove ", "Success");
//                new UpdateStatusTodirty(ctx).execute();
            }
            else{
//                Toast.makeText(getContext(), "Remove Error", Toast.LENGTH_SHORT);
                Log.d("Friend Remove ", "error" + s);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> call_ = Api.getClient().RemoveGroup(gid, gname, user_name);

            try {
                Response<String> resp = call_.execute();
                Log.d("Friend Remove ", "in background");
                return resp.body();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Friend Remove ", "exception");
            }
            Log.d("Friend Remove ", "Out");
            return null;
        }
    }

//    class UpdateStatusTodirty extends AsyncTask<Void, Void, String>{
//        Context ctx;
//
//        public UpdateStatusTodirty(Context x){ctx=x;}
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if(s.equals("ok")){
//                Log.d("Version updated to dirty  ", "o");
//            }
//            else{
//                Log.d("Version update dirty problem ", s);
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            Call<String> call_ = Api.getClient().updateStatusdirty(gid);
//
//            try {
//                Response<String> resp = call_.execute();
//                Log.d("Version updated to dirty  ", "in background");
//                return resp.body();
//            } catch (IOException e) {
//                Log.d("Version updated to dirty  ", "Exception");
//                e.printStackTrace();
//            }
//            Log.d("Version updated to dirty  ", "Out");
//            return null;
//        }
//    }


}
