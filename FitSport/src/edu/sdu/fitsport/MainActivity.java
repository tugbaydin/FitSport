package edu.sdu.fitsport;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.sdu.fitsport.GecmisFragment;
import edu.sdu.fitsport.SportFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.view.GravityCompat;

public class MainActivity extends SherlockFragmentActivity {

	/*Yan Menu*/
	DrawerLayout drawerLayout;
	/*Menu i�indeki itemler*/
	ListView drawerLv;
	ActionBarDrawerToggle drawerToggle;
	/*Menu Elemanlar�n� G�sterimi*/
	MenuListAdapter menuAdapter;
	/*Menu ba�l�k ve alt ba�l�klar�*/
	String[] baslik;
	String[] altBaslik;
	int[] icon;
	/*DrawerLayouttan Gidilecek Fragmentlar*/
	Fragment gecmisFg = new GecmisFragment();
	Fragment sportFg = new SportFragment();
	/*Fragment ba�l���*/
	private CharSequence drawerBaslik;
	private CharSequence mBaslik;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		mBaslik = drawerBaslik = getTitle();

		//Menu ba�l�klar�
		baslik = new String[] { "Spor", "Ge�mi�" };
		altBaslik = new String[] { "Spor Aktiviteleri", "Ge�mi� �al��malar" };

		icon = new int[] { R.drawable.action_about, R.drawable.action_settings };

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLv = (ListView) findViewById(R.id.listview_drawer);

		/*
		 * DrawerLayout a��l�nca g�lge efekti*/
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		menuAdapter = new MenuListAdapter(MainActivity.this, baslik, altBaslik,
				icon);
		drawerLv.setAdapter(menuAdapter);

		//ListView itemleri i�in Click Event
		drawerLv.setOnItemClickListener(new DrawerItemClickListener());

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				//Fragment�n ba�l���n� ayarlama
				getSupportActionBar().setTitle(drawerBaslik);
				super.onDrawerOpened(drawerView);
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*if (item.getItemId() == android.R.id.home) {

			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}
*/
		return super.onOptionsItemSelected(item);
	}

	//Navigation Drawer i�indeki ListView i�in ItemClick
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (position) {
		case 0:
			ft.replace(R.id.content_frame, sportFg);
			break;
		case 1:
			ft.replace(R.id.content_frame, gecmisFg);
			break;
		}
		ft.commit();
		drawerLv.setItemChecked(position, true);
		setTitle(baslik[position]);
		//Se�ildikten sonra kapat
		drawerLayout.closeDrawer(drawerLv);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// onRestoreInstanceState tetiklendikte sonra senkronize et
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// yeni ayarlar� toggle at
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void setTitle(CharSequence title) {
		mBaslik = title;
		getSupportActionBar().setTitle(mBaslik);
	}

	@Override
	public void onBackPressed() {

		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() > 0) {
			/*Geri alma i�lemi i�in FragmentActivity d�n*/
			manager.popBackStack();

		} else {
			//Di�er durumlarda kullan�c�ya sor
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Bildirim")
        .setIcon(R.drawable.ic_compose_inverse)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
}
