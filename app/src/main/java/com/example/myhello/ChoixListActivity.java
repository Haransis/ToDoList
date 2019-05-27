package com.example.myhello;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

        String s = "Listes des ToDo de ";
        s += profil.getLogin() + " :";

        TextView tv = findViewById(R.id.tvInfo);
        tv.setText(s);

        List<ListeToDo> ListeDesToDo = profil.getMesListeToDo();
        for (int k=0; k<ListeDesToDo.size();k++)
            mNomListe.add(ListeDesToDo.get(k).getTitreListeToDo());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(profil,"",mNomListe,this,ChoixListActivity.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*final EditText editText = findViewById(R.id.edtListe);*/
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
        alertDialogBuilder.setMessage("Entrez le nom de la liste");
        alertDialogBuilder.setView(editText);
        alertDialogBuilder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
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



}
