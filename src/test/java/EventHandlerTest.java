import com.softwire.training.gasmon.Event;
import com.softwire.training.gasmon.EventHandler;
import com.softwire.training.gasmon.MessageId;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

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
    void testJsonIntoEvent() {
        String message = "{locationId: 'kjwerhwerhujerjkl', eventId: 'jfrkbwerwefjhb', value: 20.092834, timestamp: 987341}";
        EventHandler eventHandler = new EventHandler();
        Event event = eventHandler.jsonIntoEvent(message);
        assertThat(event, instanceOf(Event.class));
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
        HashMap<String, Event> eventHashMap = new HashMap<>();
        eventHashMap.put(event.eventId, event);
        eventHandler.events =  eventHashMap;

        eventHandler.checkForDuplicates(event);

        assertThat(eventHandler.events, equalTo(eventHashMap));
    }

    @Test
    void testCheckForEnglishReturnsMessage() {
        EventHandler eventHandler = new EventHandler();
        String message = "kjshdfw;lerjbhdfpoeventIdlkerfnkeffl";
        String result = eventHandler.checkForEnglish(message);
        assertThat(result, equalTo(message));
    }

    @Test
    void testCheckForEnglishReturnsNull() {
        EventHandler eventHandler = new EventHandler();
        String message = "kjshdfw;lerjbhdfpoetIdlkerfnkeffl";
        String result = eventHandler.checkForEnglish(message);
        assertThat(result, equalTo(null));
    }

    @Test
    void testAverageEvents() {
        ArrayList<Event> eventsFiveMinsAgo = new ArrayList<>();
        ArrayList<Event> eventsSixMinsAgo = new ArrayList<>();
        Long timestamp = new Long(987654363);
        for (int index = 0; index < 10; index ++) {
            Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb"+index, 20.092834, timestamp);
            eventsSixMinsAgo.add(event);
            eventsFiveMinsAgo.add(event);
        }
        Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb1298371203", 20.092834, timestamp);
        eventsFiveMinsAgo.add(event);

        EventHandler eventHandler = new EventHandler();
        int result = eventHandler.averageEvents(eventsSixMinsAgo, eventsFiveMinsAgo);

        assertThat(result, equalTo(1));
    }

//    @Test
//    void testWriteToFile() {
////        try(FileWriter fw = new FileWriter(averagedEventsFileName, true);
////            BufferedWriter bw = new BufferedWriter(fw);
////            PrintWriter out = new PrintWriter(bw))
////        {
////            out.println(lineToBeWrittenToFile);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }

    @Test
    void testFindOldEvents() {
        DateTime timeAgo = new DateTime();
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        HashMap<String, Long> arrivalTime = new HashMap<>();
        HashMap<String, Event> events = new HashMap<>();
        for (int index = 5; index < 15; index++) {
            Event event = new Event("kjwerhwerhujerjkl", "jfrkbwerwefjhb"+index, 20.092834, timeAgo.minusMinutes(index).getMillis());
            arrivalTime.put(event.eventId, event.timestamp);
            events.put(event.eventId, event);
            if (index>10) {
                eventsToRemove.add(event);
            }
        }

        EventHandler eventHandler = new EventHandler();
        eventHandler.arrivalTime = arrivalTime;
        eventHandler.events = events;
        ArrayList<Event> results = eventHandler.findOldEvents(timeAgo.minusMinutes(10));

        assertThat(results.size(), equalTo(eventsToRemove.size()));
    }

    @Test
    void testTrashOldEvents() {
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        HashMap<String, Long> arrivalTime = new HashMap<>();
        HashMap<String, Event> events = new HashMap<>();
        HashMap<String, Event> resultHashMap = new HashMap<>();
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
