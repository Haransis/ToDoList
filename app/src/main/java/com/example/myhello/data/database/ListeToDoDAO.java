package com.example.myhello.data.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myhello.data.models.ListeToDo;

import java.util.ArrayList;

@Dao
public interface ListeToDoDAO {
    @Query("SELECT * FROM listeToDo")
    ArrayList<ListeToDo> getListes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(ArrayList<ListeToDo> listes);
}
