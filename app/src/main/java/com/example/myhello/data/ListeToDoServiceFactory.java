package com.example.myhello.data;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListeToDoServiceFactory {

    public static String Url = "http://tomnab.fr/todo-api/";
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static void changeUrl(String url){
        Url = url;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> type) {
        return retrofit.create(type);
    }
}