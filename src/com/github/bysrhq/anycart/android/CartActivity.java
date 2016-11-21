package com.github.bysrhq.anycart.android;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.bysrhq.anycart.android.entity.Message;
import com.github.bysrhq.anycart.android.entity.Transaction;
import com.github.bysrhq.anycart.android.entity.User;
import com.github.bysrhq.anycart.util.UrlConstant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CartActivity extends Activity {
	private SharedPreferences prefs;
	private ProgressDialog pDialog;
	private Button checkoutButton;
	private Button cancelButton;
	private EditText transactionTotalEditText;
	private EditText transactionSalesEditText;
	private EditText transactionCustomerEditText;
	private CartAdapter adapter;
	private ListView cartListView;
	private Transaction transaction = new Transaction();
	private String transactionId;
	private String sales;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		// Show the Up button in the action bar.
		setupActionBar();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		transactionId = prefs.getString("transaction_id", "");
		sales = prefs.getString("username", "");
		
		Log.d("CEK Transaction ID pas masuk CartActivity", transactionId);

		transactionTotalEditText = (EditText) findViewById(R.id.edittext_transaction_total);
		transactionSalesEditText = (EditText) findViewById(R.id.edittext_transaction_sales);
		transactionCustomerEditText = (EditText) findViewById(R.id.edittext_transaction_customer);
		transactionSalesEditText.setText(sales);
		
		checkoutButton = (Button) findViewById(R.id.button_checkout);
		checkoutButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new CartControlAsyncTask().execute("checkout");
			}
		});
		cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new CartControlAsyncTask().execute("cancel");
				Log.d("TEST", "cancelButton terklik");
			}
		});
		
		adapter = new CartAdapter(this, transaction.getTransactionDetails());
		cartListView = (ListView) findViewById(R.id.listview_cart);
		cartListView.setAdapter(adapter);
		cartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(CartActivity.this, CartDetailActivity.class);
				i.putExtra("idx", position);
				
				CartActivity.this.startActivity(i);	
			}
		});
		
		if (getIntent().getExtras() != null)
			new CartManagerActivityAsyncTask(getIntent().getExtras().getString("itemId")).execute("add");
		else 
			new CartManagerActivityAsyncTask().execute("get");
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cart, menu);
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
	
	private class CartManagerActivityAsyncTask extends AsyncTask<String, Void, ResponseEntity<Transaction>> {
		private String requestParam = "";
		private int idx;
		private String id;
		
		public CartManagerActivityAsyncTask() {}
		
		public CartManagerActivityAsyncTask(int idx) {
			this.idx = idx;
		}
		
		public CartManagerActivityAsyncTask(String id) {
			this.id = id;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pDialog = new ProgressDialog(CartActivity.this);
			pDialog.setMessage("Mohon tunggu...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected ResponseEntity<Transaction> doInBackground(String... request) {
			requestParam = request[0];
			
			URI uri = null;
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
			if(request[0].equals("add"))  {
				uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URLCART)
						.queryParam("itemId", id)
						.queryParam("transactionId", transactionId)
						.build().toUri();
				
				Log.d("CartActivity : ", "passing get url with itemId" + transactionId);
				
				return restTemplate.exchange(uri, HttpMethod.GET, null, Transaction.class);
			} else if (request[0].equals("get")) {
				uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URLCART)
						.queryParam("transactionId", transactionId)
						.build().toUri();
				
				Log.d("CartActivity : ", "passing get url" + transactionId);
				
				return restTemplate.exchange(uri, HttpMethod.GET, null, Transaction.class);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(ResponseEntity<Transaction> result) {
			pDialog.dismiss();
			
			transaction = result.getBody();
			
			prefs = PreferenceManager.getDefaultSharedPreferences(CartActivity.this);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("transaction_id", transaction.getId());
			editor.commit();
				
			transactionTotalEditText.setText(String.valueOf(transaction.getTotal()));
			transactionSalesEditText.setText(prefs.getString("username", ""));
			transactionCustomerEditText.setText(String.valueOf(transaction.getCustomer()));
			
			adapter = new CartAdapter(CartActivity.this, transaction.getTransactionDetails());
			cartListView = (ListView) findViewById(R.id.listview_cart);
			cartListView.setAdapter(adapter);
		}
	}
	
	private class CartControlAsyncTask extends AsyncTask<String, Void, ResponseEntity<Message>> {
		
		@Override
		protected ResponseEntity<Message> doInBackground(String... request) {
			URI uri = null;
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
			if (request[0].equals("checkout")) {
				uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URLCART)
						.queryParam("checkout", "")
						.build().toUri();
				
				User sales = new User();
				sales.setUsername(transactionSalesEditText.getText().toString());
				transaction.setSales(sales);
				transaction.setCustomer(transactionCustomerEditText.getText().toString());
				
				HttpEntity<Transaction> requestEntity = new HttpEntity<Transaction>(transaction);
			
				return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Message.class);
			} else if (request[0].equals("cancel")) {
				uri = UriComponentsBuilder.fromHttpUrl(UrlConstant.URLCART)
						.queryParam("cancel", "")
						.queryParam("transactionId", transaction.getId())
						.build().toUri();
			
				HttpEntity<Transaction> requestEntity = new HttpEntity<Transaction>(transaction);
				
				Log.d("TEST", "Proses cancel");
			
				return restTemplate.exchange(uri, HttpMethod.DELETE, null, Message.class);
			}
				
			return null;
		}
		
		@Override
		protected void onPostExecute(ResponseEntity<Message> result) {
			Toast.makeText(CartActivity.this, result.getBody().getMessage(), Toast.LENGTH_LONG).show();
			
			prefs = PreferenceManager.getDefaultSharedPreferences(CartActivity.this);
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove("transaction_id");
			editor.commit();
			Log.d("TEST", "akhir dari proses CartControl");
			
			CartActivity.this.startActivity(new Intent(CartActivity.this, ItemListActivity.class));
		}
		
	}
}
