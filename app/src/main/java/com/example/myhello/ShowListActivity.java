package com.example.myhello;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowListActivity extends AppCompatActivity {

    private ArrayList<String> mNomItem=new ArrayList<>();
    private String nomListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        if (getIntent().hasExtra("liste")){
            nomListe = getIntent().getStringExtra("liste");
            Log.i("PMR",nomListe);
        }

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
            profil = gson.fromJson(sJsonLu,ProfilListeToDo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ListeToDo> Liste = profil.getMesListeToDo();

        if(profil.rechercherListe(nomListe)!=-1) {
            List<ItemToDo> ItemToDo = Liste.get(profil.rechercherListe(nomListe)).getLesItems();
            for (int k = 0; k < ItemToDo.size(); k++) {
                mNomItem.add(ItemToDo.get(k).getDescription());
            }
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(profil,nomListe,mNomItem,this,ShowListActivity.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
