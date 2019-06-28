package com.example.myhello.data.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class ItemToDoDb {
    @PrimaryKey
    private @NonNull int id;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo (name = "checked")
    private int fait;

    @ColumnInfo (name = "idListe")
    private int idListe;

    public ItemToDoDb(int id, String description, int fait, int idListe) {
        this.id = id;
        this.description = description;
        this.fait = fait;
        this.idListe = idListe;
    }

    public ItemToDoDb() {
    }

    public int getIdListe() {
        return idListe;
    }

    public void setIdListe(int idListe) {
        this.idListe = idListe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFait() {
        return fait;
    }

    public void setFait(int fait) {
        this.fait = fait;
    }

}
