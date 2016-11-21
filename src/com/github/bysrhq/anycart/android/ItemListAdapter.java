package com.github.bysrhq.anycart.android;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.bysrhq.anycart.android.entity.Item;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListAdapter extends ArrayAdapter<Item> {
	private List<Item> items;
	private Activity activity;
	public ItemListAdapter(Activity activity, List<Item> items) {
		super(activity, R.layout.listview_item_list, items);
		// TODO Auto-generated constructor stub
		
		this.items = items;
		this.activity = activity;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.listview_item_list, null, true);
		
		Log.d("File ItemListAdapter", items.get(position).getName());
		
		TextView itemNameTextView = (TextView) view.findViewById(R.id.textview_item_name);
		itemNameTextView.setText(items.get(position).getName());

		TextView itemPriceTextView = (TextView) view.findViewById(R.id.textview_item_price);
		itemPriceTextView.setText(String.valueOf(items.get(position).getPrice()));
		
		TextView itemQuantityTextView = (TextView) view.findViewById(R.id.textview_item_quantity);
		itemQuantityTextView.setText(String.valueOf(items.get(position).getQuantity()));
		
		ImageView itemImageImageView = (ImageView) view.findViewById(R.id.imageview_item_image);
		new ImageViewAsyncTask(itemImageImageView).execute(items.get(position).getImage());
		
		return view;
	}
	
	private class ImageViewAsyncTask extends AsyncTask<String, Void, ResponseEntity<Resource>> {
		private ImageView imageView;
		
		public ImageViewAsyncTask(ImageView imageView) {
			this.imageView = imageView;
		}
		@Override
		protected ResponseEntity<Resource> doInBackground(String... url) {
			UriComponentsBuilder uri = UriComponentsBuilder
					.fromHttpUrl("http://10.0.2.2:8080/anycart/img")
					.pathSegment(url[0]);
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
			
			return restTemplate.exchange(uri.build().toUri(), HttpMethod.GET, null, Resource.class);
			
		}
		
		@Override
		protected void onPostExecute(ResponseEntity<Resource> result) {
			Bitmap bmp = null;
			try {
				bmp = BitmapFactory.decodeStream(result.getBody().getInputStream());
			} catch (IOException e) { e.printStackTrace(); }
			
			imageView.setImageBitmap(bmp);
		}
	}
}
