package com.example.myhello;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerViewAdapter1.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter1";
    private ArrayList<String> mNomListe;
    private OnListListener mOnListListener;


    public RecyclerViewAdapter1(ArrayList<String> NomListe, OnListListener onListListener){
        this.mNomListe = NomListe;
        this.mOnListListener = onListListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent,false);
        return new ViewHolder(view,mOnListListener);
    }



    @Override
    public int getItemCount() {
        return mNomListe.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nomListe;
        CoordinatorLayout parentLayout;
        CheckBox checkBox;
        OnListListener mOnListListener;

        ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            checkBox =itemView.findViewById(R.id.checkbox);
            nomListe=itemView.findViewById(R.id.item);
            parentLayout=itemView.findViewById(R.id.parent_layout);
            this.mOnListListener = onListListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnListListener.onListClick(getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.nomListe.setText(mNomListe.get(position));
        }



    public interface OnListListener{
        void onListClick(int position);
    }
}

