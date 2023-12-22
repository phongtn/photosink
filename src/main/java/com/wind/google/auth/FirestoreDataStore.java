package com.wind.google.auth;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.wind.google.firestore.GoogleCredentialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class FirestoreDataStore extends AbstractDataStore<StoredCredential> {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private final GoogleCredentialsRepository repository;

    /**
     * @param dataStoreFactory data store factory
     * @param id               data store ID
     */
    protected FirestoreDataStore(DataStoreFactory dataStoreFactory,
                                 GoogleCredentialsRepository repository, String id) {
        super(dataStoreFactory, id);
        this.repository = repository;
    }


    @Override
    public Set<String> keySet() {
        return repository.findKeys();
    }

    @Override
    public Collection<StoredCredential> values() {
        return repository.findAll();
    }

    @Override
    public StoredCredential get(String key) throws IOException {
        return repository.getUserToken(key);
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential value) {
        repository.saveCredential(key, value.getAccessToken(),
                value.getRefreshToken(),
                value.getExpirationTimeMilliseconds());
        return this;
    }

    @Override
    public DataStore<StoredCredential> clear() {
        repository.clearData();
        return this;
    }

    @Override
    public DataStore<StoredCredential> delete(String key) {
        repository.removeToken(key);
        return this;
    }

}