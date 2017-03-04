package com.vuga.paybus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button btnPay,btnScan;
    static TextView logoutLink = null;
    File file;
    private ImageView ivImage,UsrImage;
    Context context = this;
    String imageName,userImage;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPay = (Button) findViewById(R.id.buttonPay);
        btnScan = (Button) findViewById(R.id.buttonScan);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        UsrImage = (ImageView) findViewById(R.id.UsrImage);
        String root = getApplicationContext().getFilesDir().toString();
        Log.i(TAG,  util.COMPANY_LOGO);
        try{
            imageName =   util.COMPANY_LOGO;
            String ul = util.FileUrl+"uploads/"+ util.COMPANY_LOGO;
            Log.i(TAG, ul);
            File imgFile = new File(root + "/"+ util.COMPANY_LOGO);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivImage.setImageBitmap(myBitmap);
            }
            else{
                Picasso.with(context).load(util.FileUrl+"uploads/"+util.COMPANY_LOGO).resize(50, 50)
                        .centerCrop().into( ivImage);
                new DownloadLogo().execute((util.FileUrl + "uploads/" + util.COMPANY_LOGO).trim());
            }
        }catch(Exception e) {
            //  Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();

        }
        try{
            userImage =   util.USER_IMAGE;
            String ul = util.FileUrl+"uploads/"+ util.USER_IMAGE;
            Log.i(TAG, ul);
            File imgFile2 = new File(root + "/"+ util.USER_IMAGE);
            if(imgFile2.exists()){
                Bitmap myBitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                UsrImage.setImageBitmap(myBitmap2);
            }
            else{
                Picasso.with(context).load(util.FileUrl+"uploads/"+util.USER_IMAGE).resize(50, 50)
                        .centerCrop().into(UsrImage);
                new DownloadImage().execute((util.FileUrl + "uploads/" + util.USER_IMAGE).trim());
            }
        }catch(Exception e) {
            //  Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();

        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(MainActivity.this, PayActivity.class);
                startActivity(startLocation);
               // finish();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(startLocation);
                //finish();
            }
        });
        logoutLink = (TextView) findViewById(R.id.link_logout);
        logoutLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_LONG).show();
                SharedPreferences myPrefs = getSharedPreferences(util.PREFS_NAME, 0);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.clear();
                editor.commit();

                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(myIntent);// Commit the edits!
                finish();
            }
        });


    }

    private class DownloadLogo extends AsyncTask<String, Void, Bitmap> {
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
            saveImage(getApplicationContext(), result, userImage);
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



}
