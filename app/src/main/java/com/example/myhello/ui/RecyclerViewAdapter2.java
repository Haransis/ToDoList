package com.example.myhello.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhello.data.ItemToDo;
import com.example.myhello.data.ListeToDo;
import com.example.myhello.data.ProfilListeToDo;
import com.example.myhello.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

// La construction de ce 2e Adapter est quasiment identique au premier. Sauf qu'une partie du traitement se fait dans celui-ci.
// En effet, je n'ai pas réussi à relier la valeur que prenait la checkBox avec l'activité.
// Je me suis donc contenté de le faire lorsque l'on appelle OnBindViewHolder.
public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter2";
    private ArrayList<String> mNomItem;
    private ProfilListeToDo mProfil;
    private Context mContext;
    private String mNomListe;


    public RecyclerViewAdapter2(ArrayList<String> NomItem,ProfilListeToDo profil, Context context,String NomListe){
        this.mNomItem = NomItem;
        this.mProfil = profil;
        this.mContext = context;
        this.mNomListe = NomListe;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem2, parent,false);
        return new RecyclerViewAdapter2.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mNomItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nomListe;
        CoordinatorLayout parentLayout;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox =itemView.findViewById(R.id.checkbox);
            nomListe=itemView.findViewById(R.id.item);
            parentLayout=itemView.findViewById(R.id.parent_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    // Une partie du traitement se fait ici.
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter2.ViewHolder holder, final int position) {
        holder.nomListe.setText(mNomItem.get(position));

        // On est donc obligé de récupérer la liste et l'item cliqué ici.
        List<ListeToDo> ListeListe = mProfil.getMesListeToDo();
        ListeToDo Liste = ListeListe.get(mProfil.rechercherListe(mNomListe));
        List<ItemToDo> ListeItem = Liste.getLesItems();
        final ItemToDo item = ListeItem.get(Liste.rechercherItem(mNomItem.get(position)));

        // Permet d'initialiser les checkBox pour qu'elles correspondent
        // à ce que l'utilisateur a fait auparavant.
        // Normalement, cette étape se ferait dans le onStart() de ShowListActivity
        // mais je n'ai pas réussi à relier la valeur que prenait la checkBox avec l'Activité
        if (item.getFait()){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }

        // On déclenche un événement lorsque la checkBox est cochée
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<ListeToDo> ListeListe = mProfil.getMesListeToDo();
                ListeToDo Liste = ListeListe.get(mProfil.rechercherListe(mNomListe));
                // Si l'item vient d'être coché
                if (buttonView.isChecked()){
                    Log.i("PMR","Checked");
                    // On le valide
                    Liste.validerItem(mNomItem.get(position));
                    sauveProfilToJsonFile(mProfil);}
                else{
                    Liste.uncheckItem(mNomItem.get(position));
                    sauveProfilToJsonFile(mProfil);
                    Log.i("PMR","UnChecked");
                }
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
            outputStream= mContext.getApplicationContext().openFileOutput(filename, mContext.getApplicationContext().MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            Log.i("PMR","Sauvegarde du fichier"+p.getLogin());
            Log.i("PMR",fileContents);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
