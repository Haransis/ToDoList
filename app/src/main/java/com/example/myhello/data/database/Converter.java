package com.example.myhello.data.database;

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
        return new ListeToDo(listeToDoDb.getTitreListeToDo(), listeToDoDb.getmId());
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
}
