package edu.sdu.fitsport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {

	Context context;
	String[] basliklar;
	String[] altBasliklar;
	int[] icon;
	LayoutInflater inflater;

	public MenuListAdapter(Context context, String[] basliklar, String[] altBasliklar,
			int[] icon) {
		this.context = context;
		this.basliklar = basliklar;
		this.altBasliklar = altBasliklar;
		this.icon = icon;
	}

	@Override
	public int getCount() {
		return basliklar.length;
	}

	@Override
	public Object getItem(int position) {
		return basliklar[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView txtBaslik;
		TextView txtAltBaslik;
		ImageView imgIcon;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.drawer_list_item, parent,
				false);

		//drawer_list_item.xml den bileþenleri al
		txtBaslik = (TextView) itemView.findViewById(R.id.baslik);
		txtAltBaslik = (TextView) itemView.findViewById(R.id.altbaslik);

		//drawer_list_item.xml ImageView al
		imgIcon = (ImageView) itemView.findViewById(R.id.icon);

		txtBaslik.setText(basliklar[position]);
		txtAltBaslik.setText(altBasliklar[position]);

		imgIcon.setImageResource(icon[position]);

		return itemView;
	}

}
