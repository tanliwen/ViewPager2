package com.smooth.conflict.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.integration.testapp.R;
import androidx.viewpager2.widget.ViewPager2;

/*
 *  @author      : xunlei
 *  @date        : 2022/12/8
 *  @desc        : 1.0
 */
public class FindXFragment extends Fragment {

    public Fragment[] fragments;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_findx, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragments = new Fragment[]{
                new CardFragment(),
//                new MainLiveFragment(), //现有非smart实现
                new CardFragment(),
                new MainLiveFragmentNew(), //smart androiddx 1.1.0 实现
                new CardFragment(),
        };

        ViewPager2 pager2 = view.findViewById(R.id.viewPager);

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments[position];
            }

            @Override
            public int getItemCount() {
                return fragments.length;
            }
        };
        pager2.setAdapter(adapter);
    }
}
