package com.example.myhello;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter2.ViewHolder holder, final int position) {
        holder.nomListe.setText(mNomItem.get(position));
        List<ListeToDo> ListeListe = mProfil.getMesListeToDo();
        ListeToDo Liste = ListeListe.get(mProfil.rechercherListe(mNomListe));
        List<ItemToDo> ListeItem = Liste.getLesItems();
        final ItemToDo item = ListeItem.get(Liste.rechercherItem(mNomItem.get(position)));

        if (item.getFait()){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<ListeToDo> ListeListe = mProfil.getMesListeToDo();
                ListeToDo Liste = ListeListe.get(mProfil.rechercherListe(mNomListe));
                if (buttonView.isChecked()){
                    Log.i("PMR","Checked");
                    Liste.validerItem(mNomItem.get(position));
                    sauveProfilToJsonFile(mProfil);}
                else{
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
