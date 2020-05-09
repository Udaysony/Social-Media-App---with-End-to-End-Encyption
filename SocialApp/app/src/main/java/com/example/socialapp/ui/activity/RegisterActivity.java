package com.example.socialapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.models.GroupKeyDetails;
import com.example.socialapp.models.GroupStatusDetails;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.utils.CommonUtils;
import com.example.socialapp.utils.Security_Key_message;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.KeyGenerator;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText confirm_password;
    EditText email;
    EditText mobile;
    EditText firstname;
    EditText lastname;
    Button register;
    private int defaultGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



//        Toast.makeText(getApplicationContext(), Integer.toHexString(System.identityHashCode(private_key)) , Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), Integer.toHexString(System.identityHashCode(public_key)) , Toast.LENGTH_LONG).show();

        firstname = (EditText) findViewById(R.id.register_firstname);
        lastname = (EditText) findViewById(R.id.register_lastname);

        email = (EditText) findViewById(R.id.register_email);
        mobile = (EditText) findViewById(R.id.register_mobile_no);
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        confirm_password = (EditText) findViewById(R.id.register_confirm_password);

        register = (Button) findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(username.getText().toString())) {

                    Toast.makeText(getApplicationContext(), "Try again....!!!", Toast.LENGTH_SHORT).show();

                } else {
                    if (CommonUtils.checkInternetConnection(RegisterActivity.this)) {
                        // Create Group Status Table Entry

                        // Cretae Group Table Entry

                        AddUserApi addUser = new AddUserApi(RegisterActivity.this);
                        addUser.execute();


                    } else {
                        Toast.makeText(getApplicationContext(), "Something wrong with Internet Connection !", Toast.LENGTH_SHORT).show();
                    }
                }
//                Intent login_intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                //if given credentials are TRUE than start intent or back to login activity
//                //code the logic
//                startActivity(login_intent);
            }
        });

    }

    class AddUserApi extends AsyncTask<Void, Void, String> {
        Context context;
        UserDetails userDetails = new UserDetails();
        ProgressDialog pd;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        byte [] defaultGroupKey;
        byte[] PrivateSecretKey;
        byte[] private_key_bytes;
        String encoded_group_key = "None";
        String str_key;
        String alias = username.getText().toString() + "RSA_Keys";

        public AddUserApi(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Registering user...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

            // Asymetric Key generation/////
            KeyPairGenerator keyPairGenerator = null;
            Security_Key_message get_key = new Security_Key_message();


            try {
                keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA,"AndroidKeyStore");
                final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setKeySize(2048)
                        .build();
                keyPairGenerator.initialize(keyGenParameterSpec);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                PublicKey public_key = keyPair.getPublic();
                byte[] byte_arr = public_key.getEncoded();
                str_key = Base64.getEncoder().encodeToString(byte_arr);
                editor.putString("RSAPublicKey", str_key);
                editor.commit();


                defaultGroupKey= get_key.getGroupKey();
                Log.d("getGroupKey", defaultGroupKey.toString());
                encoded_group_key = get_key.encrypt_With_publicKey(defaultGroupKey , public_key);
                editor.putString("DefaultGroupKey", encoded_group_key);
                editor.commit();

                PrivateSecretKey = get_key.getPrivateSecretKey();
                editor.putString("PrivateSecretKey", PrivateSecretKey.toString());
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }



            userDetails.setUserPublicKey(str_key);
            userDetails.setTelephone(mobile.getText().toString());
            userDetails.setUserName(username.getText().toString());
            userDetails.setLastName(lastname.getText().toString());
            userDetails.setFirstName(firstname.getText().toString());
            userDetails.setEmailId(email.getText().toString());
            userDetails.setPassword(password.getText().toString());

            // Group Key Generation




        }

        @Override
        protected void onPostExecute(String statusCode) {
//            hideProgressDialog();
            pd.dismiss();
            if (statusCode.equals("OK")) {

                Toast.makeText(getApplicationContext(), "Registered Successfully...!", Toast.LENGTH_SHORT).show();
                Intent my_intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(my_intent);
                finish();

            } else {

//                showSnackBar(getString(R.string.server_error), findViewById(R.id.activity_view_admin_coordinatorLayout));
                Toast.makeText(getApplicationContext(), "Error while onPostExecute ...!" + statusCode, Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Call<String> callAddAdmin = Api.getClient().addUser(userDetails,encoded_group_key);
            try {
                Response<String> respCallAdmin = callAddAdmin.execute();
                return respCallAdmin.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

//    class CreateDefaultGroupAPI extends AsyncTask<Void, Void, String>{
//        GroupDetails groupDetails = new GroupDetails();
//        Context mContext;
//        ProgressDialog pd;
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();
//
//        public  CreateDefaultGroupAPI( Context ctx) {mContext = ctx;}
//
//        @Override
//        protected void onPreExecute() {
//            pd = new ProgressDialog(RegisterActivity.this);
//            pd.setCancelable(false);
//            pd.setMessage("Creating Friends Group...");
//            pd.getWindow().setGravity(Gravity.CENTER);
//            pd.show();
//
//            groupDetails.setUserName(username.getText().toString());
//            groupDetails.setgroupid(defaultGroupID);
//            groupDetails.setgroupname(username.getText().toString() + "Friends");
//            groupDetails.setisOwner("yes");
//            groupDetails.setIsFriend("yes");
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            pd.dismiss();
//            if (s.equals("OK")) {
//                //Add user Api
//            }
//            else{
//                Toast.makeText(getApplicationContext(), "Please Try again...", Toast.LENGTH_SHORT);
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            GroupDetails gd = new GroupDetails();
//            Call<String> postGroup = Api.getClient().CreateGroup(groupDetails);
//            try{
//                Response<String> resp = postGroup.execute();
//                if (resp.body() != null){
//                    return resp.body();
//                }
//
//            }catch(Exception e) {
//                 e.printStackTrace();
//            }
//            return "failed";
//            }
//        }
//        // Entery in Group Status , extract GroupID
//
//    class EntryInGroupStatusTableAPI extends AsyncTask<Void, Void, GroupStatusDetails> {
//        Context ctx;
//        GroupStatusDetails gs = new GroupStatusDetails();
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();
//
//        public EntryInGroupStatusTableAPI(Context c) {
//            ctx = c;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            gs.setStatus(0);
//        }
//
//        @Override
//        protected void onPostExecute(GroupStatusDetails s) {
//            if (s != null) {
//                defaultGroupID = s.getGroupid();
//                editor.putString("defaultGroupID", String.valueOf(defaultGroupID));
//                new CreateDefaultGroupAPI(ctx).execute();
//
//            }
//        }
//
//        @Override
//        protected GroupStatusDetails doInBackground(Void... voids) {
//            GroupStatusDetails newGs = new GroupStatusDetails();
//            Call<GroupStatusDetails> call_ = Api.getClient().AddAgroup(gs);
//            try {
//                Response<GroupStatusDetails> resp = call_.execute();
//                newGs = resp.body();
//                return newGs;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return newGs;
//        }
//    }
}
