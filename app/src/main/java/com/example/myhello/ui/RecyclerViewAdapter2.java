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
    private Context mContext;
    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;

    public RecyclerViewAdapter2(Context context, List<ItemToDo> LesItems, int idListe){
        this.mLesItems = LesItems;
        this.mIdListe = idListe;
        this.mContext = context;
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

        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

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

                // On récupère le hash à utiliser.
                final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                String hash = settings.getString("hash","44692ee5175c131da83acad6f80edb12");
                ApiInterface Interface = ListeToDoServiceFactory.createService(ApiInterface.class);
                Call<ListeToDo> call;
                final String check;
                final int idItem = item.getId();
                // Si l'item vient d'être coché
                if (buttonView.isChecked()){
                    // On le valide
                    check = "1";
                }
                else{
                    check = "0";
                }
                boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
                if (isConnected){
                    call = Interface.cocherItems(hash,mIdListe,idItem,check);
                    call.enqueue(new Callback<ListeToDo>() {
                        @Override
                        public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                            Log.d(TAG, "onResponse: "+response.code());
                        }
                        @Override public void onFailure(Call<ListeToDo> call, Throwable t) {
                            Log.d("TAG", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                        }
                    });
                }
                else{
                    final RoomDatabase database = RoomListeToDoDb.getDatabase(mContext);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            ((RoomListeToDoDb) database).getItems().update(new ItemToDoDb(idItem,item.getDescription(),Integer.parseInt(check),mIdListe));
                        }
                    });
                }
            }
        });
    }
}
