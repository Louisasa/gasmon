import com.softwire.training.gasmon.Event;
import com.softwire.training.gasmon.EventHandler;
import com.softwire.training.gasmon.MessageId;
import org.junit.jupiter.api.Test;

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
        String message = "{locationId: 'kjwerhwerhujerjkl', eventId: 'jfrkbwerwefjhb', value: 20.092834, timestamp: 987341}";
        EventHandler eventHandler = new EventHandler();
        Event event = eventHandler.jsonIntoEvent(message);
        HashMap<String, Event> eventHashMap = new HashMap<>();
        eventHashMap.put(event.eventId, event);

        eventHandler.checkForDuplicates(event);

        assertThat(eventHandler.events, equalTo(eventHashMap));
    }

    @Test
    void testCheckForDuplicates() {
        String message = "{locationId: 'kjwerhwerhujerjkl', eventId: 'jfrkbwerwefjhb', value: 20.092834, timestamp: 987341}";
        EventHandler eventHandler = new EventHandler();
        Event event = eventHandler.jsonIntoEvent(message);
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

//    @Test
//    public void testAverageEvents() {
////        eventsFiveMinsAgo.size()-eventsSixMinsAgo.size();
//    }
//
//    @Test
//    public void testWriteToFile() {
////        try(FileWriter fw = new FileWriter(averagedEventsFileName, true);
////            BufferedWriter bw = new BufferedWriter(fw);
////            PrintWriter out = new PrintWriter(bw))
////        {
////            out.println(lineToBeWrittenToFile);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//
//    @Test
//    public void testFindOldEvents() {
////        ArrayList<Event> eventsToRemove = new ArrayList<>();
////        for (Map.Entry<String, Long> stringDateTimeEntry : arrivalTime.entrySet()) {
////            Map.Entry pair = stringDateTimeEntry;
////            Long arrivalTime = (Long) pair.getValue();
////            if (timeAgo.isAfter(arrivalTime)) {
////                Event event = (Event) pair.getKey();
////                eventsToRemove.add(event);
////            }
////        }
//    }
//
//    @Test
//    public void testTrashOldEvents() {
//
////        for (Event event : eventsToRemove) {
////            events.remove(event);
////            arrivalTime.remove(event);
////        }
////    }
}
