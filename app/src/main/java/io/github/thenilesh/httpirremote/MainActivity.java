package io.github.thenilesh.httpirremote;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import io.github.thenilesh.httpirremote.dao.AppDatabase;
import io.github.thenilesh.httpirremote.dto.AppConfig;
import io.github.thenilesh.httpirremote.dto.IRCode;
import io.github.thenilesh.httpirremote.dto.RButton;
import io.github.thenilesh.httpirremote.fragment.CreateRButtonFragment;
import io.github.thenilesh.httpirremote.fragment.HomeFragment;
import io.github.thenilesh.httpirremote.utils.IRRemoteService;
import io.github.thenilesh.httpirremote.utils.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Main";
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Open home fragment at the start
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.screen_area, homeFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_home) {
            if (homeFragment == null) {
                fragment = new HomeFragment();
            } else {
                fragment = homeFragment;
            }
        } else if (id == R.id.nav_createbutton) {
            fragment = new CreateRButtonFragment();
        } else if (id == R.id.nav_manage) {
            //TODO:Fragment to configure Remote URL
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addButton(View view) {
        //TODO:Ask IR remote module for ir codes of last pressed button
        EditText etButtonName = (EditText) findViewById(R.id.et_rbutton_name);
        final String buttonName = etButtonName.getText().toString();
        Log.d(TAG, "Creating button: " + buttonName);
        (new AsyncTask<Void, Void, RButton>() {
            @Override
            protected RButton doInBackground(Void... voids) {
                Log.d(TAG, "Receiving IR Code");
                IRRemoteService irRemoteService = ServiceGenerator.create(IRRemoteService.class);
                AppConfig appConfig = AppDatabase.getInstance(getApplicationContext()).configDao().load();
                Call<IRCode> irCodeCall = irRemoteService.receiveIrCode(appConfig.getBaseUrl());
                IRCode irCode = null;
                try {
                    Log.d(TAG, "Requesting ir code");
                    Response<IRCode> irCodeResponse = irCodeCall.execute();
                    Log.d(TAG, "Recvd ir code");
                    if (irCodeResponse.isSuccessful()) {
                        irCode = irCodeResponse.body();
                        Log.d(TAG, "Valid ir code: " + irCode);
                    } else {
                        Log.e(TAG, "Invalid response: " + irCodeResponse.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (irCode == null) {
                    throw new RuntimeException("Null ir code recvd");
                }
                RButton rButton = new RButton();
                rButton.setName(buttonName);
                rButton.setIrCode(irCode);
                final ImageView imageView = (ImageView) findViewById(R.id.iv_thumbnail);
                String randomName = "" + new Random().nextInt(999);
                final File thumbnailFile = new File(getFilesDir(), randomName);
                try (OutputStream outputStream = new FileOutputStream(thumbnailFile)) {
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (IOException e) {
                    Log.e(TAG, "Could not save thumbnail", e);
                }
                Uri uri = Uri.fromFile(thumbnailFile);
                rButton.setThumbnail(uri);
                Log.d(TAG, "Button created");
                return rButton;
            }

            @Override
            protected void onPostExecute(RButton rButton) {
                super.onPostExecute(rButton);
                homeFragment.addNewRButton(rButton);
            }
        }).execute();
       /* EditText etIrCode = (EditText) findViewById(R.id.et_ircode);
        String irCode = etIrCode.getText().toString();
        RButton rButton = new RButton();
        rButton.setName(buttonName);
        rButton.setIrCode(StringToIrCodeConverter.convert(irCode));
        final ImageView imageView = (ImageView) findViewById(R.id.iv_thumbnail);
        String randomName = "" + new Random().nextInt(999);
        final File thumbnailFile = new File(getFilesDir(), randomName);

        (new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try (OutputStream outputStream = new FileOutputStream(thumbnailFile)) {
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (IOException e) {
                    Log.e(TAG, "Could not save thumbnail", e);
                }
                return null;
            }
        }).execute();

        Uri uri = Uri.fromFile(thumbnailFile);
        rButton.setThumbnail(uri);
        homeFragment.addNewRButton(rButton);
        */
    }

    public void browseIcon(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.iv_thumbnail);
            imageView.setTag(picturePath);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
}
