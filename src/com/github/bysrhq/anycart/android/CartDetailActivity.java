package com.github.bysrhq.anycart.android;

import java.net.URI;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.bysrhq.anycart.android.entity.Item;
import com.github.bysrhq.anycart.android.entity.Transaction;
import com.github.bysrhq.anycart.util.UrlConstant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class CartDetailActivity extends Activity {
	private SharedPreferences prefs;
	private String transactionId;
	private int idx;
	private Transaction transaction;
	private EditText transactionDetailQuantityEditText;
	private TextView transactionDetailSubtotalTextView;
	private Button updateButton;
	private Button deleteButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		idx = getIntent().getExtras().getInt("idx");
		
		Log.d("DI CART DETAIL ACTIVITY", "nilai idx : " + idx);
		
		transactionDetailQuantityEditText = (EditText) findViewById(R.id.edittext_transaction_detail_quantity);
		transactionDetailSubtotalTextView = (TextView) findViewById(R.id.textview_transaction_detail_subtotal);
		updateButton = (Button) findViewById(R.id.button_update);
		updateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new CartDetailAsyncTask().execute("update");
			}
		});
		
		deleteButton = (Button) findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new CartDetailAsyncTask().execute("delete");
			}
		});
		
		new CartDetailAsyncTask().execute("get");
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cart_detail, menu);
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
	
	private class CartDetailAsyncTask extends AsyncTask<String, Void, ResponseEntity<Transaction>>{
		private String requestParam = "";
		
		@Override
		protected ResponseEntity<Transaction> doInBackground(String... request) {
			requestParam = request[0];
			
			prefs = PreferenceManager.getDefaultSharedPreferences(CartDetailActivity.this);
			transactionId = prefs.getString("transaction_id", "");
			URI uri = null;
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
			if (request[0].equals("get")) {
				uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URLCART)
						.queryParam("transactionId", transactionId)
						.build().toUri();
				
				return restTemplate.exchange(uri, HttpMethod.GET, null, Transaction.class);
			} else if (request[0].equals("update")) {
				uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URLCART)
						.queryParam("update", idx)
						.build().toUri();
				
				int qty = Integer.parseInt(transactionDetailQuantityEditText.getText().toString());
				transaction.getTransactionDetails().get(idx).setQuantity(qty);
				
				HttpEntity<Transaction> requestEntity = new HttpEntity<Transaction>(transaction);
				
				return restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Transaction.class);
			} else if (request[0].equals("delete")) {
				uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URLCART)
						.queryParam("delete", idx)
						.build().toUri();
				
				HttpEntity<Transaction> requestEntity = new HttpEntity<Transaction>(transaction);
				
				return restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Transaction.class);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ResponseEntity<Transaction> result) {
			transaction = result.getBody();
			if (requestParam.equals("get")) {
				
//				Log.d("LOG SUBTOTAL BRO", String.valueOf(transaction.getTransactionDetails().get(idx).getSubTotal()));
				
				transactionDetailQuantityEditText.setText(String.valueOf(transaction.getTransactionDetails().get(idx).getQuantity()));
				transactionDetailSubtotalTextView.setText(String.valueOf(transaction.getTransactionDetails().get(idx).getSubTotal()));
			} else if (requestParam.equals("update")) {
				startActivity(new Intent(CartDetailActivity.this, CartActivity.class));
			} else if (requestParam.equals("delete")) {
				startActivity(new Intent(CartDetailActivity.this, CartActivity.class));
			}
		}
		
	}

}
