package com.example.myhello;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;

public interface ProfilToJsonFile{


    void sauveProfilToJsonFile(ProfilListeToDo p);
}
