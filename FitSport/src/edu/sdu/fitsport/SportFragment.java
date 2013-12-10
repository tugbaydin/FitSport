package edu.sdu.fitsport;

import java.lang.reflect.Field;

import com.actionbarsherlock.app.SherlockFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SportFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.viewpager_main, container, false);
		// Tablar için ViewPagerý al
		ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		// ViewPager adapter ayarla
		mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
