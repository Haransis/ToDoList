package com.example.myhello.data.API;

import com.example.myhello.data.models.ListeToDo;
import com.example.myhello.data.models.ProfilListeToDo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // Demander un nouveau hash
    @POST("authenticate")
    public Call<Hash>  getHash(@Header("hash") String hash,
                               @Query("user") String user, @Query("password") String password);

    // Lister les listes
    @GET("lists")
    public Call<ProfilListeToDo> getLists(@Header("hash") String hash);

    // Ajouter une liste
    @POST("lists")
    public Call<ProfilListeToDo> addLists(@Header("hash") String hash, @Query("label") String label);

    // Lister les items
    @GET("lists/{id}/items")
    public Call<ListeToDo> getItems(@Header("hash") String hash, @Path("id") Integer id);

    // Cocher/décocher un item
    @PUT("lists/{idListe}/items/{idItem}")
    public Call<ListeToDo> cocherItems(@Header("hash") String hash,
                                       @Path("idListe") int idListe, @Path("idItem") int idItem,
                                       @Query("check") String check);

    // Ajouter un item
    @POST("lists/{idListe}/items")
    public Call<ListeToDo> addItem(@Header("hash") String hash,
                                         @Path("idListe") int idListe,
                                         @Query("label") String label, @Query("url") String url);

}
