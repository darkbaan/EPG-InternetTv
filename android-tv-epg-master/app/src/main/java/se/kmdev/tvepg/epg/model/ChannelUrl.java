package se.kmdev.tvepg.epg.model;

import com.google.gson.annotations.SerializedName;

import se.kmdev.tvepg.tag.ChannelURLSTag;


/**
 * Created by admin on 25-Aug-15.
 */
public class ChannelUrl {
    @SerializedName(ChannelURLSTag.CURL_ID)
    private int id;
    @SerializedName(ChannelURLSTag.CURL_NAME)
    private String name;
    @SerializedName(ChannelURLSTag.CURL_PATH)
    private String path;

    public ChannelUrl(int id, String name, String path) {
        setId(id);
        setName(name);
        setPath(path);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
