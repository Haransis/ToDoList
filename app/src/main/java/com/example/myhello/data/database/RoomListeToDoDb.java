package com.example.myhello.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;



@Database(entities = { ListeToDoDb.class, ItemToDoDb.class }, version = 3)
public abstract class RoomListeToDoDb extends RoomDatabase {

    public abstract ListeToDoDAO getListes();

    public abstract ItemToDoDAO getItems();

    private static RoomListeToDoDb INSTANCE;

    // permet de vérifier s'il y a déjà une instance de la database construite.
    public static RoomListeToDoDb getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (RoomListeToDoDb.class) {
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RoomListeToDoDb.class, "roomtodolist").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
