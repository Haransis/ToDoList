package com.example.myhello.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myhello.data.API.ApiInterface;
import com.example.myhello.data.Network.ServiceManager;
import com.example.myhello.data.database.Converter;
import com.example.myhello.data.database.ItemToDoDb;
import com.example.myhello.data.database.ListeToDoDb;
import com.example.myhello.data.database.RoomListeToDoDb;
import com.example.myhello.data.models.ItemToDo;
import com.example.myhello.data.models.ListeToDo;
import com.example.myhello.data.API.ListeToDoServiceFactory;
import com.example.myhello.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowListActivity extends AppCompatActivity{

    private static final String TAG = "ShowListActivity";
    private String urlTest = "url test";
    private String idListe;
    private ListeToDo ListeDesToDo;
    private List<ItemToDo> listeDesItems;
    private RecyclerViewAdapter2 adapter;
    private BroadcastReceiver networkChangeReceiver;
    private Call<ListeToDo> call;
    public Converter converter;
    public RoomListeToDoDb database;
    ApiInterface Interface;
    String hash;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        converter = new Converter();

        // Cette condition permet d'éviter que l'application ne crashe
        // si l'activité précédente n'a rien renvoyé.
        if (getIntent().hasExtra("liste")){
            idListe = getIntent().getStringExtra("liste");
        }

        Log.d(TAG, "onCreate: "+idListe);

        //On instancie la base de donnée
        database = RoomListeToDoDb.getDatabase(getApplicationContext());

        // Construction d'une liste d'ItemToDo vide à envoyer au RecyclerViewAdapter1
        ListeDesToDo = new ListeToDo();
        listeDesItems = ListeDesToDo.getLesItems();

        // On réutilise la même méthode que dans ChoixListActivity
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter2(getApplicationContext(),listeDesItems,Integer.parseInt(idListe));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreerAlertDialog();
            }
        });

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        Interface = ListeToDoServiceFactory.createService(ApiInterface.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        networkChangeReceiver = new ShowListActivity.NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }


    private void CreerAlertDialog() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Entrez le nom de la nouvelle tâche");
        alertDialogBuilder.setView(editText);
        alertDialogBuilder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                add(editText.getText().toString());
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void add(String nomNewItem) {
        ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        call = Interface.addItem(hash, Integer.parseInt(idListe), nomNewItem, urlTest);
        call.enqueue(new Callback<ListeToDo>() {
            @Override
            public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                Log.d(TAG, "onResponse: "+response.code());
            }

            @Override public void onFailure(Call<ListeToDo> call, Throwable t) {
                Toast.makeText(ShowListActivity.this,"Error code : "+t,Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }


    private void syncFromAPI() {
        Log.d(TAG, "syncFromAPI: ");
        call = Interface.getItems(hash,Integer.parseInt(idListe));
        call.enqueue(new Callback<ListeToDo>() {
            @Override
            public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                if(response.isSuccessful()){
                    ListeToDo listeRecue = response.body();
                    if (listeRecue.isEmpty()){
                        Toast.makeText(ShowListActivity.this,"Liste vide",Toast.LENGTH_SHORT).show();}
                    else {
                        listeDesItems = listeRecue.getLesItems();
                        syncToBDD();
                        adapter.show(listeDesItems);
                    }
                }else {
                    Log.d("TAG", "onResponse: "+response.code());
                    Toast.makeText(ShowListActivity.this,"Error code : "+response.code(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onFailure(Call<ListeToDo> call, Throwable t) {
                Toast.makeText(ShowListActivity.this,"Error code : "+t,Toast.LENGTH_SHORT).show();
                Log.d("TAG", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    /**
     * Permet de faire la synchronisation avec la BDD
     */
    private void syncFromBDD(){
        Log.d(TAG, "syncFromBDD: ");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<ItemToDoDb> itemsDb = database.getItems().getAll(Integer.parseInt(idListe));
                listeDesItems = converter.fromItemDb(itemsDb);
            }
        });
        adapter.show(listeDesItems);
    }


    /**
     * Permet de récupérer les données de la BDD
     */
    public void syncToBDD(){
        Log.d(TAG, "syncToBDD: ");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<ItemToDoDb> itemsForDb = converter.fromItem(listeDesItems, Integer.parseInt(idListe));
                database.getItems().save(itemsForDb);
            }
        });
    }

    /**
     * Permettra à terme de synchroniser les changements avec l'API
     */
    public void syncToAPI(){
        Log.d(TAG, "syncToAPI: ");
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {


        private static final String TAG = "NetworkChangeReceiver";
        public boolean isConnected;

        @Override
        public void onReceive(final Context context, final Intent intent) {

            isConnected = checkInternet(context);
            // On a récupéré l'accès à Internet
            if(isConnected){
                syncToAPI();
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                syncFromAPI();
            }
            // On a perdu l'accès à Internet
            else{
                Toast.makeText(getApplicationContext(),"Réseau perdu, lecture depuis le cache", Toast.LENGTH_SHORT).show();
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                syncFromBDD();
            }

        }

        boolean checkInternet(Context context) {
            ServiceManager serviceManager = new ServiceManager(context);
            if (serviceManager.isNetworkAvailable()) {
                return true;
            } else {
                return false;
            }
        }
    }

}