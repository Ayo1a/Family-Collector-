package com.liyah_barakb.familycollector;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liyah_barakb.familycollector.ImageAdapter;
import com.liyah_barakb.familycollector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    ArrayList<String> imagelist, permissionsCounter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ImageAdapter adapter;
    FirebaseAuth mAuth;
    String userID, folderName;
    StorageReference listRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        imagelist=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerview);
        adapter=new ImageAdapter(imagelist,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(null));
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        // Getting the folder to look at - Shared/Private
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference UserInfo = db.collection("users").document(userID);
        UserInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //if we found the user
                    permissionsCounter = (ArrayList<String>) documentSnapshot.get("permissions");

                    Spinner spinner = findViewById(R.id.spinner);
                    ArrayList<String> arrayList = new ArrayList<>();


                    // getting user's choice folder images
                    if(permissionsCounter != null && permissionsCounter.size() == 1){
                        // ENTER HERE THE SPINNER WITH 1 CHOICE
                        arrayList.add("FamilyCollectorShared");

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GalleryActivity.this,android.R.layout.simple_spinner_item, arrayList);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(arrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String choice = parent.getItemAtPosition(position).toString();
                                Toast.makeText(parent.getContext(), "Selected: " + choice,Toast.LENGTH_LONG).show();
                                if(choice.equals("FamilyCollectorShared")){
                                    imagelist.clear();

                                    folderName = "FamilyCollectorShared";
                                    listRef = FirebaseStorage.getInstance().getReference().child((String) folderName);
                                    listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            for(StorageReference file:listResult.getItems()){
                                                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imagelist.add(uri.toString());
                                                        Log.e("Itemvalue",uri.toString());
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        recyclerView.setAdapter(adapter);
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView <?> parent) {
                            }
                        });

                    } else if(permissionsCounter != null && permissionsCounter.size() == 2){
                        // ENTER HERE THE SPINNER WITH 2 CHOICES
                        arrayList.add("FamilyCollectorShared");
                        arrayList.add("FamilyIdentityCollectorShared");

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GalleryActivity.this,android.R.layout.simple_spinner_item, arrayList);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(arrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String choice = parent.getItemAtPosition(position).toString();
                                Toast.makeText(parent.getContext(), "Selected: " + choice,Toast.LENGTH_LONG).show();
                                if(choice.equals("FamilyCollectorShared")){
                                    imagelist.clear();

                                    folderName = "FamilyCollectorShared";
                                    listRef = FirebaseStorage.getInstance().getReference().child((String) folderName);
                                    listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            for(StorageReference file:listResult.getItems()){
                                                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imagelist.add(uri.toString());
                                                        Log.e("Itemvalue",uri.toString());
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        recyclerView.setAdapter(adapter);
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    imagelist.clear();

                                    folderName = "FamilyIdentityCollectorShared";
                                    listRef = FirebaseStorage.getInstance().getReference().child((String) folderName);
                                    listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            for(StorageReference file:listResult.getItems()){
                                                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imagelist.add(uri.toString());
                                                        Log.e("Itemvalue",uri.toString());
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        recyclerView.setAdapter(adapter);
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }

                            }
                            @Override
                            public void onNothingSelected(AdapterView <?> parent) {
                            }
                        });

                    }else{
                        Toast.makeText(GalleryActivity.this, "SOMETHING WENT WRONG!! = ", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

    }

    private void aboutAlertDialog()
    {
        String strDeviceOS = "Android OS " + Build.VERSION.RELEASE + " API " + Build.VERSION.SDK_INT;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("About App");
        dialog.setMessage("\nOur Family Collector mobile App!" + "\n\n" + strDeviceOS + "\n\n" + "By Liya Hanny Avitan and Barak Ben Hamo 2023.");
        //dialog.setMessage("\nOur Family Collector mobile App!" + "\n\n" + strDeviceOS + "\n\n" + "By Liya Hanny Avitan and Barak Ben Hamo 2023.");
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
    // Setting all the app bar we don't need invisible
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        menu.findItem(R.id.gallery_mm).setVisible(false);
        menu.findItem(R.id.login_mm).setVisible(false);
        menu.findItem(R.id.register_mm).setVisible(false);
        return true;
    }


    // app bar
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        if (item.getItemId() == R.id.main_mm) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()== R.id.about_mm){
            aboutAlertDialog();
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