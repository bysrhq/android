package com.github.bysrhq.anycart.android;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class ItemListActivity extends Activity {
	private ListView listView;
	private List<Item> items = new ArrayList<Item>();
	private ItemListAdapter adapter;
	private int min;
	private int count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		
		new ItemListAsyncTask().execute();
		
		min =  getIntent().getExtras() != null ? getIntent().getExtras().getInt("min") : 0;
		count =  getIntent().getExtras() != null ? getIntent().getExtras().getInt("count") : 4;
		
		adapter = new ItemListAdapter(this, items);
		listView = (ListView) findViewById(R.id.listview_item_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(ItemListActivity.this, ItemDetailActivity.class);
				i.putExtra("id", items.get(position).getId());
				
				startActivity(i);
			}
		});
		
		Button itemBackButton = (Button) findViewById(R.id.button_item_back);
		itemBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(ItemListActivity.this, ItemListActivity.class);
				i.putExtra("min", min - 4);
				i.putExtra("count", 4);
				startActivity(i);
				
			}
		});
		Button itemNextButton = (Button) findViewById(R.id.button_item_next);
		itemNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(ItemListActivity.this, ItemListActivity.class);
				i.putExtra("min", min + 4);
				i.putExtra("count", 4);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch(menuItem.getItemId()) {
			case R.id.menu_cart : startActivity(new Intent(this, CartActivity.class)); return true;
			case R.id.menu_preference : startActivity(new Intent(this, SettingActivity.class)); return true;
			default: return false;
		}
	}
	
	private class ItemListAsyncTask extends AsyncTask<Void, Void, ResponseEntity<Item[]>>{

		@Override
		protected ResponseEntity<Item[]> doInBackground(Void... arg0) {
			URI uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URL_ITEM)
					.queryParam("min", min).queryParam("count", count)
					.build().toUri();
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
			return restTemplate.exchange(uri, HttpMethod.GET, null, Item[].class);
		}
		
		@Override
		protected void onPostExecute(ResponseEntity<Item[]> result) {
			items = Arrays.asList(result.getBody());
			adapter = new ItemListAdapter(ItemListActivity.this, items);
			listView.setAdapter(adapter);
		}
	}

}
