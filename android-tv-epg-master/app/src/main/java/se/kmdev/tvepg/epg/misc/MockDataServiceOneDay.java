package se.kmdev.tvepg.epg.misc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.kmdev.tvepg.epg.domain.EPGChannel;
import se.kmdev.tvepg.epg.domain.EPGEvent;
import se.kmdev.tvepg.epg.model.Channel;
import se.kmdev.tvepg.epg.model.Event;
import se.kmdev.tvepg.epg.model.InfoChannel;

/**
 * Created by An DB on 15-05-24.
 */
public class MockDataServiceOneDay {

    public static Map<EPGChannel, List<EPGEvent>> getMockData(InfoChannel[] channels) {
        HashMap<EPGChannel, List<EPGEvent>> result = Maps.newLinkedHashMap();

        for (int i = 0; i < channels.length; i++) {
            Channel channel = channels[i].getChannel();
            EPGChannel epgChannel = new EPGChannel(channel.getThumb3(),
                    channel.getName(), Integer.toString(i));

            result.put(epgChannel, createEvents(channels[i].getEvents()));
        }

        return result;
    }

    private static List<EPGEvent> createEvents(ArrayList<Event> events) {
        List<EPGEvent> result = Lists.newArrayList();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            EPGEvent epgEvent = new EPGEvent(event.getStartMin() * 60 * 1000, event.getEndMin() * 60 * 1000, event.getShortName());
            result.add(epgEvent);
        }

        return result;
    }
}
