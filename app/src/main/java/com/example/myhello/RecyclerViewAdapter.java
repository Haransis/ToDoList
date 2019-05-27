package com.example.myhello;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import java.io.FileOutputStream;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ProfilToJsonFile {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mNomListe=new ArrayList<>();
    private Context mContext;
    private Class classe;
    public ProfilListeToDo profil;
    public String NomListe;

    public RecyclerViewAdapter(ProfilListeToDo profil,String NomListe,ArrayList<String> mImageNames, Context mContext,Class classe) {
        this.mNomListe = mImageNames;
        this.mContext = mContext;
        this.classe = classe;
        this.profil = profil;
        this.NomListe = NomListe;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(classe==ChoixListActivity.class){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.layout_listitem,parent,false);
            ViewHolder viewHolder=new ViewHolder(itemView);
            return viewHolder;}
        else{
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.layout_listitem2,parent,false);
            ViewHolder viewHolder=new ViewHolder(itemView);
            return viewHolder;
        }
    }



    @Override
    public int getItemCount() {
        return mNomListe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nomListe;
        CoordinatorLayout parentLayout;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox =(CheckBox) itemView.findViewById(R.id.checkbox);
            nomListe=(TextView) itemView.findViewById(R.id.item);
            parentLayout=itemView.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.nomListe.setText(mNomListe.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classe == ChoixListActivity.class){
                    Intent intent = new Intent(mContext,ShowListActivity.class);
                    Bundle data = new Bundle();
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                    data.putString("profil",settings.getString("pseudo",""));
                    intent.putExtras(data);
                    intent.putExtra("liste",mNomListe.get(position));
                    Log.i("PMR",mNomListe.get(position));
                    mContext.startActivity(intent);
                }
                /*else{
                    if(checkBox.isChecked()){
                        Log.i("PMR","Checked");
                    } else {
                        Log.i("PMR","UnChecked");
                    }
                }*/

            }
        });
        if(classe==ShowListActivity.class){
            List<ListeToDo> ListeListe = profil.getMesListeToDo();
            ListeToDo Liste = ListeListe.get(profil.rechercherListe(NomListe));
            List<ItemToDo> ListeItem = Liste.getLesItems();
            if (ListeItem.get(Liste.rechercherItem(mNomListe.get(position))).getFait()){
                holder.checkBox.setChecked(true);
            }
            else{
                holder.checkBox.setChecked(false);
            }
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    List<ListeToDo> ListeListe = profil.getMesListeToDo();
                    ListeToDo Liste = ListeListe.get(profil.rechercherListe(NomListe));
                    if (buttonView.isChecked()){
                        Log.i("PMR","Checked");
                        Liste.validerItem(mNomListe.get(position));
                        sauveProfilToJsonFile(profil);}
                    else{
                        Log.i("PMR","UnChecked");
                    }
                }
            });
        }

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

