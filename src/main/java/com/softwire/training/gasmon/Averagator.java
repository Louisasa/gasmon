package com.softwire.training.gasmon;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;

public class Averagator {

    @Inject private EventHandler eventHandler;

    public double averageEvents(ArrayList<Event> eventsSixMinsAgo, ArrayList<Event> eventsFiveMinsAgo) {
        double total = 0;
        for (Event event : eventsFiveMinsAgo) {
            if (!eventsSixMinsAgo.contains(event)) {
                total += event.value;
            }
        }
        return total/(eventsFiveMinsAgo.size()-eventsSixMinsAgo.size());
    }

    public ConcurrentHashMap<String, ArrayList<Double>> addToAverageLocations(ArrayList<Event> eventsToAddToAverage, ConcurrentHashMap<String, ArrayList<Double>> locationAverages) {
        for (Event event : eventsToAddToAverage) {
            if (locationAverages.contains(event.locationId)) {
                ArrayList<Double> currentValues = locationAverages.get(event.locationId);
                currentValues.add(event.value);
                locationAverages.put(event.locationId, currentValues);
            } else {
                locationAverages.put(event.locationId, new ArrayList<Double>(){{ add(event.value);}});
            }
        }
        return locationAverages;
    }

    public void averageLocation(ConcurrentHashMap<String, ArrayList<Double>> locationAverages) {
        for (Map.Entry<String, ArrayList<Double>> stringArrayListEntry : locationAverages.entrySet()) {
            Map.Entry pair = stringArrayListEntry;
            ArrayList<Double> averages = (ArrayList<Double>) pair.getValue();
            double count = 0;
            for (double value : averages) {
                count += value;
            }
            eventHandler.writeToFile("Average for " + (String) pair.getKey() + ": " + count/averages.size());
        }
    }
}
