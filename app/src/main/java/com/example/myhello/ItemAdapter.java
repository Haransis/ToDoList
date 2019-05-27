/*
package com.example.myhello;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<String> items;
    private final ActionListener actionListener;

    interface ActionListener {
        public void onItemClicked(String data);
    }


    public ItemAdapter(List<String> items, ActionListener al) {
        this.actionListener = al;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (position ==0) return R.layout.item_header;
        else return R.layout.item;
    }

    @NonNull @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View itemView = inflater.inflate(R.layout.item,parent,false);

        View itemView = inflater.inflate(viewType,parent,false);

        return new ItemViewHolder(itemView);
    }

    @Override public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String itemData = items.get(position);
        holder.bind(itemData);

    }

    @Override public int getItemCount() {
        return items.size();
    }



    class ItemViewHolder extends RecyclerView.ViewHolder{

        private final TextView textView;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
            // Alternative : on peut aussi demander à la classe ItemViewHolder d'implémenter l'interface onClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        // On peut changer la donnée
                        String data = items.get(getAdapterPosition()) + " clicked !";
                        items.set(getAdapterPosition(),data);

                        notifyItemChanged(getAdapterPosition());

                        actionListener.onItemClicked(data);
                    }

                }
            });

        }

        public void bind(String itemData) {
            textView.setText(itemData);
        }
    }

}
*/
