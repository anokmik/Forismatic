package com.slobodastudio.forismaticqoutations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;

public class QuotationsAdapter extends ArrayAdapter<QuotationData> {
	Context context;
	int layoutResource;
	List<QuotationData> items;
	
	public QuotationsAdapter(Context context, int layoutResource,  List<QuotationData> items) {
		super(context, layoutResource, items);
		this.context = context;
		this.layoutResource = layoutResource;
		this.items = items;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}
	
	@Override
	public QuotationData getItem(int position) {
		return items.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}
	
	static class ViewHolderAll {
		TextView quotationTextViewAll;
		TextView authorTextViewAll;
		CheckBox favouriteCheckBoxAll;
	}
	
	static class ViewHolderFav {
		TextView quotationTextViewFav;
		TextView authorTextViewFav;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (layoutResource == R.layout.listview_row) {
			ViewHolderAll viewHolder;
			String quotation = (String) getItem(position).getQuotation();
			String author = (String) getItem(position).getAuthor();
			Boolean favourite = (Boolean) getItem(position).getFavourite();
			
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(layoutResource, parent, false);
				viewHolder = new ViewHolderAll();
				viewHolder.quotationTextViewAll = (TextView) convertView.findViewById(R.id.quotationTextAtList);
				viewHolder.authorTextViewAll = (TextView) convertView.findViewById(R.id.quotationAuthorAtList);
				viewHolder.favouriteCheckBoxAll = (CheckBox) convertView.findViewById(R.id.quotationFavouriteAtList);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolderAll) convertView.getTag();
			}
			viewHolder.quotationTextViewAll.setText(quotation);
			viewHolder.authorTextViewAll.setText(author);
			viewHolder.favouriteCheckBoxAll.setChecked(favourite);
			//For testing purposes
			viewHolder.favouriteCheckBoxAll.setTag(position);
			//For testing purposes
		} else if (layoutResource == R.layout.listview_row_fav) {
			ViewHolderFav viewHolder;
			String quotation = (String) getItem(position).getQuotation();
			String author = (String) getItem(position).getAuthor();
			
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(layoutResource, parent, false);
				viewHolder = new ViewHolderFav();
				viewHolder.quotationTextViewFav = (TextView) convertView.findViewById(R.id.quotationTextAtListFav);
				viewHolder.authorTextViewFav = (TextView) convertView.findViewById(R.id.quotationAuthorAtListFav);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolderFav) convertView.getTag();
			}
			viewHolder.quotationTextViewFav.setText(quotation);
			viewHolder.authorTextViewFav.setText(author);
		}
		return convertView;
	}
	
}