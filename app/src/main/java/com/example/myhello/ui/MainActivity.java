package com.example.myhello.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
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
    private Button btnOK;
    private BroadcastReceiver networkChangeReceiver;
    String hash;

    private void alerter(String s) {
        Log.i(TAG,s);
        Toast myToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On relie les éléments du layout activity_main à l'activité :
        // On récupère le hash à utiliser.
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");

        btnOK = findViewById(R.id.btnOK); // Un bouton qui permet de valider le choix
        edtPseudo = findViewById(R.id.edtPseudo); // Un editText qui permet de saisir le choix
        edtPassword = findViewById(R.id.edtPassword); // Un editText qui permet de saisir le mot de passe
        btnOK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On récupère les informations des deux editTexts.
                String pseudo = edtPseudo.getText().toString();
                String password = edtPassword.getText().toString();

                // On les stocke dans les préférences pour qu'elles puissent
                // réapparaître lors du lancement de l'application.
                final SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.putString("pseudo", pseudo);
                editor.putString("password", password);
                editor.apply();

                // On change l'url de la factory
                ListeToDoServiceFactory.changeUrl(settings.getString("url","http://tomnab.fr/todo-api/"));

                // On demande la création d'un nouveau hash
                ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
                call = Interface.getHash(hash,pseudo,password);
                call.enqueue(new Callback<Hash>() {
                    @Override
                    public void onResponse(Call<Hash> call, Response<Hash> response) {
                        // Si les identifiants sont bons, on stocke le nouveau hash dans les préférences
                        // Remarque : l'appel à l'API génère une fois sur deux une réponse fausse.
                        // Je n'ai pas réussi à identifier le problème.
                        if(response.isSuccessful()){
                            editor.clear();
                            editor.putString("hash", String.valueOf(response.body()));
                            editor.apply();
                        }
                        else{
                            // Si les identifiants sont incorrects, le code est 400.
                            if(response.code()==400) {
                                Toast.makeText(MainActivity.this, "Pseudo ou mot de passe incorrect", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    // Si l'on échoue à faire le call.
                    @Override public void onFailure(Call<Hash> call, Throwable t) {
                        Toast.makeText(MainActivity.this,"Error code : ",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                    }
                });

                // On lance la nouvelle activité
                Intent intent = new Intent(getApplicationContext(),ChoixListActivity.class);
                unregisterReceiver(networkChangeReceiver);
                startActivity(intent);
            }
        });
    }

    // On affiche les derniers utilisateurs et mot de passe utilisés
    // i.e. ceux stockés dans les préférences.
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        edtPseudo.setText(settings.getString("pseudo","alban"));
        edtPassword.setText(settings.getString("password","alban"));

        // On instancie le broadcast receiver.
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

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
     * Il est nécessaire d'unregister le broadcast receiver.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }


    // La classe NetWorkChangeReceiver détecte en continue
    // si l'on a accès au réseau.
    // On l'implément au sein de chaque activité pour pouvoir y écrire
    // les instructions à effectuer lors d'un changement de réseau.
    public class NetworkChangeReceiver extends BroadcastReceiver {

        private static final String TAG = "NetworkChangeReceiverFromMain";
        public boolean isConnected;
        @Override
        public void onReceive(final Context context, final Intent intent) {

            isConnected = checkInternet(context);
            // On a récupéré l'accès à Internet
            if(isConnected){
                btnOK.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.DARKEN);
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