package com.example.myhello.data.API;

import com.example.myhello.data.models.ListeToDo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseProfil {
    @SerializedName("lists")
    public List<ListeToDo> mesListeToDo;

    @SerializedName("pseudo")
    public String login;
}
