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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myhello.data.API.ApiInterface;
import com.example.myhello.data.Network.ServiceManager;
import com.example.myhello.data.database.Converter;
import com.example.myhello.data.database.ListeToDoDb;
import com.example.myhello.data.database.RoomListeToDoDb;
import com.example.myhello.data.database.Synchron;
import com.example.myhello.data.models.ListeToDo;
import com.example.myhello.data.API.ListeToDoServiceFactory;
import com.example.myhello.data.models.ProfilListeToDo;
import com.example.myhello.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoixListActivity extends AppCompatActivity implements RecyclerViewAdapter1.OnListListener {

    private static final String TAG = "ChoixListActivity";
    private RecyclerViewAdapter1 adapter;
    private List<ListeToDo> ListeDesToDo;
    private Call<ProfilListeToDo> call;
    private BroadcastReceiver networkChangeReceiver;
    private FloatingActionButton floatingActionButton;
    private boolean modification;
    public RoomListeToDoDb database;
    public Synchron synchroniseur;
    public Converter converter;
    public SharedPreferences settings;
    String hash;
    ApiInterface Interface;
    ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_to_dos);

        converter = new Converter();
        synchroniseur = new Synchron(getApplicationContext());

        // Construction d'une liste de listeToDo vide à envoyer au RecyclerViewAdapter1
        ProfilListeToDo profilVide = new ProfilListeToDo("random");
        ListeDesToDo = profilVide.getMesListeToDo();

        // Utilisation du RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        // Création de l'adapter qui va organiser les ItemHolders
        adapter = new RecyclerViewAdapter1(ListeDesToDo,this);
        recyclerView.setAdapter(adapter);
        // On implémente un LayoutManager basique au RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //On instancie la base de donnée
        database = RoomListeToDoDb.getDatabase(getApplicationContext());

        // On crée le bouton flottant qui permet d'ajouter des listes
        floatingActionButton = findViewById(R.id.fab);
        // Les variables ont besoin d'être déclarées en final car on les utilise dans un cast local.
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreerAlertDialog();
            }
        });

        // On récupère le hash à utiliser.
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
    }

    @Override
    protected void onStart() {
        // On instancie le broadcast receiver.
        super.onStart();
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        modification = settings.getBoolean("modifié", false);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    /**
     *  La méthode CreerAlertDialog crée une fenêtre où l'utisateur peut
     *      rentrer le nom de la nouvelle liste.
     */
    private void CreerAlertDialog() {

        final EditText editText = new EditText(this);
        // Un AlertDialog fonctionne comme une «mini-activité».
        // Il demande à l'utisateur une valeur, la renvoie à l'activité et s'éteint.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Entrez le nom de la liste");
        alertDialogBuilder.setView(editText);
        // Cet AlertDialog comporte un bouton pour valider…
        alertDialogBuilder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                // Requête POST ici
                add(editText.getText().toString());
                // On relance l'activité pour la rafraîchir
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        // … et un bouton pour annuler, qui arrête l'AlertDialog.
        alertDialogBuilder.setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Cette méthode génère l'appel de la requête POST à l'API.
     * @param nomNewListe le titre de la nouvelle liste
     */
    private void add(String nomNewListe) {
        findViewById(R.id.progess).setVisibility(View.VISIBLE);

        // On récupère le hash à utiliser.
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        call = Interface.addLists(hash,nomNewListe);
        call.enqueue(new Callback<ProfilListeToDo>() {
            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                Log.d(TAG, "onResponse: "+response.code());
                findViewById(R.id.progess).setVisibility(View.GONE);
            }

            @Override public void onFailure(Call<ProfilListeToDo> call, Throwable t) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                findViewById(R.id.progess).setVisibility(View.GONE);
            }
        });
    }


    /**
     * Récupère les données depuis l'API.
     */
    private void syncFromAPI() {
        findViewById(R.id.progess).setVisibility(View.VISIBLE);

        // On fait la requête permettant de récupérer
        // la liste des listes de l'utilisateur connecté.
        call = Interface.getLists(hash);
        // On rajoute l'appel à la liste des tâches
        call.enqueue(new Callback<ProfilListeToDo>() {

            // Si l'on réussit à envoyer la requête
            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                Log.d(TAG, "onResponse: ");
                // Dans le cas où la réponse indique un succès :
                if(response.isSuccessful()){
                    // On récupère le profil de l'utilisateur
                    ProfilListeToDo profilRecu = response.body();
                    // Si ce profil est vide, on envoie un Toast pour avertir l'utilisateur
                    if (profilRecu.isEmpty()){Toast.makeText(ChoixListActivity.this,"Liste vide",Toast.LENGTH_SHORT).show();}
                    // Sinon, le profil
                    else {
                        ListeDesToDo = profilRecu.getMesListeToDo();
                        syncToBDD();
                        adapter.show(ListeDesToDo);}
                }
                // Dans le cas où la réponse indique un échec :
                // on affiche un toast qui montre le code d'erreur.
                else {
                    Log.d(TAG, "onResponse: "+response.code());
                    Toast.makeText(ChoixListActivity.this,"Error code : "+response.code(),Toast.LENGTH_SHORT).show();
                }
                findViewById(R.id.progess).setVisibility(View.GONE);
            }

            // Si l'on ne réussit pas à envoyer la requête
            @Override public void onFailure(Call<ProfilListeToDo> call, Throwable t) {
                // On affiche un Toast.
                Toast.makeText(ChoixListActivity.this,"Error code : ",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                findViewById(R.id.progess).setVisibility(View.GONE);
            }
        });

    }

    /**
     *  Instanciation de la méthode de l'interface onListListener.
     *      Elle est appelée lors d'un clique sur un élément du RecyclerView.
     * @param position l'entier indiquant sur quel item a eu lieu le clique.
     */
    public void onListClick(int position) {
        // Lors du clique, on lance ShowListActivity.
        Intent intent = new Intent(this,ShowListActivity.class);


        // On envoie le nom de la liste sur laquelle le clique a été effectué.
            Bundle data = new Bundle();
            intent.putExtras(data);
            intent.putExtra("liste", ListeDesToDo.get(position).getmId());

            this.startActivity(intent);
    }

    /**
     * Récupère les données de la BDD
     */
    private void syncFromBDD(){
        Log.d(TAG, "syncFromBDD: ");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<ListeToDoDb> listesDb = database.getListes().getAll(hash);
                final List<ListeToDo> listeAAfficher = converter.fromDb(listesDb);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListeDesToDo = listeAAfficher;
                        adapter.show(ListeDesToDo);
                    }
                });
            }
        });
        // on actualise les données de l'adapter
    }

    /**
     * Synchronise les données avec la BDD
     */
    public void syncToBDD(){
        Log.d(TAG, "syncToBDD: " + ListeDesToDo.get(0).getTitreListeToDo());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<ListeToDoDb> listeForDb = converter.from(ListeDesToDo, hash);
                Log.d(TAG, "run: "+listeForDb.get(0).getTitreListeToDo());
                database.getListes().save(listeForDb);
            }
        });
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
                Log.d(TAG, "onReceive: "+modification);
                if(modification){
                    if(synchroniseur.syncItemsToApi(hash)==200) {
                        Toast.makeText(getApplicationContext(), "Synchronisation réussie", Toast.LENGTH_SHORT).show();
                        modification = false;
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("modifié");
                        editor.apply();
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
