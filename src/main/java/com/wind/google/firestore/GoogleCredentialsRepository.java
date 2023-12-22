package com.wind.google.firestore;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GoogleCredentialsRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public final FirestoreRepository firestoreRepository;
    public final String COLLECTION_NAME = "users";

    @Inject
    public GoogleCredentialsRepository(FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    public Collection<StoredCredential> findAll() {
        logger.warn("This method not yet implement");
        //Todo this method not yet implement
        return new ArrayList<>();
    }

    public Set<String> findKeys() {
        logger.warn("This method not yet implement");
        //Todo this method not yet implement
        return new HashSet<>();
    }

    /**
     * Clear all collection's data
     */
    public void clearData() {
        firestoreRepository.deleteCollection(COLLECTION_NAME);
    }

    public StoredCredential getUserToken(String userId) {
        Map<String, Object> data = firestoreRepository.readData(COLLECTION_NAME, userId);
        if (!data.isEmpty()) {
            StoredCredential storedCredential = new StoredCredential();
            storedCredential.setExpirationTimeMilliseconds(Long.parseLong(data.get("expirationTimeSeconds").toString()));
            storedCredential.setAccessToken(data.get("accessKey").toString());
            storedCredential.setRefreshToken(data.get("refreshKey").toString());
            return storedCredential;
        }
        return null;
    }

    public void saveCredential(String userId, String accessToken, String refreshToken, long expirationTimeSeconds) {
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("accessKey", accessToken);
        mapData.put("refreshKey", refreshToken);
        mapData.put("expirationTimeSeconds", expirationTimeSeconds);
        firestoreRepository.insertData(COLLECTION_NAME, userId, mapData);
    }

    public void removeToken(String userId) {
        firestoreRepository.deleteData(COLLECTION_NAME, userId);
    }

}
