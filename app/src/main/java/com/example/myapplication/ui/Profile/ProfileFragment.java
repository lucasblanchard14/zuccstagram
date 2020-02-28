package com.example.myapplication.ui.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.SectionsPageAdapter;
import com.example.myapplication.ui.Bio.BioFragment;
import com.example.myapplication.ui.Posts.PostsFragment;
import com.google.android.material.tabs.TabLayout;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {




        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        mSectionsPageAdapter = new SectionsPageAdapter(getChildFragmentManager());
        mViewPager = getView().findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = getView().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new PostsFragment(), "Posts");
        adapter.addFragment(new BioFragment(), "Bio");
        viewPager.setAdapter(adapter);
    }

}