package com.softwire.training.gasmon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Receives messages and handles them */
public class EventHandler {
    public HashMap<String, Event> events = new HashMap<>();
    public HashMap<String, Long> arrivalTime = new HashMap<>();
    private static String averagedEventsFileName = "averagedEvents.txr";

    public MessageId jsonIntoMessageId(String message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(message, MessageId.class);
    }

    public Event jsonIntoEvent(String message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(message, Event.class);
    }

    public void checkForDuplicates(Event event) {
        if (!events.containsKey(event.eventId)) {
            events.put(event.eventId, event);
            arrivalTime.put(event.eventId, event.timestamp);
        }
    }

    public String checkForEnglish(String message) {
        return message.contains("eventId") ? message : null;
    }

    public int averageEvents(ArrayList<Event> eventsSixMinsAgo, ArrayList<Event> eventsFiveMinsAgo) {
        return eventsFiveMinsAgo.size()-eventsSixMinsAgo.size();
    }

    public void writeToFile(String lineToBeWrittenToFile) {
        try(FileWriter fw = new FileWriter(averagedEventsFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(lineToBeWrittenToFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Event> findOldEvents(DateTime timeAgo) {
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        for (Map.Entry<String, Long> stringDateTimeEntry : arrivalTime.entrySet()) {
            Map.Entry pair = stringDateTimeEntry;
            Long arrivalTime = (Long) pair.getValue();
            if (timeAgo.isAfter(arrivalTime)) {
                String eventId = (String) pair.getKey();
                eventsToRemove.add(events.get(eventId));
            }
        }
        return eventsToRemove;
    }

    public void trashOldEvents(ArrayList<Event> eventsToRemove) {

        for (Event event : eventsToRemove) {
            events.remove(event.eventId);
            arrivalTime.remove(event.eventId);
        }
    }
}
