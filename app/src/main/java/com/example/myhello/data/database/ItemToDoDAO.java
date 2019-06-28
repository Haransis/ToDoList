package com.example.myhello.data.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Permet de faire les requêtes à la table ItemToDo de la BdD
 */
@Dao
public interface ItemToDoDAO {


    /**
     * @param listeId le numéro de la ListeToDo propriétaire de l'Item
     * @return la liste des items associés à la ListeToDo
     */
    @Query("SELECT * FROM items WHERE idListe LIKE :listeId")
    List<ItemToDoDb> getAll(int listeId);


    /**
     * Permet de sauvegarder les Items dans la BdD
     * @param items les Items à sauvegarder
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<ItemToDoDb> items);


    /**
     * Permet de modifier un Item.
     * Pour l'instant, la méthode n'est utilisée que pour cocher/décocher l'Item
     * @param itemToDoDb l'item dont il faut changer l'état
     */
    @Update
    void update(ItemToDoDb itemToDoDb);
}
