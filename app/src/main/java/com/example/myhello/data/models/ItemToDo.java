package com.example.myhello.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemToDo implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("label")
    private String description;

    @SerializedName("checked")
    private int fait;

    public int getListeId() {
        return listeId;
    }

    public void setListeId(int listeId) {
        this.listeId = listeId;
    }

    private int listeId;

    public void setId(int id) {
        this.id = id;
    }

    public ItemToDo(int id, String description, int fait, int listeId) {
        this.id = id;
        this.description = description;
        this.fait = fait;
        this.listeId = listeId;
    }


    public String getDescription() {
        return description;
    }

    public int getId() {return id;}

    public int getChecked() {
        return this.fait;
    }

    public Boolean getFait() {
        return fait == 1;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFait(int fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return ("Item : "+ this.getDescription() + " - Fait : " +this.getFait().toString());
    }

}
