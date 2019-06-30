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
import com.example.myhello.data.database.RoomListeModifieeDb;
import com.example.myhello.data.database.RoomListeToDoDb;
import com.example.myhello.data.database.Synchron;
import com.example.myhello.data.models.ItemToDo;
import com.example.myhello.data.models.ListeToDo;
import com.example.myhello.data.API.ListeToDoServiceFactory;
import com.example.myhello.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowListActivity extends AppCompatActivity implements RecyclerViewAdapter2.OnItemListener{

    private static final String TAG = "ShowListActivity";
    private String idListe;
    private List<ItemToDo> listeDesItems;
    private RecyclerViewAdapter2 adapter;
    private BroadcastReceiver networkChangeReceiver;
    private Call<ItemToDo> call2;
    private Synchron synchroniseur;
    private boolean modification = false;
    public Converter converter;
    public RoomListeToDoDb database;
    public RoomListeModifieeDb databaseModifiee;
    public SharedPreferences settings;
    ApiInterface Interface;
    String hash;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        // on instancie les objets de traitements
        converter = new Converter();
        synchroniseur = new Synchron(getApplicationContext());

        // Cette condition permet d'éviter que l'application ne crashe
        // si l'activité précédente n'a rien renvoyé.
        if (getIntent().hasExtra("liste")){
            idListe = getIntent().getStringExtra("liste");
        }

        Log.d(TAG, "onCreate: "+idListe);

        //On instancie les bases de donnée
        database = RoomListeToDoDb.getDatabase(getApplicationContext());
        databaseModifiee = RoomListeModifieeDb.getDatabase(getApplicationContext());

        // Construction de listes d'ItemToDo vide à envoyer au RecyclerViewAdapter1
        // et pour stocker les modifications effectuées en local.
        ListeToDo listeDesToDo = new ListeToDo();
        listeDesItems = listeDesToDo.getLesItems();

        // On réutilise la même méthode que dans ChoixListActivity
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter2(this,listeDesItems,Integer.parseInt(idListe));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreerAlertDialog();
            }
        });

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        Interface = ListeToDoServiceFactory.createService(ApiInterface.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        networkChangeReceiver = new ShowListActivity.NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        modification = settings.getBoolean("modifié", false);
    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(networkChangeReceiver);

        SharedPreferences.Editor editor = settings.edit();
        editor.remove("modifié");
        Log.d(TAG, "onStop: "+modification);
        editor.putBoolean("modifié", modification);
        editor.apply();
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
        String urlTest = "url test";
        findViewById(R.id.progess).setVisibility(View.VISIBLE);
        call2 = Interface.addItem(hash, Integer.parseInt(idListe), nomNewItem, urlTest);
        call2.enqueue(new Callback<ItemToDo>() {
            @Override
            public void onResponse(Call<ItemToDo> call, Response<ItemToDo> response) {
                Log.d(TAG, "onResponse: "+response.code());
                findViewById(R.id.progess).setVisibility(View.GONE);
            }

            @Override public void onFailure(Call<ItemToDo> call, Throwable t) {
                Toast.makeText(ShowListActivity.this,"Error code : "+t,Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                findViewById(R.id.progess).setVisibility(View.GONE);
            }
        });
    }


    private void syncFromAPI() {
        Log.d(TAG, "syncFromAPI: ");
        findViewById(R.id.progess).setVisibility(View.VISIBLE);
        Call<ListeToDo> call = Interface.getItems(hash, Integer.parseInt(idListe));
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
                findViewById(R.id.progess).setVisibility(View.GONE);
            }

            @Override public void onFailure(Call<ListeToDo> call, Throwable t) {
                Toast.makeText(ShowListActivity.this,"Error code : "+t,Toast.LENGTH_SHORT).show();
                Log.d("TAG", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                findViewById(R.id.progess).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Permet de faire la synchronisation depuis la BDD
     */
    private void syncFromBDD() throws ExecutionException, InterruptedException {
        Log.d(TAG, "syncFromBDD: ");
        Future<List<ItemToDoDb>> resultat = executor.submit(new Callable<List<ItemToDoDb>>() {
            @Override
            public List<ItemToDoDb> call() throws Exception {
                return database.getItems().getAll(Integer.parseInt(idListe));
            }
        });
        List<ItemToDoDb> listeDesItemsDb;
        try {
            listeDesItemsDb = resultat.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw e;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
        listeDesItems = converter.fromItemDb(listeDesItemsDb);
        adapter.show(listeDesItems);
    }


    /**
     * Permet d'envoyer les données vers la BDD
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

    private String getFaitConverti(ItemToDo item) {
        if(item.getFait()){return "1";}
        else{return "0";}
    }

    @Override
    public void onItemClick(final int position) {
        findViewById(R.id.progess).setVisibility(View.VISIBLE);

        //Lors du clique on actualise l'API/la BdD
        final ItemToDo itemACocher = listeDesItems.get(position);

        //On marque l'item comme coché
        if(itemACocher.getFait()){itemACocher.setFait(0);}
        else{itemACocher.setFait(1);}

        final String check = getFaitConverti(itemACocher);

        if (estConnecte()){
            call2 = Interface.cocherItems(hash,Integer.parseInt(idListe),itemACocher.getId(),check);
            call2.enqueue(new Callback<ItemToDo>() {
                @Override
                public void onResponse(Call<ItemToDo> call, Response<ItemToDo> response) {
                    Log.d(TAG, "onResponse: "+response.code());
                    findViewById(R.id.progess).setVisibility(View.GONE);
                }
                @Override public void onFailure(Call<ItemToDo> call, Throwable t) {
                    Log.d("TAG", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                    findViewById(R.id.progess).setVisibility(View.GONE);
                }
            });
        }

        else{
            modification = true;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ItemToDoDb itemToDoDb=converter.fromItem(itemACocher,Integer.parseInt(idListe));
                    database.getItems().update(itemToDoDb);
                    databaseModifiee.getItems().save(itemToDoDb);
                }
            });
        }
    }

    /**
     * @return un booléen indiquant l'état de connection
     */
    private boolean estConnecte(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     *  La classe NetWorkChangeReceiver détecte en continue
     *      si l'on a accès au réseau.
     *      On l'implément au sein de chaque activité pour pouvoir y écrire
     *      les instructions à effectuer lors d'un changement de réseau.
     */
    public class NetworkChangeReceiver extends BroadcastReceiver {
        private static final String TAG = "NetworkChangeReceiver";
        public boolean isConnected;

        @Override
        public void onReceive(final Context context, final Intent intent) {

            isConnected = checkInternet(context);
            // On a récupéré l'accès à Internet
            if(isConnected){
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                if(modification){
                    if(synchroniseur.syncItemsToApi(hash)==200) {
                        Toast.makeText(getApplicationContext(), "Synchronisation réussie", Toast.LENGTH_SHORT).show();
                        adapter.show(listeDesItems);
                        modification = false;
                    }else{
                        Toast.makeText(getApplicationContext(),"Synchronisation échouée", Toast.LENGTH_SHORT).show();
                    }
                }
                else{syncFromAPI();}
            }
            // On a perdu l'accès à Internet
            else{
                Toast.makeText(getApplicationContext(),"Réseau perdu, lecture depuis le cache", Toast.LENGTH_SHORT).show();
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                try {
                    syncFromBDD();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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