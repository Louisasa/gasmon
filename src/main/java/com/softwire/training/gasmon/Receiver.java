package com.softwire.training.gasmon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Receives messages and handles them */
public class Receiver {
    private HashMap<String, Event> events = new HashMap<>();
    private HashMap<String, DateTime> arrivalTime = new HashMap<>();

    //todo:check for garbage etc

    public void jsonIntoJava(String message) {
        if (message.contains("eventId")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            MessageId messageId = gson.fromJson(message, MessageId.class);
            System.out.println(messageId.toString());
            Event event = gson.fromJson(messageId.toString(), Event.class);
            if (!events.containsKey(event.eventId)) {
                events.put(event.eventId, event);
                arrivalTime.put(event.eventId, new DateTime());

            }
        }
    }

    private void averageOfAllEvents() {}

    private ArrayList<Event> findOldEvents() {
        DateTime tenMinsAgo = new DateTime().minusMinutes(10);
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        for (Map.Entry<String, DateTime> stringDateTimeEntry : arrivalTime.entrySet()) {
            Map.Entry pair = stringDateTimeEntry;
            DateTime arrivalTime = (DateTime) pair.getValue();
            if (tenMinsAgo.isBefore(arrivalTime)) {
                Event event = (Event) pair.getKey();
                eventsToRemove.add(event);
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        return eventsToRemove;
    }

    public void trashOldEvents() {
        ArrayList<Event> eventsToRemove = findOldEvents();

        for (Event event : eventsToRemove) {
            events.remove(event);
            arrivalTime.remove(event);
        }
    }
}
