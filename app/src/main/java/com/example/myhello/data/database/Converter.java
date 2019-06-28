package com.example.myhello.data.database;

import com.example.myhello.data.models.ItemToDo;
import com.example.myhello.data.models.ListeToDo;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public ListeToDoDb from(ListeToDo listeToDo) {
        ListeToDoDb listeToDoDb = new ListeToDoDb();
        listeToDoDb.setmId(listeToDo.getTitreListeToDo());
        listeToDoDb.setTitreListeToDo(listeToDo.getmId());
        return listeToDoDb;
    }

    public ListeToDo fromDb(ListeToDoDb listeToDoDb){
        return new ListeToDo(listeToDoDb.getmId(), listeToDoDb.getTitreListeToDo());
    }

    public List<ListeToDo> fromDb(List<ListeToDoDb> listesDb){
        List<ListeToDo> listes = new ArrayList<>(listesDb.size());
        for (ListeToDoDb listeToDoDb: listesDb){
            listes.add(fromDb(listeToDoDb));
        }
        return listes;
    }

    public List<ListeToDoDb> from(List<ListeToDo> listes){
        List<ListeToDoDb> listesDb = new ArrayList<>(listes.size());
        for (ListeToDo listeToDo: listes){
            listesDb.add(from(listeToDo));
        }
        return listesDb;
    }

    public ItemToDoDb fromItem(ItemToDo itemToDo, int idListe) {
        ItemToDoDb itemToDoDb = new ItemToDoDb();
        itemToDoDb.setId(itemToDo.getId());
        itemToDoDb.setDescription(itemToDo.getDescription());
        itemToDoDb.setIdListe(idListe);
        return itemToDoDb;
    }

    public ItemToDo fromItemDb(ItemToDoDb itemToDoDb){
        return new ItemToDo(itemToDoDb.getDescription(), itemToDoDb.getId());
    }

    public List<ItemToDo> fromItemDb(List<ItemToDoDb> itemsDb){
        List<ItemToDo> items = new ArrayList<>(itemsDb.size());
        for (ItemToDoDb itemToDoDb: itemsDb){
            items.add(fromItemDb(itemToDoDb));
        }
        return items;
    }

    public List<ItemToDoDb> fromItem(List<ItemToDo> items, int idListe){
        List<ItemToDoDb> itemsDb = new ArrayList<>(items.size());
        for (ItemToDo ItemToDo: items){
            itemsDb.add(fromItem(ItemToDo, idListe));
        }
        return itemsDb;
    }
}
