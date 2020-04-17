package edu.neu.khojak.LocationReminder;


import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.BsonValue;
import org.bson.Document;

public class Util {
    public static String userName;
    public final static StitchAppClient client =
            Stitch.initializeDefaultAppClient("khojak-gdxut");

    public final static RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, "khojak-data");

    public final static Task<StitchUser> stitchUserTask = client.getAuth()
            .loginWithCredential(new AnonymousCredential());

    public final static RemoteMongoCollection<Document> userCollection =
            mongoClient.getDatabase("khojak").getCollection("User");

    public final static RemoteMongoCollection<Document> groupCollection =
            mongoClient.getDatabase("khojak").getCollection("Group");

    public final static RemoteMongoCollection<Document> reminderCollection =
            mongoClient.getDatabase("khojak").getCollection("Reminder");

    public static String getId(BsonValue insertedId) {
        String intermediate[] = insertedId.toString().split("=");
        return intermediate[1].substring(0,intermediate[1].length() - 1);
    }
}
