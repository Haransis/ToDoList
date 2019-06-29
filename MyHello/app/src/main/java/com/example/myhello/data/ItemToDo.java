package com.example.myhello.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemToDo implements Serializable {

    @SerializedName("label")
    private String description;

    @SerializedName("checked")
    private int fait;

    @SerializedName("id")
    private int id;

    public ItemToDo(String description, int fait) {
        this.description = description;
        this.fait = fait;
    }

    public ItemToDo(String description) {
        this.description = description;
        this.fait = 0;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {return id;}

    public Boolean getFait() {
        return fait == 1;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFait(int fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return ("Item : "+ this.getDescription() + " - Fait : " +this.getFait().toString());
    }

}
