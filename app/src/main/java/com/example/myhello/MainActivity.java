package com.example.myhello;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// L'activité implémente l'interface 'onClickListener'
// Une 'interface' est un "contrat"
// qui définit des fonctions à implémenter
// Ici, l'interface "onClickLister" demande que la classe
// qui l'implémente fournisse une méthode onClick

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ProfilToJsonFile {

    public final String CAT="PMR";
    private Button btnOK = null;
    private EditText edtPseudo = null;
    private Intent myIntent = null;
    public Bundle myBdl =null;

    private void alerter(String s) {
        Log.i(CAT,s);
        Toast myToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOK = findViewById(R.id.btnOK);
        edtPseudo = findViewById(R.id.edtPseudo);

        btnOK.setOnClickListener(this);
        edtPseudo.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        edtPseudo.setText(settings.getString("pseudo",""));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOK :
                String pseudo = edtPseudo.getText().toString();
                File file = new File(getApplicationContext().getFilesDir(),pseudo);
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

                else{
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.putString("pseudo", pseudo);

                    myIntent = new Intent(this, ChoixListActivity.class);
                    startActivity(myIntent);
                    break;
                }

            case R.id.edtPseudo :
                alerter("Saisir votre pseudo");
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_account:
                alerter("Menu Compte");
                break;

            case R.id.action_settings:
                Intent toSettings=new Intent(this,SettingsActivity.class);
                startActivity(toSettings);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void sauveProfilToJsonFile(ProfilListeToDo p)
    {
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String filename = p.getLogin();
        String fileContents = gson.toJson(p);
        FileOutputStream outputStream;

        try {
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
