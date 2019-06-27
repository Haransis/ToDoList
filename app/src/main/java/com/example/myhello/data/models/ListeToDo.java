package com.example.myhello.data.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListeToDo implements Serializable {

    @SerializedName("id")
    private String mId;

    @SerializedName("label")
    public String titreListeToDo;

    @SerializedName("items")
    private List<ItemToDo> lesItems;

    public ListeToDo() {
        lesItems = new ArrayList<ItemToDo>();
    }

    public ListeToDo(String titreListeToDo, List<ItemToDo> lesItems, String id) {
        this.titreListeToDo = titreListeToDo;
        this.lesItems = lesItems;
        this.mId = id ;
    }

    public ListeToDo(String titreListeToDo, String id) {
        this.titreListeToDo = titreListeToDo;
        lesItems = new ArrayList<>();
        this.mId = id ;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public ListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
        lesItems = new ArrayList<>();
    }

    public String getTitreListeToDo() {
        return titreListeToDo;
    }

    public Boolean isEmpty(){
        if(lesItems.isEmpty()){return true;}
        else{return false;}
    }

    public void setTitreListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
    }

    public List<ItemToDo> getLesItems() {
        return lesItems;
    }

    public void setLesItems(List<ItemToDo> lesItems) {
        this.lesItems = lesItems;
    }
    public void ajouterItem(ItemToDo unItem)
    {
        this.lesItems.add(unItem);
    }

    public Boolean uncheckItem(String s)
    {
        int indice = -1;

        if ((indice = rechercherItem(s)) >=0)
        {
            this.lesItems.get(indice).setFait(0);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean validerItem(String s)
    {
        int indice = -1;

        if ((indice = rechercherItem(s)) >=0)
        {
            this.lesItems.get(indice).setFait(1);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public int rechercherItem(String s)
    {
        int retour = -1;
        for (int i=0; i < this.lesItems.size() ;i++)
        {
            if (this.lesItems.get(i).getDescription().equals(s))
            {
                retour=i;
                i=this.lesItems.size();
            }
        }
        return retour;
    }

    @Override
    public String toString() {
        String retour;
        retour = "Liste : " + this.getTitreListeToDo()+ "Items : " + this.getLesItems().toString();
        return retour;
    }
}