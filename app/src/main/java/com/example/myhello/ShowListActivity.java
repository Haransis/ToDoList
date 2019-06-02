package com.example.myhello;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowListActivity extends AppCompatActivity{

    private ArrayList<String> mNomItem=new ArrayList<>();
    private String nomListe;
    private ProfilListeToDo profil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        // Cette condition permet d'éviter que l'application crashe
        // si l'activité précédente n'a rien renvoyé.
        if (getIntent().hasExtra("liste")){

            // Récupération du nom de la liste à afficher.
            nomListe = getIntent().getStringExtra("liste");
            Log.i("PMR",nomListe);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String filename = settings.getString("pseudo","");

        // On reconstruit le profil à partir de la lecture du fichier.
        ProfilListeToDo profil = lectureFromJson(filename);

        List<ListeToDo> Liste = profil.getMesListeToDo();

        // Construction de la liste à envoyer au RecyclerView
        if(profil.rechercherListe(nomListe)!=-1) {
            List<ItemToDo> ItemToDo = Liste.get(profil.rechercherListe(nomListe)).getLesItems();
            for (int k = 0; k < ItemToDo.size(); k++) {
                mNomItem.add(ItemToDo.get(k).getDescription());
            }
        }

        // On réutilise la même méthode que dans ChoixListActivity
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter2 adapter = new RecyclerViewAdapter2(mNomItem,profil,this,nomListe);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        final ProfilListeToDo finalProfil = profil;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreerAlertDialog(finalProfil);
            }
        });
    }

    private void CreerAlertDialog(final ProfilListeToDo finalProfil) {
        final EditText editText = new EditText(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Entrez le nom de la nouvelle tâche");
        alertDialogBuilder.setView(editText);
        alertDialogBuilder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ItemToDo newItem = new ItemToDo(editText.getText().toString());
                List<ListeToDo> Liste = finalProfil.getMesListeToDo();
                Liste.get(finalProfil.rechercherListe(nomListe)).ajouterItem(newItem);
                sauveProfilToJsonFile(finalProfil);
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

    public ProfilListeToDo lectureFromJson(String fileName){
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        FileInputStream inputStream;
        String sJsonLu="";
        ProfilListeToDo profil = new ProfilListeToDo();

        try {
            inputStream = openFileInput(fileName);
            int content;
            while ((content = inputStream.read()) != -1) {
                // convert to char and display it
                sJsonLu = sJsonLu+(char)content;
            }
            inputStream.close();
            profil = (ProfilListeToDo)gson.fromJson(sJsonLu,ProfilListeToDo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profil;
    }

}