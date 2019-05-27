package com.example.myhello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChoixListActivity extends AppCompatActivity /*implements ItemAdapter.ActionListener*/{

    private ArrayList<String> mNomListe=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_to_dos);

        /* Récupération du bundle de la première activité */

        Bundle b = this.getIntent().getExtras();
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        FileInputStream inputStream;
        String sJsonLu="";
        ProfilListeToDo profil = new ProfilListeToDo();
        String filename = b.getString("profil");

        try {
            inputStream = openFileInput(filename);
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
/*
        try {
            inputStream = openFileInput(filename);
            int content;
            while ((content = inputStream.read()) != -1) {
                // convert to char and display it
                sJsonLu = sJsonLu+(char)content;
            }
            inputStream.close();

            profil = (ProfilListeToDo)gson.fromJson(sJsonLu,ProfilListeToDo.class);
            Log.i("PMR",profil.getLogin());
        }
        catch (Exception e) {

            *//* Creation d'un profil par defaut *//*
            Log.i("TODO_ISA","Création du profil par défaut " + profil.getLogin());

            String fileContents = gson.toJson(profil);
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
                Log.i("TODO_ISA","Création du fichier monprofil_json");
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i("TODO_ISA","Impossible de créer le fichier de sauvegarde du profil par défaut");
            }

        }*/
//Ça c'était la correction de la prof.

        String s = "Listes des ToDo de ";
        s += profil.getLogin() + " :";


        TextView tv = findViewById(R.id.tvInfo);
        tv.setText(s);

        /*Avec les préférences*/

//        SharedPreferences preferencesAppli = PreferenceManager.getDefaultSharedPreferences(this);
//        String sPref = preferencesAppli.getString("cle","rien");
//        TextView tvPref = (TextView) findViewById(R.id.tvPref);
//        tvPref.setText(sPref);

        List<ListeToDo> ListeDesToDo = profil.getMesListeToDo();
        for (int k=0; k<ListeDesToDo.size();k++)
            mNomListe.add(ListeDesToDo.get(k).getTitreListeToDo());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(profil,"",mNomListe,this,ChoixListActivity.class);
//        ItemAdapter adapter = new ItemAdapter(mNomListe,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final EditText editText = findViewById(R.id.edtListe);
        final Button button = findViewById(R.id.button);
        final ProfilListeToDo finalProfil = profil;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListeToDo newListe = new ListeToDo();
                newListe.setTitreListeToDo(editText.getText().toString());
                finalProfil.ajouteListe(newListe);
                Log.i("PMR","Ajout de " + newListe.getTitreListeToDo());
                sauveProfilToJsonFile(finalProfil);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
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
