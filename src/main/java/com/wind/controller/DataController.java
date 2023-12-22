package com.wind.controller;

import com.google.inject.Inject;
import com.wind.google.firestore.FirestoreRepository;
import io.javalin.http.Context;

import java.util.Map;

public record DataController(FirestoreRepository firestoreRepository){
    @Inject
    public DataController {
    }

    public void findData(Context context) {
        Map<String, Object> data = firestoreRepository.readData(
                context.pathParam("collection"),
                context.pathParam("key"));
        context.json(data);
    }
}
