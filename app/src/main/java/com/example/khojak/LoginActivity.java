package com.example.khojak;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.khojak.LocationReminder.Util;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    EditText editText;
    private final String errorText = "Username is incorrect";
    private final String emptyText = "Username cannot be empty";
    private final String userAlreadyExist = "User with this username already exist Try a new one!!";
    public static volatile Boolean isLoggedIn = null;
    final static StitchAppClient client =
            Stitch.initializeDefaultAppClient("khojak-gdxut");

    final static RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, "khojak-data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText = findViewById(R.id.username);
    }

    public void onSignUpPressed(View view){

        if(!editText.getText().toString().equals("")) {
           addUser(editText.getText().toString());
        } else {
            editText.setError(emptyText);
        }
    }

    public void onLoginPressed(View view){
        authenticateUser(editText.getText().toString());
    }

    private void login() {
        startActivity(new Intent(this, HomePage.class));
        finish();
    }

    private void authenticateUser(String userName) {
        final RemoteMongoCollection<Document> coll =
                mongoClient.getDatabase("khojak").getCollection("User");
        Document document = new Document("username",userName);
        Task<StitchUser> stitchUserTask = client.getAuth().loginWithCredential(new AnonymousCredential());
        AtomicReference<Task<Document>> fetch = new AtomicReference<>();
        stitchUserTask.addOnCompleteListener(task -> {
            fetch.set(coll.findOne(document));
            fetch.get().addOnCompleteListener(fetchTask -> {
                if(fetchTask.isSuccessful() && fetchTask.getResult() != null) {
                    Util.userName = userName;
                       login();
                } else {
                    editText.setText(errorText);
                }
            });
        });
    }

    private void addUser(String userName) {
        final RemoteMongoCollection<Document> coll =
                mongoClient.getDatabase("khojak").getCollection("User");
        Document document = new Document("username",userName);
        Task<StitchUser> stitchUserTask = client.getAuth().loginWithCredential(new AnonymousCredential());
        AtomicReference<Task<Document>> fetch = new AtomicReference<>();
        stitchUserTask.addOnCompleteListener(task -> {
            fetch.set(coll.findOne(document));
            fetch.get().addOnCompleteListener(fetchTask -> {
                if(fetchTask.isSuccessful() && fetchTask.getResult() == null) {
                    coll.insertOne(document).addOnCompleteListener(insertTask -> {
                        if(insertTask.isSuccessful()) {
                            Util.userName = userName;
                            login();
                        }
                    });
                } else {
                    editText.setText(userAlreadyExist);
                }
            });
        });
    }
}
