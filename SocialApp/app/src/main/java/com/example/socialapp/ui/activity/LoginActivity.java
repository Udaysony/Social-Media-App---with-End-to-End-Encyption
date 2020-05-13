package com.example.socialapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.models.UserDetails;
import com.example.socialapp.utils.CommonUtils;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText text_username;
    EditText text_password;
    Button login_button;
    TextView text_register;
    UserDetails register;
    public byte[] defaultGroupKey;
    public byte[] PrivateSecretKey;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        if (pref.contains("isLogin")){
            Intent mlaActivity = new Intent();
            mlaActivity.setClass(LoginActivity.this, SocialActivity.class);
            startActivity(mlaActivity);
            finish();

        }



        text_username = (EditText)findViewById(R.id.login_username);
        text_password = (EditText)findViewById(R.id.login_password);
//        Log.d("DATA GOT:   ",text_username.getText().toString()+"               "+ text_password.getText().toString()) ;
        login_button = (Button)findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(text_password.getText().toString()) || TextUtils.isEmpty(text_username.getText().toString())) {

                    Toast.makeText(getApplicationContext(), "Try again....!!!", Toast.LENGTH_SHORT).show();

                } else {
                    if (CommonUtils.checkInternetConnection(LoginActivity.this)) {
                        MLALoginAPI authentication = new MLALoginAPI(getApplicationContext());
                        authentication.execute(text_username.getText().toString(), text_password.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Something wrong with Internet Connection !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        text_register = (TextView)findViewById(R.id.login_register);
        text_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register_intent);
            }
        });
    }


    class MLALoginAPI extends AsyncTask<String, Void, UserDetails> {
        Context appContext;
        ProgressDialog pd;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();


        public MLALoginAPI (Context context) {
            appContext = context;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Verifying...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();

        }

        @Override
        protected void onPostExecute(UserDetails registerArg) {
            register = registerArg;

            pd.dismiss();
            if (register.username != null) {
                editor.putBoolean("isLogin", true);
                editor.putString("username", text_username.getText().toString());
                editor.commit();

                Intent mlaActivity = new Intent();
                mlaActivity.setClass(LoginActivity.this, SocialActivity.class);
                startActivity(mlaActivity);
                finish();


            } else {
                Toast.makeText(getApplicationContext(),"Please try again..!",Toast.LENGTH_SHORT).show();
//                pd.dismiss();
            }
        }

        @Override
        protected UserDetails doInBackground(String... params) {
            UserDetails register = new UserDetails();
            Call<List<UserDetails>> callAuth = Api.getClient().authenticate(params[0], params[1]);
            try {
                Response<List<UserDetails>> respAuth = callAuth.execute();
                if (respAuth != null && respAuth.isSuccessful() & respAuth.body() != null && respAuth.body().size() > 0) {
                    register = respAuth.body().get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return register;
        }
    }

}
