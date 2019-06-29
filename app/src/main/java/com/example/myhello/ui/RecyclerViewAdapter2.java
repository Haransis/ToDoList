package com.example.myhello.ui;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
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
import androidx.room.RoomDatabase;

import com.example.myhello.data.API.ApiInterface;
import com.example.myhello.data.database.ItemToDoDb;
import com.example.myhello.data.database.RoomListeToDoDb;
import com.example.myhello.data.models.ItemToDo;
import com.example.myhello.R;
import com.example.myhello.data.models.ListeToDo;
import com.example.myhello.data.API.ListeToDoServiceFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// La construction de ce 2e Adapter est quasiment identique au premier. Sauf qu'une partie du traitement se fait dans celui-ci.
// En effet, je n'ai pas réussi à relier la valeur que prenait la checkBox avec l'activité.
// Je me suis donc contenté de le faire lorsque l'on appelle OnBindViewHolder.
public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter2";
    private List<ItemToDo> mLesItems;
    private int mIdListe;
    OnItemListener mOnitemListener;


    public RecyclerViewAdapter2(OnItemListener onItemListener, List<ItemToDo> LesItems, int idListe){
        this.mOnitemListener = onItemListener;
        this.mLesItems = LesItems;
        this.mIdListe = idListe;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem2, parent,false);
        return new RecyclerViewAdapter2.ViewHolder(view,mOnitemListener);
    }

    @Override
    public int getItemCount() {
        return mLesItems.size();
    }

    public void show(List<ItemToDo> mesItemToDo){
        mLesItems = mesItemToDo;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter2.ViewHolder holder, final int position) {
        holder.bind(mLesItems.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nomListe;
        CoordinatorLayout parentLayout;
        CheckBox checkBox;
        OnItemListener onItemListener;

        ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            checkBox =itemView.findViewById(R.id.checkbox);
            nomListe=itemView.findViewById(R.id.item);
            parentLayout=itemView.findViewById(R.id.parent_layout);
            this.onItemListener = onItemListener;

            checkBox.setOnClickListener(this);
        }

        public void bind(ItemToDo itemToDo) {
            nomListe.setText(itemToDo.getDescription());
            checkBox.setChecked(itemToDo.getFait());
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }
}
