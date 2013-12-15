package edu.sdu.fitsport;

import edu.sdu.fitsport.AktiviteFragmentTab;
import edu.sdu.fitsport.HaritaFragmentTab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	final int SAYFA_SAYI = 2;
	private String basliklar[] = new String[] { "Aktivite", "Harita" };

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			AktiviteFragmentTab fragmenttab1 = new AktiviteFragmentTab();
			return fragmenttab1;
		case 1:
			HaritaFragmentTab fragmenttab2 = new HaritaFragmentTab();
			
			return fragmenttab2;
		}
		return null;
	}

	public CharSequence getPageTitle(int position) {
		return basliklar[position];
	}

	@Override
	public int getCount() {
		return SAYFA_SAYI;
	}

}