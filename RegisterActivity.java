package com.liyah_barakb.familycollector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword,editTextUsername;
    Button btnRegister, btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;


    @Override
    public void onStart() {
        // If user is already logged in to application go to main activity
        super.onStart();
        setTitle("Register");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, username;
                //giving accounts permissions to folders
                // *** PERMISSION 1 IS AUTO GENERATED FOR ALL USERS - SHARED FOLDER **
                // *** NEED TO ADD TO SETTINGS THE OPTION TO GIVE ANOTHER PERMISSION **
                List<String> permissions = new ArrayList<>();
                permissions.add("FamilyCollectorShared");
//                permissions.add("FamilyIdentityCollectorShared");

                // Create a new folder in Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                username = String.valueOf(editTextUsername.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "Enter user name", Toast.LENGTH_LONG).show();
                    return;
                }
                // Get the email address and create the folder name
//                String folderName = "private_" + email;
//                StorageReference privateFolderRef = storageRef.child(folderName);
//                // Create the folder
//                permissions.add("gs://familycollectorv2.appspot.com/" + folderName);
//                privateFolderRef.putBytes(new byte[0]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Private folder created successfully!");
//                        } else {
//                            Log.d(TAG, "Failed to create private folder: " + task.getException().getMessage());
//                        }
//                    }
//                });

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful())
                                {

                                    Toast.makeText(RegisterActivity.this, "Account Created Successfully!", Toast.LENGTH_LONG).show();
                                    userID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(userID);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("email",email);
                                    user.put("UserName",username);
                                    user.put("permissions", permissions);
//                                    user.put("LookAt", "0");//looking at shared folder

                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: User Profile is created for " + userID);
                                        }
                                    });
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                                    Toast.makeText(RegisterActivity.this, "Password Must Contain At-least 6 Chars.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
    // Setting all the app bar we don't need invisible
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        menu.findItem(R.id.register_mm).setVisible(false);
        menu.findItem(R.id.about_mm).setVisible(false);
        menu.findItem(R.id.main_mm).setVisible(false);
        menu.findItem(R.id.settings_mm).setVisible(false);
        menu.findItem(R.id.gallery_mm).setVisible(false);
        menu.findItem(R.id.signout_mm).setVisible(false);
        return true;
    }

    // app bar
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        if(item.getItemId()==R.id.login_mm) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId()==R.id.exit_mm) {
            finish();
        }
        return true;
    }
}