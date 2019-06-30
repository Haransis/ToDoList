package com.example.myhello.data.database;

import android.content.Context;
import android.util.Log;

import com.example.myhello.data.API.ApiInterface;
import com.example.myhello.data.API.ListeToDoServiceFactory;
import com.example.myhello.data.models.ItemToDo;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Synchron{
    private static final String TAG = "Synchron";
    private Call<ItemToDo> call;
    private ApiInterface Interface;
    private RoomListeModifieeDb database;
    private Context context;
    private Converter converter;
    private List<ItemToDo> listeDesItemsModifies;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public Synchron(Context context) {
        this.context = context;
        database = RoomListeModifieeDb.getDatabase(context);
        Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        converter = new Converter();
    }

    private String getFaitConverti(ItemToDo item) {
        if(item.getFait()){return "1";}
        else{return "0";}
    }

    public void syncAllToApi(String hash, final Context context){
        syncItemsToApi(hash);
        syncListesToApi(hash);
        syncProfilsToApi(hash);
    }

    public int syncItemsToApi(String hash){

        Future<List<ItemToDoDb>> resultat = executor.submit(new Callable<List<ItemToDoDb>>() {
            @Override
            public List<ItemToDoDb> call() throws Exception {
                return database.getItems().getAllItems();
            }
        });
        List<ItemToDoDb> itemDbModifies;
        try {
            itemDbModifies = resultat.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
        listeDesItemsModifies = converter.fromItemDb(itemDbModifies);

        final int[] responseCode = {200};

        for (final ItemToDo itemACocher: listeDesItemsModifies){
            String check = getFaitConverti(itemACocher);
            int idItem = itemACocher.getId();
            int idListe = itemACocher.getListeId();

            call = Interface.cocherItems(hash, idListe, idItem, check);
            call.enqueue(new Callback<ItemToDo>() {
                @Override
                public void onResponse(Call<ItemToDo> call, Response<ItemToDo> response) {
                    if(response.isSuccessful()){
                        Log.d(TAG, "onResponse: "+itemACocher.getDescription()+" a été (dé)coché");
                    }
                    else{
                        Log.d(TAG, "onResponse: "+response.code());
                        responseCode[0] = response.code();
                    }
                }

                @Override
                public void onFailure(Call<ItemToDo> call, Throwable t) {
                    responseCode[0] = 0;
                }
            });
            if(responseCode[0] !=200)
                return responseCode[0];
        }
        return responseCode[0];
    }

    //TODO : à changer si l'on décide d'ajouter des items dans le cache
    public void syncListesToApi(String hash){

    }

    //TODO : à changer si l'on décide d'ajouter des listes dans le cache
    public void syncProfilsToApi(String hash){

    }

}
