package com.example.myhello.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myhello.data.ApiInterface;
import com.example.myhello.data.ItemToDo;
import com.example.myhello.data.ListeToDo;
import com.example.myhello.data.ListeToDoServiceFactory;
import com.example.myhello.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowListActivity extends AppCompatActivity{

    private static final String TAG = "ShowListActivity";
    private String urlTest = "url test";
    private String idListe;
    private ListeToDo ListeDesToDo;
    private RecyclerViewAdapter2 adapter;
    private Call<ListeToDo> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        // Cette condition permet d'éviter que l'application crashe
        // si l'activité précédente n'a rien renvoyé.
        if (getIntent().hasExtra("liste")){
            idListe = getIntent().getStringExtra("liste");
        }

        Log.d(TAG, "onCreate: "+idListe);

        // Construction d'une liste d'ItemToDo vide à envoyer au RecyclerViewAdapter1
        ListeDesToDo = new ListeToDo();
        List<ItemToDo> listeItemVide = ListeDesToDo.getLesItems();

        // On réutilise la même méthode que dans ChoixListActivity
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter2(getApplicationContext(),listeItemVide,Integer.parseInt(idListe));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreerAlertDialog();
            }
        });
        sync();
    }

    private void CreerAlertDialog() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Entrez le nom de la nouvelle tâche");
        alertDialogBuilder.setView(editText);
        alertDialogBuilder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                add(editText.getText().toString());
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

    private void add(String nomNewItem) {
        // On récupère le hash à utiliser.
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        call = Interface.addItem(hash, Integer.parseInt(idListe), nomNewItem, urlTest);
        call.enqueue(new Callback<ListeToDo>() {
            @Override
            public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                Log.d(TAG, "onResponse: "+response.code());
            }

            @Override public void onFailure(Call<ListeToDo> call, Throwable t) {
                Toast.makeText(ShowListActivity.this,"Error code : "+t,Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }


    private void sync() {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
        ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
        call = Interface.getItems(hash,Integer.parseInt(idListe));
        call.enqueue(new Callback<ListeToDo>() {
            @Override
            public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                if(response.isSuccessful()){
                    ListeToDo listeRecue = response.body();
                    if (listeRecue.isEmpty()){
                        Toast.makeText(ShowListActivity.this,"Liste vide",Toast.LENGTH_SHORT).show();}
                    else {adapter.show(listeRecue.getLesItems());}
                }else {
                    Log.d("TAG", "onResponse: "+response.code());
                    Toast.makeText(ShowListActivity.this,"Error code : "+response.code(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onFailure(Call<ListeToDo> call, Throwable t) {
                Toast.makeText(ShowListActivity.this,"Error code : "+t,Toast.LENGTH_LONG).show();
                Log.d("TAG", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });

    }

}