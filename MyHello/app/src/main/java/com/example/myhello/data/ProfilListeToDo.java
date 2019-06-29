package com.example.myhello.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfilListeToDo implements Serializable {

    @SerializedName("lists")
    private List<ListeToDo> mesListeToDo;

    @SerializedName("pseudo")
    private String login;

    public ProfilListeToDo(List<ListeToDo> mesListeToDo, String login) {
        this.mesListeToDo = mesListeToDo;
        this.login = login;
    }

    public ProfilListeToDo(String login) {
        this.login = login;
        this.mesListeToDo = new ArrayList<>();
    }

    public ProfilListeToDo() {
        this.mesListeToDo = new ArrayList<>();
    }

    public ProfilListeToDo(List<ListeToDo> mesListeToDo) {
        this.mesListeToDo = mesListeToDo;
    }

    public List<ListeToDo> getMesListeToDo() {
        return mesListeToDo;
    }

    public void setMesListeToDo(List<ListeToDo> mesListeToDo) {
        this.mesListeToDo = mesListeToDo;
    }

    public String getLogin() {
        return login;
    }

    public boolean isEmpty(){
        if(mesListeToDo.isEmpty()){return true;}
        else{return false;}
    }

    public void setLogin(String login) {
        this.login = login;
    }
    public void ajouteListe(ListeToDo uneListe)
    {
        this.mesListeToDo.add(uneListe);
    }

    public int rechercherListe(String s)
    {
        int retour = -1; //la méthode renvoie -1 dans le cas où la Liste n'a pas été trouvée
        for (int i=0; i < this.mesListeToDo.size() ;i++)
        {
            if (this.mesListeToDo.get(i).getTitreListeToDo().equals(s)){
                retour=i;
                i=this.mesListeToDo.size();
            }
        }
        return retour;
    }

    @Override
    public String toString() {
        return "ProfilListeToDo{" + "mesListeToDo=" + mesListeToDo + ", login=" + login + '}';
    }


}
