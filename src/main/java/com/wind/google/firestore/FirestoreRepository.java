package com.wind.google.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreRepository {

    private static final Logger logger = LoggerFactory.getLogger(FirestoreRepository.class.getName());
    private final Firestore db;

    @Inject
    public FirestoreRepository(Firestore firestore) {
        this.db = firestore;
    }

    public Map<String, Object> readData(String collectionName, String key) {
        DocumentReference docRef = db.collection(collectionName).document(key);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot documentSnapshot = null;
        try {
            documentSnapshot = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
        if (documentSnapshot != null && documentSnapshot.exists())
            return documentSnapshot.getData();
        else
            return new HashMap<>();
    }

    /**
     * If the document does not exist, it will be created.
     * If the document does exist, its contents will be overwritten with the newly provided data,
     *
     * @param collectionName collection's name
     * @param key            data's key
     * @param docData        the documentation data
     */
    public boolean insertData(String collectionName, String key, Map<String, Object> docData) {
        boolean isOk = true;
        ApiFuture<WriteResult> future = db.collection(collectionName)
                .document(key)
                .set(docData, SetOptions.merge());
        try {
            Timestamp timestamp = future.get().getUpdateTime();
            logger.info("Document updated: {}", timestamp);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
            isOk = false;
        }
        return isOk;
    }

    public boolean deleteData(String collectionName, String key) throws RuntimeException {
        boolean isOk = true;
        ApiFuture<WriteResult> future = db.collection(collectionName)
                .document(key).delete();
        try {
            logger.info("delete object at {}", future.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
            isOk = false;
        }
        return isOk;
    }

    /**
     * Delete a collection in batches to avoid out-of-memory errors. Batch size may be tuned based on
     * document size (atmost 1MB) and application requirements.
     */
    public void deleteCollection(String collectionName) {
        CollectionReference collection = db.collection(collectionName);
        final int batchSize = 100;
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collectionName);
            }
        } catch (Exception e) {
            logger.error("Error deleting collection : {}", e.getMessage());
        }
    }
}
