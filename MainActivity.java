package com.liyah_barakb.familycollector;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int notificationID;

    private NotificationManager notificationManager;
    private static final int CAMERA_REQUEST = 111;
    private static final int GALLERY_REQUEST = 222;

    private static String CHOISE="Save to family pictures";


    private WifiManager wifiManager;

    private TextView txvTitle;
    private ImageView img;
    private EditText edtInput;
    private Button btnCamera;
    //private Button btnGallery;
    private Button btnSaveImg;
    private Bitmap picBitmap;
    FirebaseAuth mAuth;
    String userID, folderName;
    ArrayList<String> imagelist, permissionsCounter;


    private void setupNotification(){
        //notification init
        notificationID = 1;
        notificationManager =  getSystemService(NotificationManager.class);

        //creat channel
        NotificationChannel notificationChannel = new NotificationChannel(
                "HIGH_CHANNEL_ID",      // Constant for Channel ID
                "HIGH_CHANNEL_NAME",    // Constant for Channel NAME
                NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(notificationChannel);

    }


    public void doNotify()
    {
        String title = "FamilyCollector";
        String text = "New item successfully uploaded";


        Notification notification = new NotificationCompat.Builder(MainActivity.this, "HIGH_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_notofication)
                .setContentTitle(title + "(" + notificationID + ")")
                .setContentText(text)
                .build();

        notificationManager.notify(notificationID, notification);
        notificationID++;
    }



    private void aboutAlertDialog()
    {
        String strDeviceOS = "Android OS " + Build.VERSION.RELEASE + " API " + Build.VERSION.SDK_INT;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("About App");
        dialog.setMessage("\nOur Family Collector mobile App!" + "\n\n" + strDeviceOS + "\n\n" + "By Liya Hanny Avitan and Barak Ben Hamo 2023.");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();   // close this dialog
            }
        });
        dialog.show();
    }


    private void exitAlertDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //dialog.setIcon(R.drawable.icon_exit);
        dialog.setTitle("Exit App");
        dialog.setMessage("Are you sure ?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();   // destroy this activity
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();   // close this dialog
            }
        });
        dialog.show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNotification();

        btnCamera= findViewById(R.id.btnCameraID);
        //btnGallery=findViewById(R.id.btnGalleryID);
        btnSaveImg=findViewById(R.id.btnSaveImgID);

        // Getting the folder to look at - Shared/Private
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        //spinner

       DocumentReference UserInfo = db.collection("users").document(userID);
        UserInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists())
                {
                    ArrayList<String> arrayList = (ArrayList<String>) documentSnapshot.get("permissions");
                    //if we found the user

                    Log.d("mylog", ">>> arrayList "+arrayList);

                   Spinner spinner = findViewById(R.id.spinner);
                    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item, arrayList);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                        String tutorialsName = parent.getItemAtPosition(position).toString();
                        String choice = parent.getItemAtPosition(position).toString();
//FamilyCollectorShared, FamilyIdentityCollectorShared
                if(choice.equals("FamilyCollectorShared")){
                    CHOISE="FamilyCollectorShared";
                    Log.d("mylog", ">>> CHOISE=Save to family pictures");
                }
                else{
                    CHOISE="FamilyIdentityCollectorShared";
                    Log.d("mylog", ">>> CHOISE=Save to identity files");
                }
                Toast.makeText(parent.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_LONG).show();
                Log.d("mylog", ">>> Spinner value was selected");

            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });



                }
            }
        });


        img = findViewById(R.id.imgID);
        //img.setImageResource(R.drawable.noimagefound);
        //buttonClicked(btnCamera);
        btnCamera.setOnClickListener(this);
        //btnGallery.setOnClickListener(this);
        btnSaveImg.setOnClickListener(this);

        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


    }

    @Override
    public void onClick(View btn)
    {

        switch (btn.getId())
        {
            case R.id.btnCameraID:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //ActivityResultLauncher()
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;



            case R.id.btnSaveImgID:
                //writing upload function
                //Intent cameraIntent = new Intent
                try {
                    addImgToFirebase();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    private void addImgToFirebase() throws FileNotFoundException {
        Log.d("mylog", ">>> addImgToFirebase");
        //FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new bitMap with a generated ID
        //db.collection("Family");
        checkConnection();

        //check that biMap not null
        //toast- if null
        if (picBitmap==null){
            bitMapAlertDialog();
            //break?
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // add the Img to fireBase with description
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app
                    String pic_name = System.currentTimeMillis()+".jpg"; //Integer.toString(min);

                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    //default name for directory
                    String folderName = CHOISE;
                    //String folderName = "FamilyCollectorShared";

                    storage.getReference(folderName + "/" +pic_name).putBytes(data)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception exception)
                                {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(MainActivity.this,"upload failed", Toast.LENGTH_LONG).show();
                                    Log.d("mylog", ">>> onFailure, thread, addImgToFirebase");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override

                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    //taskSnapshot.getMetadata(); //contains file metadata such as size, content-type, etc.
                                    Log.d("mylog", ">>> onSuccess, thread, addImgToFirebase");
                                    //Toast.makeText(MainActivity.this,"image was uploaded", Toast.LENGTH_LONG).show();
                                    doNotify();
                                }
                            });



                }
            }).start();



        }



    }
    private void checkConnection(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                ConnectionAlert(1);
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to mobile data
                ConnectionAlert(2);
            }
        } else {
            ConnectionAlert(0);
            // not connected to the internet
        }
    }
    private void ConnectionAlert(int type)
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (type==0){
            dialog.setTitle("There is no connection, click to continue");
            dialog.setMessage("Currently you cannot perform uploading, please connect to wifi or mobile connection");
        }
        else if(type==1){
            //dialog.setTitle("There is WIFI connection, click to continue");
        }
        else if (type==2){
            //dialog.setTitle("There is Mobile Data connection, click to close this window");

        }
        dialog.show();
    }

    private void bitMapAlertDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //dialog.setIcon(R.drawable.icon_exit);
        dialog.setTitle("Cannot save picture");
        dialog.setMessage("Do you want to take a new picture?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //take a new picture. taking automatically
                //finish();   // destroy this activity
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //do nothing stay on the same activity.
                //dialog.dismiss();   // close this dialog
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CAMERA_REQUEST:
                    picBitmap = (Bitmap)data.getExtras().get("data");
                    img.setImageBitmap(picBitmap);
                    break;

                case GALLERY_REQUEST:
                    // Get the url of the image from data
                    Uri selectedPicUri = data.getData();

                    if (null != selectedPicUri) {
                        // update the preview image in the layout
                        img.setImageURI(selectedPicUri);

                        // make a bitmap from pic

                        //saveBitmapToFile(bitmap);
                    }
                    break;
                //case CAMERA_REQUEST:


                //break;
            }
        }
    }
    // Setting all the app bar we don't need invisible
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        menu.findItem(R.id.main_mm).setVisible(false);
        menu.findItem(R.id.login_mm).setVisible(false);
        menu.findItem(R.id.register_mm).setVisible(false);
        return true;
    }

    // app bar
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        item.setVisible(false);

        if (item.getItemId()== R.id.gallery_mm){
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()== R.id.about_mm){
            aboutAlertDialog();
           // Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
            //startActivity(intent);
          //finish();

        }
        if(item.getItemId()==R.id.settings_mm){
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId()==R.id.signout_mm) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId()==R.id.exit_mm) {
            exitAlertDialog();
        }
        return true;
    }
}