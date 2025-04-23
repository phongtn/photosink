package com.wind.controller;

import com.wind.google.firestore.FirestoreRepository;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class DataControllerTest {

    /**
     * Test class for {@link DataController}.
     * This class contains test cases for the {@code findData} method to verify its behavior.
     */

    @Test
    void testFindData_SuccessfulFetch() {
        // Arrange
        FirestoreRepository mockRepo = mock(FirestoreRepository.class);
        Context mockContext = mock(Context.class);
        DataController controller = new DataController(mockRepo);

        String collection = "testCollection";
        String key = "testKey";
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("field", "value");

        when(mockContext.pathParam("collection")).thenReturn(collection);
        when(mockContext.pathParam("key")).thenReturn(key);
        when(mockRepo.readData(collection, key)).thenReturn(mockData);

        // Act
        controller.findData(mockContext);

        // Assert
        verify(mockRepo).readData(collection, key);
        verify(mockContext).json(mockData);
    }

    @Test
    void testFindData_EmptyData() {
        // Arrange
        FirestoreRepository mockRepo = mock(FirestoreRepository.class);
        Context mockContext = mock(Context.class);
        DataController controller = new DataController(mockRepo);

        String collection = "emptyCollection";
        String key = "emptyKey";
        Map<String, Object> mockData = new HashMap<>();

        when(mockContext.pathParam("collection")).thenReturn(collection);
        when(mockContext.pathParam("key")).thenReturn(key);
        when(mockRepo.readData(collection, key)).thenReturn(mockData);

        // Act
        controller.findData(mockContext);

        // Assert
        verify(mockRepo).readData(collection, key);
        verify(mockContext).json(mockData);
    }

    @Test
    void testFindData_NullData() {
        // Arrange
        FirestoreRepository mockRepo = mock(FirestoreRepository.class);
        Context mockContext = mock(Context.class);
        DataController controller = new DataController(mockRepo);

        String collection = "nullCollection";
        String key = "nullKey";

        when(mockContext.pathParam("collection")).thenReturn(collection);
        when(mockContext.pathParam("key")).thenReturn(key);
        when(mockRepo.readData(collection, key)).thenReturn(null);

        // Act
        controller.findData(mockContext);

        // Assert
        verify(mockRepo).readData(collection, key);
        verify(mockContext).json(null);
    }
}