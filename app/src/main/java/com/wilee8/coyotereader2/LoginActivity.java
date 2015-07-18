package com.wilee8.coyotereader2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

	private static final String AUTH_PREFS = "AuthPrefsFile";

	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText             mPasswordView;
	private View                 mProgressView;
	private View                 mLoginFormView;
	private Context              mContext;

	private InoreaderService  mService;
	private SharedPreferences mPreferences;
	private String            mUsername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		mContext = this;

		mPreferences = getSharedPreferences(AUTH_PREFS, MODE_PRIVATE);
		String savedUsername = mPreferences.getString("username", "");

		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		mEmailView.setText(savedUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);

		RequestInterceptor requestInterceptor = new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade request) {
				request.addHeader("AppId", getString(R.string.app_id));
				request.addHeader("AppKey", getString(R.string.app_key));
			}
		};

		RestAdapter restAdapter = new RestAdapter.Builder()
			.setEndpoint("https://www.inoreader.com")
			.setRequestInterceptor(requestInterceptor)
			.build();

		mService = restAdapter.create(InoreaderService.class);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	@SuppressWarnings("unchecked")
	public void attemptLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;


		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);

			mUsername = email;

			Map queryMap = new HashMap<>();
			queryMap.put("Email", email);
			queryMap.put("Passwd", password);

			AuthReplyHandler authReplyHandler = new AuthReplyHandler();
			AppObservable.bindActivity(
				this,
				mService.clientLogin(queryMap)
				.subscribeOn(Schedulers.io()))
			.subscribe(authReplyHandler);
//			Observable.create(new DoAuthCallObserver(url)).subscribe(authReplyHandler);
		}
	}

	private class AuthReplyHandler extends Subscriber<Response> {

		@Override
		public void onCompleted() {

		}

		@Override
		public void onError(Throwable throwable) {
			RetrofitError error = (RetrofitError) throwable;
			String message = error.getMessage();

			showProgress(false);

			if (message.equalsIgnoreCase("401 Authorization Required")) {
				// Failed login, post an error
				mPasswordView.setError(getString(R.string.error_incorrect_password));
			} else {
			mPasswordView.setError(getString(R.string.error_network));
			}

			mPasswordView.requestFocus();
		}

		@Override
		public void onNext(Response response) {
			String responseBody = new String(((TypedByteArray) response.getBody()).getBytes());

			// Get the authentication token
			String holder[] = responseBody.split("Auth=");
			String token = holder[1].replaceAll("\n", "");
			SharedPreferences.Editor editor = mPreferences.edit();

			editor.putString("username", mUsername);
			editor.putString("authToken", token);
			editor.apply();

			// Launch main activity
			Intent mainIntent = new Intent(mContext, MainActivity.class);
			startActivity(mainIntent);
			finish();
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
				show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
				show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}



