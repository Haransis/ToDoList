package com.example.myhello.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // Lister les listes
    @GET("lists")
    public Call<ProfilListeToDo> getLists(@Header("hash") String hash);

    @POST("lists")
    public Call<ProfilListeToDo> addLists(@Header("hash") String hash, @Query("label") String label);

    @GET("lists/{id}")
    public Call<ListeToDo> getItems(@Header("hash") String hash, @Path("id") String id);

}
