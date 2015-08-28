package se.kmdev.tvepg.epg.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import se.kmdev.tvepg.tag.EventTag;


/**
 * Created by admin on 25-Aug-15.
 */
public class Event implements Serializable {
    @SerializedName(EventTag.EVENT_NAME)
    private String name;
    @SerializedName(EventTag.EVENT_NAME_SHORT)
    private String shortName;
    @SerializedName(EventTag.EVENT_NAME_LONG)
    private String longName;
    @SerializedName(EventTag.EVENT_ID)
    private int id;
    @SerializedName(EventTag.EVENT_START)
    private String start;
    @SerializedName(EventTag.EVENT_END)
    private String end;
    @SerializedName(EventTag.EVENT_DATE)
    private String date;
    @SerializedName(EventTag.EVENT_STATUS)
    private int status;
    @SerializedName(EventTag.EVENT_START_MIN)
    private int startMin;
    @SerializedName(EventTag.EVENT_END_MIN)
    private int endMin;
    @SerializedName(EventTag.EVENT_START_TIME)
    private String timeStart;
    @SerializedName(EventTag.EVENT_END_TIME)
    private String timeEnd;

    public Event(String name, String shortName, String longName, int id, String start, String end, String date, int status, int startMin, int endMin, String timeStart, String timeEnd) {
        setName(name);
        setShortName(shortName);
        setLongName(longName);
        setId(id);
        setStart(start);
        setEnd(end);
        setStatus(status);
        setDate(date);
        setStartMin(startMin);
        setEndMin(endMin);
        setTimeStart(timeStart);
        setTimeEnd(timeEnd);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
}


