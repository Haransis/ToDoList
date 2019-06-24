package com.example.myhello.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "listes")
public class ListeToDoDb {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "description")
    public String titre;

    @ColumnInfo(name="profilId")
    public int profilId;
}
