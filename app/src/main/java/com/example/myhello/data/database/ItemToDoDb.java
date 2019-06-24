package com.example.myhello.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class ItemToDoDb {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "fait")
    public int checked;

    @ColumnInfo(name="listeId")
    public int listeId;
}
