package com.example.myhello.data.API;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListeToDoServiceFactory {

    // L'Url de base.
    public static String Url = "http://tomnab.fr/todo-api/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Cette méthode est appelée lors d'un changement
    // d'Url de base.
    public static void changeUrl(String url){
        Url = url;
        // On a besoin de recréer l'instance retrofit.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> type) {
        return retrofit.create(type);
    }
}