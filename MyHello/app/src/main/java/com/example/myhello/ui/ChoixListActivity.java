package com.example.myhello.ui;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhello.data.ApiInterface;
import com.example.myhello.data.ListeToDo;
import com.example.myhello.data.ListeToDoServiceFactory;
import com.example.myhello.data.ProfilListeToDo;
import com.example.myhello.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoixListActivity extends AppCompatActivity implements RecyclerViewAdapter1.OnListListener {

    private static final String TAG = "ChoixListActivity";
    private RecyclerViewAdapter1 adapter;
    private List<ListeToDo> ListeDesToDo;
    private Call<ProfilListeToDo> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_to_dos);

        // Construction d'une liste de listeToDo vide à envoyer au RecyclerViewAdapter1
        ProfilListeToDo profilVide = new ProfilListeToDo("random");
        ListeDesToDo = profilVide.getMesListeToDo();

        // Utilisation du RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        // Création de l'adapter qui va organiser les ItemHolders
        adapter = new RecyclerViewAdapter1(ListeDesToDo,this);
        recyclerView.setAdapter(adapter);
        // On implémente un LayoutManager basique au RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // On crée le bouton flottant qui permet d'ajouter des listes
        final FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        // Les variables ont besoin d'être déclarées en final car on les utilise dans un cast local.
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreerAlertDialog();
            }
        });

        //On fait appel à la méthode d'appel à la requête
        sync();
    }

    // La méthode CreerAlertDialog crée une fenêtre où l'utisateur peut
    // rentrer le nom de la nouvelle liste.
    private void CreerAlertDialog() {

        final EditText editText = new EditText(this);
        // Un AlertDialog fonctionne comme une «mini-activité».
        // Il demande à l'utisateur une valeur, la renvoie à l'activité et s'éteint.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Entrez le nom de la liste");
        alertDialogBuilder.setView(editText);
        // Cet AlertDialog comporte un bouton pour valider…
        alertDialogBuilder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                // Requête POST ici
                add(editText.getText().toString());
                // On relance l'activité pour la rafraîchir
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        // … et un bouton pour annuler, qui arrête l'AlertDialog.
        alertDialogBuilder.setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void add(String nomNewListe) {
        // On récupère le hash à utiliser.
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        call = Interface.addLists(hash,nomNewListe);
        call.enqueue(new Callback<ProfilListeToDo>() {
            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                Log.d(TAG, "onResponse: "+response.code());
            }

            @Override public void onFailure(Call<ProfilListeToDo> call, Throwable t) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }


    private void sync() {

        // On récupère le hash à utiliser.
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        // On fait la requête permettant de récupérer
        // la liste des listes de l'utilisateur connecté.
        call = Interface.getLists(hash);
        // On rajoute l'appel à la liste des tâches
        call.enqueue(new Callback<ProfilListeToDo>() {

            // Si l'on réussit à envoyer la requête
            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                // Dans le cas où la réponse indique un succès :
                if(response.isSuccessful()){
                    // On récupère le profil de l'utilisateur
                    ProfilListeToDo profilRecu = response.body();
                    // Si ce profil est vide, on envoie un Toast pour avertir l'utilisateur
                    if (profilRecu.isEmpty()){Toast.makeText(ChoixListActivity.this,"Liste vide",Toast.LENGTH_LONG).show();}
                    // Sinon, le profil
                    else {
                        ListeDesToDo = profilRecu.getMesListeToDo();
                        adapter.show(ListeDesToDo);}
                }
                // Dans le cas où la réponse indique un échec :
                // on montre un toast qui montre le code.
                else {
                    Log.d(TAG, "onResponse: "+response.code());
                    Toast.makeText(ChoixListActivity.this,"Error code : "+response.code(),Toast.LENGTH_LONG).show();
                }
            }

            // Si l'on ne réussit pas à envoyer la requête
            @Override public void onFailure(Call<ProfilListeToDo> call, Throwable t) {
                // On affiche un Toast.
                Toast.makeText(ChoixListActivity.this,"Error code : ",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });

    }

    // Instanciation de la méthode de l'interface onListListener.
    // Elle est appelée lors d'un clique sur un élément du RecyclerView.
    public void onListClick(int position) {
        // Lors du clique, on lance ShowListActivity.
        Intent intent = new Intent(this,ShowListActivity.class);

        // On envoie le nom de la liste sur laquelle le clique a été effectué.
        Bundle data = new Bundle();
        intent.putExtras(data);
        intent.putExtra("liste",ListeDesToDo.get(position).getId());

        this.startActivity(intent);
    }

}
