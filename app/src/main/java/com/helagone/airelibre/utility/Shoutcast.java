package com.helagone.airelibre.utility;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HELA on 2/23/18.
 */

public class Shoutcast {
    private String name;

    @SerializedName("stream")
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
