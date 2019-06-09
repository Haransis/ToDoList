package com.example.myhello.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.myhello.data.ApiInterface;
import com.example.myhello.data.ListeToDo;
import com.example.myhello.data.ListeToDoServiceFactory;
import com.example.myhello.data.ProfilListeToDo;
import com.example.myhello.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// L'activité implémente l'interface 'onClickListener'
// Une 'interface' est un "contrat"
// qui définit des fonctions à implémenter
// Ici, l'interface "onClickListener" demande que la classe
// qui l'implémente fournisse une méthode onClick

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final String CAT="PMR";
    private EditText edtPseudo = null;

    private void alerter(String s) {
        Log.i(CAT,s);
        Toast myToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On relie les éléments du layout activity_main à l'activité :


        Button btnOK = findViewById(R.id.btnOK); // Un bouton qui permet de valider le choix
        edtPseudo = findViewById(R.id.edtPseudo); // Un editText qui permet de saisir le choix

        // On demande à l'activité de déclencher un évènement lors d'un clique sur le bouton et l'editText.
        btnOK.setOnClickListener(this);
        edtPseudo.setOnClickListener(this);
    }

    // On affiche le dernier pseudo utilisé i.e. celui stocké dans les préférences
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        edtPseudo.setText(settings.getString("pseudo",""));

    }

    // Généralement, on préférera instancier localement la méthode onClick.
    // Ici, j'ai repris la correction du TP.
    @Override
    public void onClick(View v) {
        Intent myIntent = null;
        switch (v.getId()) {
            case R.id.btnOK : // si le clique est sur le bouton :
                String pseudo = edtPseudo.getText().toString();
                Log.i("PMR",pseudo);
                File file = new File(getApplicationContext().getFilesDir(),pseudo);

                // On vérifie si le fichier correspondant à ce qu'a rentré l'utilisateur existe
                // Si ce n'est pas le cas, on créée le fichier et on change les Préférences.
                if(!file.exists()) {
                    Log.i("PMR","le fichier n'existe pas");
                    ProfilListeToDo login = new ProfilListeToDo(pseudo);
                    sauveProfilToJsonFile(login);
                    //Sauvegarde du pseudo
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.putString("pseudo", login.getLogin());
                    editor.apply();
                    Log.i("PMR",settings.getString("pseudo",""));

                    //Passage à la nouvelle activité
                    myIntent = new Intent(this, ChoixListActivity.class);
                    startActivity(myIntent);
                    break;
                }

                // Sinon, on se contente de changer les Préférences.
                else{
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.putString("pseudo", pseudo);
                    editor.apply();
                    myIntent = new Intent(this, ChoixListActivity.class);
                    startActivity(myIntent);
                    break;
                }

            case R.id.edtPseudo : // si le clique est sur l'editText, on affiche un Toast
                alerter("Saisir votre pseudo");
                break;

        }
    }


    // permet la création de la barre de menu à partir du xml du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // permet de choisir
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

    // La méthode sauveProfilToJsonFile permet de sauvegarder un profil p sous
    // la forme d'un fichier profil.json
    public void sauveProfilToJsonFile(ProfilListeToDo p)
    {
        // un GsonBuilder permet la création d'une chaîne de caractère
        final GsonBuilder builder = new GsonBuilder();
        // le gson est la création du GsonBuilder
        final Gson gson = builder.create();

        // le nom du fichier à créer
        String filename = p.getLogin();
        // le contenu du fichier
        String fileContents = gson.toJson(p);

        // ce qui sortira de l'activité
        FileOutputStream outputStream;

        try {
            // on dit à l'outputStream d'écrire dans filename
            // si filename n'existe pas, il est créée
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            Log.i("PMR","Sauvegarde du fichier"+p.getLogin());
            Log.i("PMR",fileContents);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}