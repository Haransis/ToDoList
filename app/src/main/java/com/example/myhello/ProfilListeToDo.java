package com.example.myhello;

import android.util.Log;

import com.example.myhello.ListeToDo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfilListeToDo implements Serializable {
    private List<ListeToDo> mesListeToDo;
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

    public void setLogin(String login) {
        this.login = login;
    }
    public void ajouteListe(ListeToDo uneListe)
    {
        this.mesListeToDo.add(uneListe);
    }

    public int rechercherListe(String s)
    {
        int retour = -1;
        Boolean trouve = Boolean.FALSE;
        for (int i=0; i < this.mesListeToDo.size() && !trouve ;i++)
        {
            if (this.mesListeToDo.get(i).getTitreListeToDo().equals(s)){
                Log.i("PMR1",Integer.toString(retour));
                Log.i("PMR2",Integer.toString(i));
                retour=i;
                i=this.mesListeToDo.size();
                trouve=Boolean.TRUE;
            }
        }
        return retour;
    }

    @Override
    public String toString() {
        return "ProfilListeToDo{" + "mesListeToDo=" + mesListeToDo + ", login=" + login + '}';
    }


}
