package com.example.myhello.data.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ListeToDoDAO {
    @Query("SELECT * FROM listes WHERE hashProfil LIKE:hash")
    List<ListeToDoDb> getAll(String hash);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<ListeToDoDb> items);
}
