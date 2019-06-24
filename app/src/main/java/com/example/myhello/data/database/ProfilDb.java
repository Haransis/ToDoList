package com.example.myhello.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profils")
public class ProfilDb {
    @PrimaryKey
    public int id;

    @ColumnInfo(name="pseudo")
    public String pseudo;
}
