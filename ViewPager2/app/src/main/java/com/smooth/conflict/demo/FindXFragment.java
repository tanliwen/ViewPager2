package com.smooth.conflict.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.integration.testapp.R;
import androidx.viewpager2.widget.ViewPager2;

import com.xunlei.tdlive.pulltorefresh.LogTag;

import java.lang.reflect.Field;

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
//        changeSlop(pager2);

        if(pager2.getChildAt(0) instanceof RecyclerView){
            ((RecyclerView)(pager2.getChildAt(0))).setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);
        }


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

    public static void changeSlop(ViewPager2 vp){
        try {
            final Field recyclerViewField = vp.getClass().getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);
            final RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(vp);//vb.viewpagerHome为要改变滑动距离的viewpager2控件

            final Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);

            final int touchSlop = (int) touchSlopField.get(recyclerView);

            Log.e(LogTag.TAG_LIVE_SMOOTH, "touchSlop = " + touchSlop + ", touchSlop 3 = " + touchSlop * 3);
            touchSlopField.set(recyclerView, touchSlop * 3);
        } catch (Exception ignore) {
        }
    }
}
