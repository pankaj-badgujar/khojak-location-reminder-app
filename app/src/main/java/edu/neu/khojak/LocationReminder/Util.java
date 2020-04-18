package edu.neu.khojak.LocationReminder;


import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.khojak.LocationTracker.adapters.PendingRequestsAdapter;

import static edu.neu.khojak.Constants.FRIEND_LIST;
import static edu.neu.khojak.Constants.PENDING_REQUESTS;
import static edu.neu.khojak.Constants.USERNAME;
import static edu.neu.khojak.LocationTracker.LocationTracker.spinnerDataAdapter;

public class Util {

    public static String userName;

    public final static StitchAppClient client =
            Stitch.initializeDefaultAppClient("khojak-gdxut");

    /* List of Object with Document.
     The Document object is as key value pair having following keys
     _id: id of the group,
     groupName: groupName,
     groupMembers: ArrayList with all the group members of this group*/
    public final static List<Document> groupData = new ArrayList<>();
    public final static List<String> friendList = new ArrayList<>();

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

    public final static RemoteMongoCollection<Document> trackingCollection =
            mongoClient.getDatabase("khojak").getCollection("Tracking");

    public static String getId(BsonValue insertedId) {
        String[] intermediate = insertedId.toString().split("=");
        return intermediate[1].substring(0, intermediate[1].length() - 1);
    }

    public static void fetchData() {
        userCollection.findOne(new Document(USERNAME, userName)).addOnCompleteListener(task -> {
            Document user = task.getResult();
            if (!task.isSuccessful() || user == null) {
                return;
            }
            Object object = user.get("groupIds");
            if (object == null) {
                return;
            }

            List<ObjectId> groupIds = ((List<String>) object).stream().map(ObjectId::new).collect(Collectors.toList());
            Document document = new Document("$in", groupIds);
            Document query = new Document("_id", document);
            RemoteFindIterable<Document> groupObject = groupCollection.find(query);
            groupObject.into(new ArrayList<>()).addOnCompleteListener(groupObjectList -> {
                if (groupObjectList.isSuccessful() && groupObjectList.getResult() != null) {
                    groupObjectList.getResult().stream().forEach(data -> {
                        if (groupData.stream().anyMatch(groupTest ->
                                data.get("_id").equals(groupTest.get("_id")))) {
                            return;
                        }
                        groupData.add(data);
                    });
                    if (GroupRemindersFragment.groupAdapter != null) {
                        GroupRemindersFragment.groupAdapter.notifyDataSetChanged();
                    }
                }
            });
        });
    }

    public static void fetchFriendList(){

        userCollection.findOne(new Document(USERNAME, userName)).addOnCompleteListener(task -> {

            Document user = task.getResult();
            if (!task.isSuccessful() || user == null) {
                return;
            }

            List<String> friendListCasted = (List<String>) user.get(FRIEND_LIST);
            friendListCasted.stream().forEach(friendFetchedFromDB -> {
                if (friendList.stream().anyMatch(friendAlreadyPresent -> friendAlreadyPresent.equals(friendFetchedFromDB))) {
                    return;
                }
                friendList.add(friendFetchedFromDB);
                if(spinnerDataAdapter != null){
                    spinnerDataAdapter.notifyDataSetChanged();
                }
            });

        });

    }

    public static void removeReminder(Document reminder) {
        Util.reminderCollection.deleteOne(reminder).addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                return;
            }
            String _id = (String) reminder.get("groupId");
            Util.groupCollection.findOne(new Document("_id",new ObjectId(_id))).addOnCompleteListener(groupObject -> {
                if(groupObject.isSuccessful() && groupObject.getResult() != null) {
                    Document group = groupObject.getResult();
                    List<String> reminderIds = (List<String>) group.get("reminderIds");
                    reminderIds.remove(reminder.get("_id").toString());
                    group.remove("reminderIds");
                    group.append("reminderIds",reminderIds);
                    Util.groupCollection.updateOne(new Document("_id",new ObjectId(_id)),group).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            //Do Something;
                        }
                    });
                }
            });
        });
    }

    public static void deletePendingRequest(PendingRequestsAdapter adapter, String requestToDelete){
        userCollection.findOne(new Document(USERNAME,userName)).addOnCompleteListener(userFound ->{
            Document user = userFound.getResult();
            if (!userFound.isSuccessful() || user == null) {
                return;
            }
            List<String> pendingRequests = (List<String>)user.get(PENDING_REQUESTS);
            pendingRequests.remove(requestToDelete);
            user.append(PENDING_REQUESTS,pendingRequests);
            userCollection.updateOne(new Document(USERNAME, userName), user).addOnCompleteListener(updateTask ->{
                if(updateTask.isSuccessful()){

                }
            });
            });
    }



    public static void acceptPendingRequest(PendingRequestsAdapter adapter, String requestToAccept) {

        // First add ourselves into their friend list

        userCollection.findOne(new Document(USERNAME, requestToAccept)).addOnCompleteListener(userFound ->{
            Document userToBefriend = userFound.getResult();
            if(!userFound.isSuccessful() || userToBefriend == null){
                return;
            }
            List<String> friendList = (List<String>)userToBefriend.get(FRIEND_LIST);
            friendList.add(userName);
            userToBefriend.append(FRIEND_LIST, friendList);
            userCollection.updateOne(new Document(USERNAME, requestToAccept), userToBefriend).addOnCompleteListener(updateTask -> {
                deletePendingRequest(adapter, requestToAccept);
            });
        });
    }
}
