package com.example.myhello.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListeToDoServiceFactory {
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://tomnab.fr/todo-api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <T> T createService(Class<T> type) {
        return retrofit.create(type);
    }
}