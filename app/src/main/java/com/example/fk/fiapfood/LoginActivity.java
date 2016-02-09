package com.example.fk.fiapfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private final String FF_PREFS = "GTD_PREFS";
    private final String KEEPSIGNEDIN = "KEEPSIGNEDIN";

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etPassword)
    EditText etPassword;

    @Bind(R.id.cbStay_signed_in)
    CheckBox cbStay_signed_in;

    private TextView info;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        info = (TextView)findViewById(R.id.info);
        fbLoginButton = (LoginButton)findViewById(R.id.fbLoginButton);

        callbackManager = CallbackManager.Factory.create();

        if (isSignedIn() || isLoggedInFB()) {
            goToMainList();
        }

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                goToMainList();
            }

            @Override
            public void onCancel() {
                info.setText(R.string.login_canceled);
            }

            @Override
            public void onError(FacebookException e) {
                info.setText(R.string.login_failed);
            }
        });
    }
    public boolean isLoggedInFB() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btSignIn)
    public void login() {
        if (isValidUser()) {
            staySignedIn();
            goToMainList();
        }
    }

    private void goToMainList() {
//        Intent i = new Intent(LoginActivity.this, MainListActivity.class);
//        startActivity(i);
        Log.d("logged", "goToMainList xxXXXxxxxX");
//        finish();
    }

    private boolean isValidUser() {
        boolean valid = false;

        String name = etName.getText().toString();
        String password = etPassword.getText().toString();

        if (name.equals("admin") && password.equals("admin")) {
            valid = true;
        } else {
            Toast.makeText(LoginActivity.this, R.string.login_invalid, Toast.LENGTH_LONG).show();
        }

        return valid;
    }

    private void staySignedIn() {
        SharedPreferences settings = getSharedPreferences(FF_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(KEEPSIGNEDIN, cbStay_signed_in.isChecked());
        editor.apply();
    }

    private boolean isSignedIn() {
        SharedPreferences settings = getSharedPreferences(FF_PREFS, MODE_PRIVATE);

        return settings.getBoolean(KEEPSIGNEDIN, false);
    }
}
