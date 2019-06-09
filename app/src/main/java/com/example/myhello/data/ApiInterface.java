package com.example.myhello.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface ApiInterface {

    // Lister les listes
    @GET("lists")
    public Call<ProfilListeToDo> getLists(@Header("hash") String hash );

}
