package com.github.bysrhq.anycart.android;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private SharedPreferences sharedPreferences;
	private EditText userUsernameEditText;
	private EditText userPasscodeEditText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        userUsernameEditText  = (EditText) findViewById(R.id.edittext_user_username);
        userPasscodeEditText = (EditText) findViewById(R.id.edittext_user_passcode);
        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("username", userUsernameEditText.getText().toString());
				editor.putString("passcode", userPasscodeEditText.getText().toString());
				editor.commit();

				startActivity(new Intent(LoginActivity.this, ItemListActivity.class));
			}
		});
    }
}
