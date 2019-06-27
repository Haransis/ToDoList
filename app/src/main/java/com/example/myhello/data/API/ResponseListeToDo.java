package com.example.myhello.data.API;


import com.example.myhello.data.models.ItemToDo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseListeToDo {
    @SerializedName("id")
    public String id;

    @SerializedName("label")
    public String titreListeToDo;

    @SerializedName("items")
    public List<ItemToDo> lesItems;
}
