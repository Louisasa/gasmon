package com.softwire.training.gasmon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Receives messages and handles them */
public class EventHandler {
    public ConcurrentHashMap<String, Event> events = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Long> arrivalTime = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, ArrayList<Double>> locationAverages = new ConcurrentHashMap<>();
    private static String averagedEventsFileName = "averagedEvents.txt";

    public MessageId jsonIntoMessageId(String message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(message, MessageId.class);
    }

    public Event jsonIntoEvent(String message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Event event = gson.fromJson(message, Event.class);
        return event;
    }

    public void checkForDuplicates(Event event) {
        if (!this.events.containsKey(event.eventId)) {
            this.events.put(event.eventId, event);
            this.arrivalTime.put(event.eventId, event.timestamp);
        }
    }

    public String checkForEnglish(String message, String checkFor) {
        return message.contains(checkFor) ? message : null;
    }

    public double averageEvents(ArrayList<Event> eventsSixMinsAgo, ArrayList<Event> eventsFiveMinsAgo) {
        double total = 0;
        for (Event event : eventsFiveMinsAgo) {
            if (!eventsSixMinsAgo.contains(event)) {
                total += event.value;
            }
        }
        return total/(eventsFiveMinsAgo.size()-eventsSixMinsAgo.size());
    }

    public void addToAverageLocations(ArrayList<Event> eventsToAddToAverage) {
        for (Event event : eventsToAddToAverage) {
            if (this.locationAverages.contains(event.locationId)) {
                ArrayList<Double> currentValues = this.locationAverages.get(event.locationId);
                currentValues.add(event.value);
                this.locationAverages.put(event.locationId, currentValues);
            } else {
                this.locationAverages.put(event.locationId, new ArrayList<Double>(){{ add(event.value);}});
            }
        }
    }

    public void averageLocation() {
        for (Map.Entry<String, ArrayList<Double>> stringArrayListEntry : this.locationAverages.entrySet()) {
            Map.Entry pair = stringArrayListEntry;
            ArrayList<Double> averages = (ArrayList<Double>) pair.getValue();
            double count = 0;
            for (double value : averages) {
                count += value;
            }
            writeToFile("Average for " + (String) pair.getKey() + ": " + count/averages.size());
        }
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

    public ArrayList<Event> findOldEvents(Long timeAgo) {
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        for (Map.Entry<String, Long> stringDateTimeEntry : this.arrivalTime.entrySet()) {
            Map.Entry pair = stringDateTimeEntry;
            Long arrivalTimeLong = (Long) pair.getValue();
            if (timeAgo > arrivalTimeLong) {
                String eventId = (String) pair.getKey();
                eventsToRemove.add(this.events.get(eventId));
            }
        }
        return eventsToRemove;
    }

    public void trashOldEvents(ArrayList<Event> eventsToRemove) {

        for (Event event : eventsToRemove) {
            this.events.remove(event.eventId);
            this.arrivalTime.remove(event.eventId);
        }
    }
}
