package com.rk.amii.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rk.amii.R;
import com.rk.amii.adapters.ViewPagerAdapter;
import com.rk.amii.fragments.FaqFragment;
import com.rk.amii.fragments.HowToFragment;

public class NotificationsFragment extends Fragment {

    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FaqFragment faqFragment;
    private HowToFragment howToFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);

        loadView();

        return view;
    }

    private void loadView() {
        viewPager = view.findViewById(R.id.tabViewer);
        tabLayout = view.findViewById(R.id.tabLayout);

        faqFragment = new FaqFragment();
        howToFragment = new HowToFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),0);

        viewPagerAdapter.addFragment(faqFragment, "FAQ");
        viewPagerAdapter.addFragment(howToFragment, "Training and Education Videos");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        loadView();
        super.onResume();
    }
}