package com.example.socialapp.ui.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.socialapp.R;
import com.example.socialapp.models.GroupDetails;
import com.example.socialapp.ui.adapter.GroupSearchAdapter;
import com.example.socialapp.webservice.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import retrofit2.Call;
import retrofit2.Response;

public class GroupFragment extends Fragment {

    View myFragementView;
    Button cur_groups;
    Button search_groups;
    Button group_reqs;
    String found = "N";

    ArrayList<GroupDetails> GroupResults = new ArrayList<GroupDetails>();

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragementView = inflater.inflate(R.layout.fragment_group, container, false);
        cur_groups = (Button)myFragementView.findViewById(R.id.cur_groups);
        search_groups = (Button)myFragementView.findViewById(R.id.new_groups_search);
        group_reqs = (Button)myFragementView.findViewById(R.id.new_group_requests);



        cur_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                GroupCurrentFragment fragment = new GroupCurrentFragment();
                fm.beginTransaction().add(R.id.groups_fragment,fragment).commit();
            }

        });

        search_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                GroupSearchFragment fragment = new GroupSearchFragment();
                fm.beginTransaction().add(R.id.groups_fragment,fragment).commit();
            }

        });

        group_reqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                GroupRequestFragment fragment = new GroupRequestFragment();
                fm.beginTransaction().add(R.id.groups_fragment,fragment).commit();

            }
        });
        return myFragementView;
    }
}

