import com.softwire.training.gasmon.Event;
import com.softwire.training.gasmon.EventHandler;
import com.softwire.training.gasmon.MessageId;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class EventHandlerTest {


    @Test
    void testJsonIntoMessageId() {
        String message = "{Message: ikjwnr3fgojwj}";
        EventHandler eventHandler = new EventHandler();
        MessageId messageId = eventHandler.jsonIntoMessageId(message);
        assertThat(messageId, instanceOf(MessageId.class));
    }

    @Test
    void testJsonIntoObject() {
        String message = "{locationId: 'kjwerhwerhujerjkl', eventId: 'jfrkbwerwefjhb', value: 20.092834, timestamp: 987341}";
        EventHandler eventHandler = new EventHandler();
        Object event = eventHandler.jsonIntoEvent(message);
        assertThat(event, instanceOf(Object.class));
    }

    @Test
    void testEventIsAddedToEventHashMap() {
        Long timestamp = new Long(987654363);
        Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb", 20.092834, timestamp);
        EventHandler eventHandler = new EventHandler();
        HashMap<String, Event> eventHashMap = new HashMap<>();
        eventHashMap.put(event.eventId, event);

        eventHandler.checkForDuplicates(event);

        assertThat(eventHandler.events, equalTo(eventHashMap));
    }

    @Test
    void testCheckForDuplicates() {
        Long timestamp = new Long(987654363);
        Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb", 20.092834, timestamp);

        EventHandler eventHandler = new EventHandler();
        ConcurrentHashMap<String, Event> eventHashMap = new ConcurrentHashMap<>();
        eventHashMap.put(event.eventId, event);
        eventHandler.events =  eventHashMap;

        eventHandler.checkForDuplicates(event);

        assertThat(eventHandler.events, equalTo(eventHashMap));
    }

    @Test
    void testCheckForEnglishReturnsMessage() {
        EventHandler eventHandler = new EventHandler();
        String message = "kjshdfw;lerjbhdfpoeventIdlkerfnkeffl";
        String result = eventHandler.checkForEnglish(message, "eventId");
        assertThat(result, equalTo(message));
    }

    @Test
    void testCheckForEnglishReturnsNull() {
        EventHandler eventHandler = new EventHandler();
        String message = "kjshdfw;lerjbhdfpoetIdlkerfnkeffl";
        String result = eventHandler.checkForEnglish(message, "eventId");
        assertThat(result, equalTo(null));
    }

//    @Test
//    void testAverageEvents() {
//        ArrayList<Event> eventsFiveMinsAgo = new ArrayList<>();
//        ArrayList<Event> eventsSixMinsAgo = new ArrayList<>();
//        Long timestamp = new Long(987654363);
//        for (int index = 0; index < 10; index ++) {
//            Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb"+index, 20.092834, timestamp);
//            eventsSixMinsAgo.add(event);
//            eventsFiveMinsAgo.add(event);
//        }
//        Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb1298371203", 20.092834, timestamp);
//        eventsFiveMinsAgo.add(event);
//
//        EventHandler eventHandler = new EventHandler();
//        double result = eventHandler.averageEvents(eventsSixMinsAgo, eventsFiveMinsAgo);
//
//        //assertThat(result, equalTo(1));
//    }

    @Test
    void testFindOldEvents() {
        DateTime timeAgo = new DateTime();
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        ConcurrentHashMap<String, Long> arrivalTime = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Event> events = new ConcurrentHashMap<>();
        for (int index = 5; index < 15; index++) {
            Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb"+index, 20.092834, System.currentTimeMillis() - (index*60000));
            arrivalTime.put(event.eventId, event.timestamp);
            events.put(event.eventId, event);
            if (index>10) {
                eventsToRemove.add(event);
            }
        }

        EventHandler eventHandler = new EventHandler();
        eventHandler.arrivalTime = arrivalTime;
        eventHandler.events = events;
        ArrayList<Event> results = eventHandler.findOldEvents(System.currentTimeMillis() - 600000);

        assertThat(results.size(), equalTo(eventsToRemove.size()));
    }

    @Test
    void testTrashOldEvents() {
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        ConcurrentHashMap<String, Long> arrivalTime = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Event> events = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Event> resultHashMap = new ConcurrentHashMap<>();
        Long timestamp = new Long(987654363);
        for (int index = 5; index < 15; index++) {
            Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb"+index, 20.092834, timestamp);
            arrivalTime.put(event.eventId, event.timestamp);
            events.put(event.eventId, event);
            if (index>10) {
                eventsToRemove.add(event);
            } else {
                resultHashMap.put(event.eventId, event);
            }
        }

        EventHandler eventHandler = new EventHandler();
        eventHandler.arrivalTime = arrivalTime;
        eventHandler.events = events;
        eventHandler.trashOldEvents(eventsToRemove);

        assertThat(eventHandler.events, equalTo(resultHashMap));

    }
}
