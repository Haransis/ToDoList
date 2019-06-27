package com.example.myhello.data.converter;

import com.example.myhello.data.API.ResponseListeToDo;
import com.example.myhello.data.models.ListeToDo;

public class Converter {

    public ListeToDo from(ResponseListeToDo responseListe){
        ListeToDo liste = new ListeToDo();
        liste.setmId(responseListe.id);
        liste.setLesItems(responseListe.lesItems);
        liste.setTitreListeToDo(responseListe.titreListeToDo);
        return liste;
    }
}
