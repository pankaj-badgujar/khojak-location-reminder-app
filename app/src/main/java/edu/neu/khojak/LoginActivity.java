package edu.neu.khojak;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Task;

import org.bson.Document;

import java.util.concurrent.atomic.AtomicReference;

import edu.neu.khojak.LocationReminder.Util;

public class LoginActivity extends AppCompatActivity {

    EditText editText;
    private ProgressDialog progressDialog;

    public static volatile Boolean isLoggedIn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText = findViewById(R.id.username);

        progressDialog = new ProgressDialog(this);

        if (!checkPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("This feature needs Location Permission. Do you want to proceed?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            requestPermission(RequestType.NORMAL);
                        }).setNegativeButton("No", ((dialog, which) -> finish()))
                        .setIcon(android.R.drawable.ic_dialog_alert).create().show();
            } else {
                requestPermission(RequestType.NORMAL);
            }
        }

        if (!checkPermission()) {
            new AlertDialog.Builder(this)
                    .setTitle("This feature needs Location Permission. Do you want to proceed?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        requestPermission(RequestType.SPECIAL);
                    }).setNegativeButton("No", ((dialog, which) -> finish()))
                    .setIcon(android.R.drawable.ic_dialog_alert).create().show();
        }
    }

    public void onSignUpPressed(View view) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        if (!editText.getText().toString().equals("")) {
            addUser(editText.getText().toString());
        } else {
            editText.setError(getString(R.string.username_required));
        }
    }

    public void onLoginPressed(View view) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        authenticateUser(editText.getText().toString());

    }

    private void login() {
        startActivity(new Intent(this, HomePage.class));
        finish();
    }

    private void authenticateUser(String userName) {
        Document document = new Document("username", userName);
        Util.userCollection.findOne(document).addOnCompleteListener(fetchTask -> {
           if(fetchTask.isSuccessful() && fetchTask.getResult() != null) {
               Util.userName = userName;
               login();
           } else {
               editText.setText(getString(R.string.username_error));
           }
        });
    }

    private void addUser(String userName) {
        Document document = new Document("username", userName);
        AtomicReference<Task<Document>> fetch = new AtomicReference<>();
        Util.userCollection.findOne(document).addOnCompleteListener(fetchTask -> {
            if (fetchTask.isSuccessful() && fetchTask.getResult() == null) {
                Util.userCollection.insertOne(document).addOnCompleteListener(insertTask -> {
                    if (insertTask.isSuccessful()) {
                        Util.userName = userName;
                        login();
                    }
                });
            } else {
                editText.setError(getString(R.string.duplicate_user));
            }
        });
    }

    private boolean checkPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private enum RequestType {
        NORMAL, SPECIAL
    }

    private void createIntent() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + this.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        this.startActivity(i);
        finish();
    }

    private void requestPermission(RequestType request) {
        switch (request) {

            case NORMAL:
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        44
                );
                break;
            case SPECIAL:
                createIntent();
                break;
        }
    }


}
