package com.example.socialapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.socialapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FriendsFragment extends Fragment {
    View myFragementView;
    Button cur_fr;
    Button friend_reqs;
    Button search_fr;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragementView = inflater.inflate(R.layout.fragment_friends, container, false);
        cur_fr = (Button)myFragementView.findViewById(R.id.cur_friends);
        search_fr = (Button)myFragementView.findViewById(R.id.new_friends_search);
        friend_reqs = (Button)myFragementView.findViewById(R.id.new_friends_requests);



        cur_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                FriendsCurrentFragment fragment = new FriendsCurrentFragment();
                fm.beginTransaction().add(R.id.friends_fragment,fragment).commit();
            }

        });

        search_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                FriendsSearchFragment fragment = new FriendsSearchFragment();
                fm.beginTransaction().add(R.id.friends_fragment,fragment).commit();
            }

        });

        friend_reqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                FriendsRequestsFragment fragment = new FriendsRequestsFragment();
                fm.beginTransaction().add(R.id.friends_fragment,fragment).commit();

            }
        });
     return myFragementView;
    }
}
