package com.example.myhello.data.API;

import com.google.gson.annotations.SerializedName;

public class Hash {

    public String getHash() {
        return hash;
    }

    @SerializedName("hash")
    private String hash;
}
