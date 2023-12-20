package com.wind.auth;

import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.inject.Inject;

import java.io.Serializable;

public class FirestoreDataStoreFactory implements DataStoreFactory {

    private final GoogleCredentialsRepository repository;

    @Inject
    public FirestoreDataStoreFactory(GoogleCredentialsRepository repository) {
        this.repository = repository;
    }

    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String id) {
        return (DataStore<V>) new FirestoreDataStore(this, repository, id);
    }
}
