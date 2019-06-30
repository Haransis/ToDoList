package com.example.myhello.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


/**
 * Cette BdD permet d'enregistrer toutes les modifications effectuées dans le cache.
 * Son fonctionnement est le même que RoomListeToDoDb.
 */
@Database(entities = { ProfilToDoDb.class, ListeToDoDb.class, ItemToDoDb.class }, version = 1)
public abstract class RoomListeModifieeDb extends RoomDatabase {

    public abstract ProfilToDoDAO getProfil();

    public abstract ListeToDoDAO getListes();

    public abstract ItemToDoDAO getItems();

    private static RoomListeModifieeDb INSTANCE;

    // permet de vérifier s'il y a déjà une instance de la database construite.
    public static RoomListeModifieeDb getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (RoomListeToDoDb.class) {
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RoomListeModifieeDb.class, "roomtodolist").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}