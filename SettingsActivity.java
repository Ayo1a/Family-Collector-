package com.liyah_barakb.familycollector;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    String userID;
    TextInputEditText editTextNewName;
    CheckBox SharedCheckBox, SecondCheckBox;
    FirebaseFirestore DB;
    ArrayList<String> permissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DB = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        editTextNewName = findViewById(R.id.new_name);

        SharedCheckBox = (CheckBox) findViewById(R.id.checkBoxShared);
        SecondCheckBox = (CheckBox) findViewById(R.id.checkBoxSec);

        SharedCheckBox.setChecked(true);
        SharedCheckBox.setClickable(false);



        setTitle("Settings");
        DocumentReference UserInfo = DB.collection("users").document(userID);
        UserInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("UserName");
                    TextView welcomeMessage = (TextView) findViewById(R.id.editTextTextPersonName);
                    welcomeMessage.setText("Hey " + username + ",");
                    permissions = (ArrayList<String>) documentSnapshot.get("permissions");
                    if(permissions != null && permissions.size() == 2){
                        SecondCheckBox.setChecked(true);
                    }
                }
            }
        });


        Button changeNameBTN = (Button) findViewById(R.id.changeName);
        Button saveSettingsBTN = (Button) findViewById(R.id.saveSettingsBTN);


        changeNameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newName;
                newName = String.valueOf(editTextNewName.getText());
                if (TextUtils.isEmpty(newName)) {
                    Toast.makeText(SettingsActivity.this, "Can't change to empty name", Toast.LENGTH_LONG).show();
                    return;
                }
                DocumentReference UserInfo = DB.collection("users").document(userID);
                UserInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("UserName", newName);
                        UserInfo.update(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                        Toast.makeText(SettingsActivity.this, "Name changed successfully to: " + newName, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });


        saveSettingsBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(SecondCheckBox.isChecked()){
//                    DocumentReference UserInfo = db.collection("users").document(userID);

                    List<String> permissions = new ArrayList<>();
                    permissions.add("FamilyCollectorShared");
                    permissions.add("FamilyIdentityCollectorShared");
                    DB.collection("users")
                            .document(userID)
                            .update("permissions" , permissions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: Permissions UPDATED successfully for userID -> " + userID);
                                    Toast.makeText(SettingsActivity.this, "APP UPDATED PERMISSIONS!", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Permissions FAILED to added for userID -> " + userID);
                                    Toast.makeText(SettingsActivity.this, "APP FAILED TO UPDATE PERMISSIONS!", Toast.LENGTH_LONG).show();
                                }
                            });

                }
                else
                {
                    List<String> permissions = new ArrayList<>();
                    permissions.add("FamilyCollectorShared");
                    DB.collection("users")
                            .document(userID)
                            .update("permissions" , permissions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: Permissions UPDATED successfully for userID -> " + userID);
                                    Toast.makeText(SettingsActivity.this, "APP UPDATED PERMISSIONS!", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Permissions FAILED to added for userID -> " + userID);
                                    Toast.makeText(SettingsActivity.this, "APP FAILED TO UPDATE PERMISSIONS!", Toast.LENGTH_LONG).show();
                                }
                            });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        menu.findItem(R.id.settings_mm).setVisible(false);
        menu.findItem(R.id.login_mm).setVisible(false);
        menu.findItem(R.id.register_mm).setVisible(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        if (item.getItemId() == R.id.main_mm) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()== R.id.gallery_mm){
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()== R.id.about_mm){
            aboutAlertDialog();
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