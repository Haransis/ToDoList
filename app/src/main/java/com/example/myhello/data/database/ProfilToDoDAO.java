package com.example.myhello.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ProfilToDoDAO {
    @Query("SELECT * FROM profils WHERE pseudo LIKE:pseudoProfil AND password LIKE:passwordProfil")
    ProfilToDoDb getProfil(String pseudoProfil, String passwordProfil);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(ProfilToDoDb profilToDoDb);
}
