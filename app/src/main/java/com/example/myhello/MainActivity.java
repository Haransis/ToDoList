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
    public ListeToDo ListeTest;
    public ListeToDo ListeTest2;

    private void alerter(String s) {
        Log.i(CAT,s);
        Toast myToast = Toast.makeText(this,s,Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemToDo item1 = new ItemToDo("Arroser les plantes");
        ItemToDo item2 = new ItemToDo("GoFluent");
        ItemToDo item3 = new ItemToDo("Vaisselle");
        ItemToDo item4 = new ItemToDo("Lessive");
        ItemToDo item5 = new ItemToDo("Planter");
        ItemToDo item6 = new ItemToDo("Semer");
        ItemToDo item7 = new ItemToDo("Imaginer");

        ListeTest = new ListeToDo();

        ListeTest.setTitreListeToDo("Quotidien");
        ListeTest.ajouterItem(item1);
        ListeTest.ajouterItem(item2);
        ListeTest.ajouterItem(item3);
        ListeTest.ajouterItem(item4);

        ListeTest2 = new ListeToDo();

        ListeTest2.setTitreListeToDo("Nouveau");
        ListeTest2.ajouterItem(item5);
        ListeTest2.ajouterItem(item6);
        ListeTest2.ajouterItem(item7);

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
                ProfilListeToDo login = new ProfilListeToDo(pseudo);
                login.ajouteListe(ListeTest);
                login.ajouteListe(ListeTest2);
                sauveProfilToJsonFile(login);

                //Sauvegarde du pseudo
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.putString("pseudo",pseudo);

                //Ajout de l'heure de connexion
                SimpleDateFormat sdf = new SimpleDateFormat(("yyyyMMdd_HH:mm"));
                String currentDateAndTime = sdf.format(new Date());
                editor.putString("dateLogin",currentDateAndTime);
                editor.commit();


                //Passage à la nouvelle activité
                //On stocke dans le bundle
                myIntent = new Intent(this,ChoixListActivity.class);
                Bundle data = new Bundle();
                data.putString("profil",login.getLogin());
                myIntent.putExtras(data);
                startActivity(myIntent);
                break;

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
