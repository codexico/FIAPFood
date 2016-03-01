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

    private static final String TAG = "FIAPFOOOOOOOOOOODLOGIN";

    private final String FF_PREFS = "FF_PREFS";
    private final String KEEPSIGNEDIN = "KEEPSIGNEDIN";

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etPassword)
    EditText etPassword;

    @Bind(R.id.cbStay_signed_in)
    CheckBox cbStay_signed_in;

    @Bind(R.id.tvInfo)
    TextView tvInfo;

    @Bind(R.id.fbLoginButton)
    LoginButton fbLoginButton;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        if (isSignedIn() || isLoggedInFB()) {
            goToMainList();
            return;
        }

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                tvInfo.setText(R.string.signedin);

                Log.d(TAG, "User ID: " + loginResult.getAccessToken().getUserId());
                Log.d(TAG, "Auth Token: " + loginResult.getAccessToken().getToken());

                goToMainList();
            }

            @Override
            public void onCancel() {
                tvInfo.setText(R.string.login_canceled);
            }

            @Override
            public void onError(FacebookException e) {
                tvInfo.setText(R.string.login_failed);
            }
        });
    }
    private boolean isLoggedInFB() {
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
        Intent i = new Intent(LoginActivity.this, MainListActivity.class);
        startActivity(i);
        finish();
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
