package com.example.myhello.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemToDo implements Serializable {

    @SerializedName("label")
    private String description;

    @SerializedName("checked")
    private Boolean fait;

    public ItemToDo(String description, Boolean fait) {
        this.description = description;
        this.fait = fait;
    }

    public ItemToDo(String description) {
        this.description = description;
        this.fait = Boolean.FALSE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFait() {
        return fait;
    }

    public void setFait(Boolean fait) {
        this.fait = fait;
    }

    @Override
    public String toString() {
        return ("Item : "+ this.getDescription() + " - Fait : " +this.getFait().toString());

    }

}
