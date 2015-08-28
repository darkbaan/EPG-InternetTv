package se.kmdev.tvepg.epg.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import se.kmdev.tvepg.tag.TAG;

/**
 * Created by admin on 27-Aug-15.
 */
public class InfoChannel implements Serializable {
    @SerializedName(TAG.CHANNEL)
    private Channel channel;
    @SerializedName(TAG.EVENTS)
    private ArrayList<Event> events;


    public InfoChannel(Channel channel, ArrayList<Event> events) {
        setChannel(channel);
        setEvents(events);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
