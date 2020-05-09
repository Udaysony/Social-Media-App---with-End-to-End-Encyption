package com.example.socialapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.example.socialapp.ui.fragments.FriendsFragment;
import com.example.socialapp.ui.fragments.GroupFragment;
import com.example.socialapp.ui.fragments.ProfileFragement;
import com.example.socialapp.ui.fragments.TimelineFragment;
import com.google.android.material.navigation.NavigationView;
import com.example.socialapp.utils.Security_Key_message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;
//import android.widget.Toolbar;

public class SocialActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public String cur_user;
    public byte[] sessionKey;
    public byte[] defaultGroupKey;
    public byte[] PrivateSecretKey;
    public PrivateKey privateKey;
//    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);


        //Code to get Secret / Session Key for this Session
        Security_Key_message get_key = new Security_Key_message();
        SharedPreferences pref =  getApplicationContext().getSharedPreferences("MyPref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String u_name = pref.getString("username","no");
        try {
            sessionKey = get_key.getSessionKey();
        } catch (Exception e) {
            e.printStackTrace();
        }



        String gk = pref.getString("DefaultGroupKey", "Uday");
        Log.d("Default Group Key from pref): ", gk);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer= findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container,
                new TimelineFragment()).commit();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
         switch (menuItem.getItemId()){
             case R.id.nav_Profile:
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container,
                         new ProfileFragement()).commit();
                 break;

             case R.id.nav_Timeline:
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container,
                         new TimelineFragment()).commit();
                 break;

             case R.id.nav_friends:
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container,
                         new FriendsFragment()).commit();
                 break;

             case R.id.nav_groups:
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container,
                         new GroupFragment()).commit();
                 break;

             case R.id.logout:
                 SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                 SharedPreferences.Editor editor = pref.edit();
                 editor.remove("isLogin");
                 editor.remove("username");
                 editor.commit();
                 Intent myintent = new Intent(SocialActivity.this, LoginActivity.class);
                 startActivity(myintent);
                 finish();
                 break;

         }
         drawer.closeDrawer(GravityCompat.START);
         return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
