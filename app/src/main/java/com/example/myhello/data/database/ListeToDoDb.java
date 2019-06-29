package com.example.myhello.data.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "listes")
public class ListeToDoDb {
    @PrimaryKey
    @NonNull String mId;

    @ColumnInfo(name="titre")
    String titreListeToDo;

    @ColumnInfo(name="hashProfil")
    String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getmId() {
        return this.mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getTitreListeToDo() {
        return this.titreListeToDo;
    }

    public void setTitreListeToDo(String titreListeToDo) {
        this.titreListeToDo = titreListeToDo;
    }
}
