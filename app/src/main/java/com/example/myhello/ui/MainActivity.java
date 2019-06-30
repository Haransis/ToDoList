package com.example.myhello.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myhello.R;
import com.example.myhello.data.API.ApiInterface;
import com.example.myhello.data.API.Hash;
import com.example.myhello.data.API.ListeToDoServiceFactory;
import com.example.myhello.data.Network.ServiceManager;
import com.example.myhello.data.database.ProfilToDoDb;
import com.example.myhello.data.database.RoomListeToDoDb;
import com.example.myhello.data.database.Synchron;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// L'activité implémente l'interface 'onClickListener'
// Une 'interface' est un "contrat"
// qui définit des fonctions à implémenter
// Ici, l'interface "onClickListener" demande que la classe
// qui l'implémente fournisse une méthode onClick.

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private EditText edtPseudo = null;
    private EditText edtPassword = null;
    private Call<Hash> call;
    private String hash;
    private Button btnOK;
    private BroadcastReceiver networkChangeReceiver;
    private RoomListeToDoDb database;
    private Synchron synchroniseur;
    private boolean modification;
    ExecutorService executor = Executors.newSingleThreadExecutor();


    private void alerter(String s) {
        Log.i(TAG,s);
        Toast myToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        synchroniseur = new Synchron(getApplicationContext());

        //On instancie la base de donnée
        database = RoomListeToDoDb.getDatabase(getApplicationContext());

        // On relie les éléments du layout activity_main à l'activité :
        // On récupère le hash à utiliser.
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        Log.d(TAG, "onCreate: "+hash);

        btnOK = findViewById(R.id.btnOK); // Un bouton qui permet de valider le choix
        edtPseudo = findViewById(R.id.edtPseudo); // Un editText qui permet de saisir le choix
        edtPassword = findViewById(R.id.edtPassword); // Un editText qui permet de saisir le mot de passe
        btnOK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On récupère les informations des deux editTexts.
                final String pseudo = edtPseudo.getText().toString();
                final String password = edtPassword.getText().toString();

                SharedPreferences.Editor editor = settings.edit();
                editor.remove("pseudo");
                editor.remove("password");
                editor.putString("pseudo",pseudo);
                editor.putString("password",password);
                editor.apply();

                // On change l'url de la factory à partir des préférences
                ListeToDoServiceFactory.changeUrl(settings.getString("url","http://tomnab.fr/todo-api/"));

                // Si on est connecté,
                // on demande la création d'un nouveau hash que l'on stocke dans les préférences
                if (estConnecte()){
                    recupererHashFromAPI(pseudo, password, hash, settings);
                }

                //Sinon, on récupère le hash correspondant dans la BdD
                else{
                    recupererHashFromBdD(pseudo, password, settings);
                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        // On instancie le broadcast receiver.
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * On affiche les derniers utilisateurs et mot de passe utilisés
     * i.e. ceux stockés dans les préférences.
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        modification = settings.getBoolean("modifié", false);

        edtPseudo.setText(settings.getString("pseudo","alban"));
        edtPassword.setText(settings.getString("password","alban"));
    }

    /**
     * Il est nécessaire d'unregister le broadcast receiver.
     * On a besoin de le faire pendant le onPause pour qu'il arrête d'écouter au
     * changement d'activité.
     */
    @Override
    public void onPause() {
        super.onPause();
        try{
            unregisterReceiver(networkChangeReceiver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // permet la création de la barre de menu à partir du xml du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // permet de choisir quoi ouvrir
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_account: // dans le cas où l'utilisateur a choisi le menu Compte
                alerter("Menu Compte");
                break;

            case R.id.action_settings: // dans le cas où l'utilisateur a choisi les Préférences
                Intent toSettings=new Intent(this,SettingsActivity.class);
                startActivity(toSettings);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Permet de recuperer le hash depuis la BdD.
     * Si l'appel réussit, on lance ChoixListActivity.
     * @param pseudo le nom rentré par l'utilisateur
     */
    private void recupererHashFromBdD(final String pseudo, final String password, final SharedPreferences settings) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    ProfilToDoDb profilBDD = database.getProfil().getProfil(pseudo, password);
                    String newHash = profilBDD.getHash();
                    if (newHash.isEmpty()) {
                    } else {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("hash");
                        editor.putString("hash", newHash);
                        editor.apply();

                        // On lance la nouvelle activité
                        Intent intent = new Intent(getApplicationContext(), ChoixListActivity.class);
                        startActivity(intent);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Pseudo ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
     * Permet de recuperer le hash depuis l'API.
     * Si l'appel réussit on lance ChoixListActivity.
     * @param pseudo le pseudo rentré par l'utilisateur
     * @param password le mot de passe rentré par l'utilisateur
     * @param hash le hash associé au profil
     * @param settings Les paramètres de l'application
     */
    private void recupererHashFromAPI(final String pseudo, final String password, final String hash, final SharedPreferences settings) {
        ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        findViewById(R.id.progess).setVisibility(View.VISIBLE);
        call = Interface.getHash(hash,pseudo,password);
        call.enqueue(new Callback<Hash>() {
            @Override
            public void onResponse(Call<Hash> call, Response<Hash> response) {
                // Si les identifiants sont bons, on stocke le nouveau hash dans les préférences
                if(response.isSuccessful()){
                    final Hash hashRecu = response.body();
                    // On stocke les nouvelles informations de connexion dans les préférences pour qu'elles puissent
                    // réapparaître lors du lancement de l'application.
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove("hash");
                    editor.putString("hash", hashRecu.getHash());
                    editor.apply();

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            // on sauvegarde le profil dans la BdD.
                            ProfilToDoDb profilToSave = new ProfilToDoDb(hashRecu.getHash(), pseudo, password);
                            database.getProfil().save(profilToSave);
                        }
                    });

                    // On lance la nouvelle activité
                    Intent intent = new Intent(getApplicationContext(), ChoixListActivity.class);
                    startActivity(intent);
                }

                // Si les identifiants sont incorrects, le code est 400.
                if(response.code()==400) {
                    Toast.makeText(MainActivity.this, "Pseudo ou mot de passe incorrect", Toast.LENGTH_LONG).show();
                }

                // Si l'on a pas l'autorisation i.e. que le hash est faux, on le supprime.
                // Remarque : ce cas ne doit pas arriver mais on ne sait jamais.
                if (response.code()==403) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
                findViewById(R.id.progess).setVisibility(View.GONE);

            }
            // Si l'on échoue à faire le call.
            @Override public void onFailure(Call<Hash> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Error code : ",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                findViewById(R.id.progess).setVisibility(View.GONE);
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

        private static final String TAG = "NetworkChangeReceiverFromMain";
        public boolean isConnected;
        @Override
        public void onReceive(final Context context, final Intent intent) {

            isConnected = checkInternet(context);

            // On a récupéré l'accès à Internet
            if(isConnected){
                btnOK.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.DARKEN);
                if(modification){
                    if(synchroniseur.syncItemsToApi(hash)==200) {
                        Toast.makeText(getApplicationContext(), "Synchronisation réussie", Toast.LENGTH_SHORT).show();
                        modification = false;
                    }else{
                        Toast.makeText(getApplicationContext(),"Synchronisation échouée", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // On a perdu l'accès à Internet
            else{
                Toast.makeText(getApplicationContext(),"Réseau perdu, les modifications seront sauvegardées en local jusqu'au retour du réseau",Toast.LENGTH_SHORT).show();
                btnOK.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.DARKEN);
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