package com.vuga.paybus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    static TextView errors, contactText, passwordText = null;
    private static final String TAG = "LoginActivity";
    private ConnectionDetector cd;
    boolean canEnter = false;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    String imageName,userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        contactText = (TextView) findViewById(R.id.contact);
        passwordText = (TextView) findViewById(R.id.password);
        PaymentHandler databaseHelper = new PaymentHandler (LoginActivity.this);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        cd = new ConnectionDetector(getApplicationContext());
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.AppTheme);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                if (cd.isConnectingToInternet()) {
                    LoginProcess();
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection !", Toast.LENGTH_LONG).show();
                }
            }
        });



        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        try {

            SharedPreferences myPrefs = getSharedPreferences(util.PREFS_NAME, 0);
            String name = myPrefs.getString("name", "").toString();
            String contact = myPrefs.getString("contact", "").toString();
            String image = myPrefs.getString("image", "").toString();
            String email = myPrefs.getString("email", "").toString();
            String id = myPrefs.getString("id", "").toString();
            util.COMPANY_LOGO = myPrefs.getString("logo", "").toString();
            util.USER_NAME = myPrefs.getString("name", "").toString();
            util.USER_IMAGE = myPrefs.getString("image", "").toString();
            util.COMPANY_ID = myPrefs.getString("companyID", "").toString();
            util.COMPANY = myPrefs.getString("company", "").toString();
            util.USER_ID = myPrefs.getString("id", "").toString();


            if (!contact.toString().equals("") && !name.toString().equals("")) {
                Intent startLocation = new Intent(LoginActivity.this,StartActivity.class);
                startActivity(startLocation);
                finish();
            }

        } catch (Exception e) {
            //  Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
            //Start.setVisibility(View.VISIBLE);
        }
    }

    private boolean LoginProcess() {

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        String password = passwordText.getText().toString();
        String contact = contactText.getText().toString();
        SharedPreferences settings = getSharedPreferences(util.PREFS_NAME, 0);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("contact", contact);
        params.put("password", password);
        String root = getApplicationContext().getFilesDir().toString();


        client.post(util.Url + "mobile/login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String ret = new String(responseBody);
                // Toast.makeText(getApplicationContext(), " "+ret, Toast.LENGTH_LONG).show();
                try {
                    JSONObject j = new JSONObject(ret);
                    // Toast.makeText(getApplicationContext(), j.get("status").toString(), Toast.LENGTH_LONG).show();
                     Toast.makeText(getApplicationContext(), j.get("info").toString(), Toast.LENGTH_LONG).show();
                    progressDialog.cancel();
                    if (j.get("status").toString().equals("true")) {
                        Toast.makeText(getApplicationContext(), "Welcome committing your user profile", Toast.LENGTH_LONG).show();
                        SharedPreferences myPrefs = getApplicationContext().getSharedPreferences(util.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = myPrefs.edit();

                        editor.putString("email", j.get("email").toString());
                        editor.putString("name", j.get("name").toString());
                        editor.putString("contact", j.get("contact").toString());
                        editor.putString("id", j.get("userID").toString());
                        editor.putString("image", j.get("image").toString());
                        editor.putString("companyID", j.get("companyID").toString());
                        editor.putString("company", j.get("company").toString());
                        editor.putString("logo", j.get("logo").toString());
                        editor.putString("bus", j.get("bus").toString());
                        editor.putString("route", j.get("route").toString());
                        editor.putString("MAX_SEATS", j.get("seats").toString());
                        //
                        editor.apply();
                        editor.commit();
                        util.USER_NAME = myPrefs.getString("name", "").toString();
                        util.COMPANY_ID = myPrefs.getString("companyID", "").toString();
                        util.BUS = myPrefs.getString("bus", "").toString();
                        util.ROUTE = myPrefs.getString("route", "").toString();
                        util.MAX_SEATS = myPrefs.getString("seats", "").toString();
                        try {

                            Toast.makeText(getApplicationContext(), j.get("info").toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "Your name is :"+j.get("name").toString(), Toast.LENGTH_LONG).show();
                            Intent startLocation = new Intent(LoginActivity.this, StartActivity.class);
                            startActivity(startLocation);
                            finish();
                            progressDialog.cancel();

                        } catch (Exception e) {
                            // Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            progressDialog.cancel();
                        }
                        try{
                            String root = getApplicationContext().getFilesDir().toString();
                            imageName =   j.get("logo").toString();
                            String ul = util.FileUrl+"uploads/"+ j.get("logo").toString();
                            Log.i(TAG, ul);
                            File imgFile2 = new File(root + "/"+ j.get("logo").toString());
                                new DownloadImage().execute((util.FileUrl + "uploads/" + j.get("logo").toString()).trim());
                        }catch(Exception e) {
                            //  Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(getApplicationContext(), "INFO: " + j.get("info").toString() + "", Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                    }
                    //  Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block

                    System.out.print("data sync Error" + e);
                    System.out.print(ret);
                    e.printStackTrace();
                    progressDialog.cancel();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
        return canEnter;
    }
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result, imageName);
        }
        public void saveImage(Context context, Bitmap b, String imageName) {
            FileOutputStream foStream;
            try {
                foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
                b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
                foStream.close();
            } catch (Exception e) {
                Log.d("saveImage", "Exception 2, Something went wrong!");
                e.printStackTrace();
            }
        }
    }




    private boolean isEmailValid(String email) {
                //TODO: Replace this with your own logic
                return email.contains("@");
            }

            private boolean isPasswordValid(String password) {
                //TODO: Replace this with your own logic
                return password.length() > 4;
            }



        }


