package si.uni_lj.fri.pbd.miniapp1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

import si.uni_lj.fri.pbd.miniapp1.contacts.Contact;
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.ContactsViewModel;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private NavigationView navigationView;
    private SharedPreferences.Editor editor;
    private String savedPhotoName = "savedPhoto";
    private ImageView profileImage;
    private Uri file;
    ContactsViewModel contactsViewModel;

    static final int REQUEST_IMAGE_CAPTURE = 33;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_contacts, R.id.nav_message)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        //We check if the list of contacts already exists, otherwise we check permissions, create it and fill with contacts.
        if (contactsViewModel.getContactHolder() == null)
            loadContacts();

        //We call method, that handles profile image of a user.
        processImage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void processImage() {

        profileImage = navigationView.getHeaderView(0).findViewById(R.id.profilePhoto);
        SharedPreferences sharedPreferences = getSharedPreferences(savedPhotoName, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String committedImage = sharedPreferences.getString(savedPhotoName, null);

        //If user previous time already saved an image, we set it.
        if(committedImage != null) {
            profileImage.setBackground(null);
            byte[] imageAsBytes = Base64.decode(committedImage.getBytes(), Base64.DEFAULT);
            profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }

        //We add click listener to image, so if we press it, we open camera.
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    //Here we get result of activity and check if it is for camera (Image capture) and we set the image and save it to shared preferences.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileImage.setBackground(null);
            profileImage.setImageBitmap(imageBitmap);

            ByteArrayOutputStream bImage = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bImage);
            byte[] bArray = bImage.toByteArray();
            String encodedImage = Base64.encodeToString(bArray, Base64.DEFAULT);

            editor.putString(savedPhotoName, encodedImage);
            editor.commit();

        }
    }

    //In case we don't already have permission, we request it.
    public void loadContacts (){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            contactsViewModel.setContactHolder(findContacts());
        }
    }

    //Here we catch result of permission request.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactsViewModel.setContactHolder(findContacts());
                } else {
                    Toast.makeText(this, "Permission to access the contacts list not granted.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //In this method we fetch contacts. First we check if the contact has number, then we iterate through that contact for all numbers and emails.
    //Next to contact name, there will also be displayed type of contact (home, work, ...) -> see toString method in Contact class,
    //because one person can have more than one phone number or email address.
    private LinkedList<Contact> findContacts() {

        LinkedList<Contact> contactHolder = new LinkedList<>();

        ContentResolver contentResolver = this.getContentResolver();

        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );
        if(cursor!=null && cursor.getCount()>0){
            while(cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            new String[]{id},
                            null
                    );
                    Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            new String[]{id},
                            null
                    );
                    if (phoneCursor != null && phoneCursor.getCount() > 0 && emailCursor != null && emailCursor.getCount() > 0) {
                        while (phoneCursor.moveToNext() && emailCursor.moveToNext()) {
                            String phId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                            String customLabel = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                            String label = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.getResources(),
                                    phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)),
                                    customLabel
                            );
                            String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String email = emailCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                            contactHolder.add(new Contact(phId, name, number, label, email));
                        }
                        if (phoneCursor != null)
                            phoneCursor.close();
                        if (emailCursor != null)
                            emailCursor.close();
                    }
                }
            }
            if (cursor != null)
                cursor.close();
        }

        return contactHolder;
    }

}
