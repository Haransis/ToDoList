package com.example.myhello.ui;

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
import com.example.myhello.R;

import java.util.List;

// La construction de ce 2e Adapter est quasiment identique au premier. Sauf qu'une partie du traitement se fait dans celui-ci.
// En effet, je n'ai pas réussi à relier la valeur que prenait la checkBox avec l'activité.
// Je me suis donc contenté de le faire lorsque l'on appelle OnBindViewHolder.
public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter2";
    private List<ItemToDo> mLesItems;

    public RecyclerViewAdapter2(List<ItemToDo> LesItems){
        this.mLesItems = LesItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem2, parent,false);
        return new RecyclerViewAdapter2.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mLesItems.size();
    }

    public void show(List<ItemToDo> mesItemToDo){
        mLesItems = mesItemToDo;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView nomListe;
        CoordinatorLayout parentLayout;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox =itemView.findViewById(R.id.checkbox);
            nomListe=itemView.findViewById(R.id.item);
            parentLayout=itemView.findViewById(R.id.parent_layout);
        }

    }

    // Une partie du traitement se fait ici.
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter2.ViewHolder holder, final int position) {

        holder.nomListe.setText(mLesItems.get(position).getDescription());

        final ItemToDo item = mLesItems.get(position);

        Log.d(TAG, "onBindViewHolder: " + item.getFait().toString());

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
                /*// On récupère la liste avec les identifiants
                List<ListeToDo> ListeListe = mProfil.getMesListeToDo();
                ListeToDo Liste = ListeListe.get(mProfil.rechercherListe(mNomListe));
                // Si l'item vient d'être coché
                if (buttonView.isChecked()){
                    // On le valide
                    Liste.validerItem(mNomItem.get(position));
                    mLesItems.get(position).getDescription();
                }
                else{
                    Liste.uncheckItem(mNomItem.get(position));
                    Log.i("PMR","UnChecked");
                }*/
            }
        });
    }

}
