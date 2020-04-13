package com.example.khojak.LocationReminder;

import android.util.Log;

import com.example.khojak.LocationReminder.POJO.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public void test(User user) {
        final StitchAppClient client =
                Stitch.initializeDefaultAppClient("khojak-gdxut");

        final RemoteMongoClient mongoClient =
                client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        final RemoteMongoCollection<Document> coll =
                mongoClient.getDatabase("khojak").getCollection("User");

        client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(
                task -> {
                    if (!task.isSuccessful()) {
                        Log.e("STITCH", "Login failed!");
                        throw task.getException();
                    }

                    final Document updateDoc = new Document(
                            "id",
                            task.getResult().getId()
                    );
                    user.setId(task.getResult().getId());
                    updateDoc.put("userName", user.getUsername());
                    updateDoc.put("latitude", user.getLatitude());
                    updateDoc.put("longitude", user.getLongitude());
                    updateDoc.put("group_names", user.getGroups());

                    return coll.updateOne(
                            null, updateDoc, new RemoteUpdateOptions().upsert(true)
                    );
                }
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("STITCH", "Found docs: " + task.getResult().toString());
                return;
            }
            Log.e("STITCH", "Error: " + task.getException().toString());
            task.getException().printStackTrace();
        });
    }
}
