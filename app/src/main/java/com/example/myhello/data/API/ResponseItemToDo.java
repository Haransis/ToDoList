package com.example.myhello.data.API;

import com.google.gson.annotations.SerializedName;

public class ResponseItemToDo {
    @SerializedName("label")
    public String description;

    @SerializedName("checked")
    public int fait;

    @SerializedName("id")
    public int id;
}
