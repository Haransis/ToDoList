package com.example.myhello.ui;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.myhello.data.ApiInterface;
import com.example.myhello.data.Hash;
import com.example.myhello.data.ListeToDoServiceFactory;

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


        btnOK = findViewById(R.id.btnOK); // Un bouton qui permet de valider le choix
        edtPseudo = findViewById(R.id.edtPseudo); // Un editText qui permet de saisir le choix
        edtPassword = findViewById(R.id.edtPassword); // Un editText qui permet de saisir le mot de passe
        btnOK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On récupère les informations des deux editTexts.
                String pseudo = edtPseudo.getText().toString();
                String password = edtPassword.getText().toString();

                // On récupère le hash à utiliser.
                final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");

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

        // On vérifie que le réseau est accessible
        // Si ce n'est pas le cas, on désactive le bouton.
        if(!verifReseau()){
            btnOK.setEnabled(false);
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

    // La méthode sert à vérifier si le réseau est accessible
    public boolean verifReseau()
    {
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun réseau détecté";
        Boolean bStatut = false;
        if (netInfo != null)
        {

            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType)
                {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "Réseau mobile détecté"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "Réseau wifi détecté"; break;
                }

            }
        }

        this.alerter(sType);
        return bStatut;
    }
}