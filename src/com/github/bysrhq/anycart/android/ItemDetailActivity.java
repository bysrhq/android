package com.github.bysrhq.anycart.android;

import java.io.IOException;
import java.net.URI;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.bysrhq.anycart.android.entity.Item;
import com.github.bysrhq.anycart.util.UrlConstant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v4.app.NavUtils;

public class ItemDetailActivity extends Activity {
	private EditText itemNameEditText;
	private EditText itemPriceEditText;
	private EditText itemQuantityEditText;
	private ImageView itemImageImageView;
	private EditText itemSizeEditText;
	private EditText itemColorEditText;
	private EditText itemBrandEditText;
	private EditText itemCategoryEditText;
	private Item item;
	private String id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);
//		Show the Up button in the action bar.
		setupActionBar();
		
		id = getIntent().getExtras().getString("id");
		
		itemNameEditText = (EditText) findViewById(R.id.edittext_item_name);
		itemPriceEditText = (EditText) findViewById(R.id.edittext_item_price);
		itemQuantityEditText = (EditText) findViewById(R.id.edittext_item_quantity);
		itemImageImageView = (ImageView) findViewById(R.id.imageview_item_image);
		itemSizeEditText = (EditText) findViewById(R.id.edittext_item_size);
		itemColorEditText = (EditText) findViewById(R.id.edittext_item_color);
		itemBrandEditText = (EditText) findViewById(R.id.edittext_item_brand);
		itemCategoryEditText = (EditText) findViewById(R.id.edittext_item_category);
		itemImageImageView = (ImageView) findViewById(R.id.imageview_item_image);
		Button addToCartButton = (Button) findViewById(R.id.button_add_to_cart);
		addToCartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(ItemDetailActivity.this, CartActivity.class);
				i.putExtra("itemId", item.getId());
				Log.d("ITEM IDNYA APA :", item.getId());
				
				startActivity(i);
			}
		});
		
		new ItemDetailAsyncTask().execute();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class ItemDetailAsyncTask extends AsyncTask<Void, Void, ResponseEntity<Item>> {

		@Override
		protected ResponseEntity<Item> doInBackground(Void... arg0) {
			URI uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URL_ITEM)
					.pathSegment(id)
					.build().toUri();
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
			return restTemplate.exchange(uri, HttpMethod.GET, null, Item.class);
		}
		
		@Override
		protected void onPostExecute(ResponseEntity<Item> result) {
			item = result.getBody();
			
			itemNameEditText.setText(item.getName());
			itemPriceEditText.setText(String.valueOf(item.getPrice()));
			itemQuantityEditText.setText(String.valueOf(item.getQuantity()));
			itemSizeEditText.setText(item.getSize().getSize());
			itemColorEditText.setText(item.getColor().getColor());
			itemBrandEditText.setText(item.getBrand().getBrand());
			itemCategoryEditText.setText(item.getCategory().getCategory());
			new ImageViewAsyncTask(itemImageImageView).execute(item.getImage());
		}
		
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
