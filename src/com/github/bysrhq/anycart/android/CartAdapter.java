package com.github.bysrhq.anycart.android;

import java.util.List;

import com.github.bysrhq.anycart.android.entity.TransactionDetail;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class CartAdapter extends ArrayAdapter<TransactionDetail> {
	private Activity activity;
	private List<TransactionDetail> transactionDetails;
	
	public CartAdapter(Activity activity, List<TransactionDetail> transactionDetails) {
		super(activity, R.layout.listview_cart, transactionDetails);

		this.activity = activity;
		this.transactionDetails = transactionDetails;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.listview_cart, null, true);
		
		TextView itemNameTextView = (TextView) view.findViewById(R.id.textview_item_name);
		itemNameTextView.setText(transactionDetails.get(position).getItem().getName());
		
		TextView itemPriceTextView = (TextView) view.findViewById(R.id.textview_item_price);
		itemPriceTextView.setText(String.valueOf(transactionDetails.get(position).getItem().getPrice()));

		TextView transactionDetailQuantityTextView = (TextView) view.findViewById(R.id.textview_transaction_detail_quantity);
		transactionDetailQuantityTextView.setText(String.valueOf(transactionDetails.get(position).getQuantity()));

		TextView transactionDetailSubTotalTextView = (TextView) view.findViewById(R.id.textview_transaction_detail_subtotal);
		transactionDetailSubTotalTextView.setText(String.valueOf(transactionDetails.get(position).getSubTotal()));
		
		return view;
	}

}
