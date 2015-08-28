package se.kmdev.tvepg.epg.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import se.kmdev.tvepg.tag.ChannelTag;


/**
 * Created by admin on 25-Aug-15.
 */
public class Channel implements Serializable {
    @SerializedName(ChannelTag.CHANNEL_ID)
    private int id;
    @SerializedName(ChannelTag.CHANNEL_NAME)
    private String name;
    @SerializedName(ChannelTag.CHANNEL_THUMB1)
    private String thumb1;
    @SerializedName(ChannelTag.CHANNEL_THUMB2)
    private String thumb2;
    @SerializedName(ChannelTag.CHANNEL_THUMB3)
    private String thumb3;
    @SerializedName(ChannelTag.CHANNEL_THUMB4)
    private String thumb4;
    @SerializedName(ChannelTag.CHANNEL_THUMB5)
    private String thumb5;
    @SerializedName(ChannelTag.CHANNEL_THUMB6)
    private String thumb6;
    @SerializedName(ChannelTag.CHANNEL_NAME_SHORT)
    private String nameShort;
    @SerializedName(ChannelTag.CHANNEL_NAME_LONG)
    private String nameLong;
    @SerializedName(ChannelTag.CHANNEL_COUNT_VIEW)
    private int countView;
    @SerializedName(ChannelTag.CHANNEL_COUNT_COMMENT)
    private int countComment;
    @SerializedName(ChannelTag.CHANNEL_COUNT_LIKE)
    private int countLike;
    @SerializedName(ChannelTag.CHANNEL_FREE)
    private int free;

    @SerializedName(ChannelTag.CHANNEL_CHANNEL_URLS)
    private ArrayList<ChannelUrl> channelUrls;

    public Channel(int id, String name, String thumb1, String thumb2, String thumb3, String thumb4, String thumb5, String thumb6,
                   String nameShort, String nameLong, int countView, int countComment, int countLike, int free,
                   ArrayList<ChannelUrl> channelUrls) {
        setId(id);
        setName(name);
        setThumb1(thumb1);
        setThumb2(thumb2);
        setThumb3(thumb3);
        setThumb4(thumb4);
        setThumb5(thumb5);
        setThumb6(thumb6);
        setNameShort(nameShort);
        setNameLong(nameLong);
        setCountLike(countLike);
        setCountComment(countComment);
        setCountView(countView);
        setFree(free);
        setChannelUrls(channelUrls);
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

    public String getThumb1() {
        return thumb1;
    }

    public void setThumb1(String thumb1) {
        this.thumb1 = thumb1;
    }

    public String getThumb2() {
        return thumb2;
    }

    public void setThumb2(String thumb2) {
        this.thumb2 = thumb2;
    }

    public String getThumb3() {
        return thumb3;
    }

    public void setThumb3(String thumb3) {
        this.thumb3 = thumb3;
    }

    public String getThumb4() {
        return thumb4;
    }

    public void setThumb4(String thumb4) {
        this.thumb4 = thumb4;
    }

    public String getThumb5() {
        return thumb5;
    }

    public void setThumb5(String thumb5) {
        this.thumb5 = thumb5;
    }

    public String getThumb6() {
        return thumb6;
    }

    public void setThumb6(String thumb6) {
        this.thumb6 = thumb6;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public String getNameLong() {
        return nameLong;
    }

    public void setNameLong(String nameLong) {
        this.nameLong = nameLong;
    }

    public int getCountView() {
        return countView;
    }

    public void setCountView(int countView) {
        this.countView = countView;
    }

    public int getCountComment() {
        return countComment;
    }

    public void setCountComment(int countComment) {
        this.countComment = countComment;
    }

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public ArrayList<ChannelUrl> getChannelUrls() {
        return channelUrls;
    }

    public void setChannelUrls(ArrayList<ChannelUrl> channelUrls) {
        this.channelUrls = channelUrls;
    }
}
