package com.example.myhello.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "profils")
public class ProfilToDoDb {

    //On peut utiliser le hash comme clé primaire car il est unique
    @PrimaryKey
    private String hash;

    @ColumnInfo(name = "pseudo")
    private String pseudo;

    @ColumnInfo(name = "password")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

}
