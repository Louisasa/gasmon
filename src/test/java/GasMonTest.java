import org.junit.jupiter.api.Test;

public class GasMonTest {

    @Test
    void objContentToJava() {
//        S3Object o = s3.getObject(bucket_name, key_name);
//        S3ObjectInputStream s3is = o.getObjectContent();
//        try {
//            String result = IOUtils.toString(s3is, "UTF-8");
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //ObjectContent objectContent = gson.fromJson(result.substring(1,result.length()-1), ObjectContent.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Test
    void formatEvents() {
//        String messageResult = eventHandler.checkForEnglish(message);
//        if (messageResult != null) {
//            MessageId messageId = eventHandler.jsonIntoMessageId(messageResult);
//            Object obj = eventHandler.jsonIntoEvent(messageId.toString());
//            Event event = eventHandler.checkEventInput(obj);
//            if (event != null) {
//                eventHandler.checkForDuplicates(event);
//            }
//        }
    }

    @Test
    void discardOldEvents() {
//        DateTime tenMinsAgo = new DateTime().minusMinutes(10);
//        ArrayList<Event> eventsToRemove = eventHandler.findOldEvents(tenMinsAgo);
//        eventHandler.trashOldEvents(eventsToRemove);
    }

    @Test
    void averageEvents() {
//        ArrayList<Event> eventsFiveMinsAgo = eventHandler.findOldEvents(dateTime.minusMinutes(5));
//        ArrayList<Event> eventsSixMinsAgo = eventHandler.findOldEvents(dateTime.minusMinutes(6));
//        int averagedEvents = eventHandler.averageEvents(eventsSixMinsAgo, eventsFiveMinsAgo);
//        eventHandler.writeToFile("Average number of events at time" + dateTime.minusMinutes(5) + ": " + averagedEvents);
    }

    @Test
    void receiveEvents() {
//        DateTime dateTime = new DateTime();
//        Period period = new Period().withMinutes(5);
//
//        EventHandler eventHandler = new EventHandler();
//
//        while (dateTime.plus(period).isAfterNow()) {
//            List<Message> messages = sqs.receiveMessage(new ReceiveMessageRequest(url)).getMessages();
//            if (messages.size() > 0) {
//                formatEvents(eventHandler, messages.get(0).getBody());
//            }
//
//
//            if (new DateTime().isAfter(dateTime.plusMinutes(5))) {
//                dateTime = new DateTime();
//                discardOldEvents(eventHandler);
//
//                averageEvents(eventHandler, dateTime);
//            }
//        }
    }

    @Test
    void main() {

//        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                .withCredentials(CREDENTIALS_PROVIDER)
//                .withRegion("eu-west-1")
//                .build();
//
//        final AmazonSNS sns = AmazonSNSClientBuilder.standard()
//                .withCredentials(CREDENTIALS_PROVIDER)
//                .withRegion("eu-west-1")
//                .build();
//        final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
//                .withCredentials(CREDENTIALS_PROVIDER)
//                .withRegion("eu-west-1")
//                .build();
//
//        objContentToJava(s3);
//
//        String url = sqs.createQueue(new CreateQueueRequest("louQueue3")).getQueueUrl();
//        Topics.subscribeQueue(sns, sqs, arn, url);
//
//        receiveEvents(sqs, url);
//
//        System.out.println("cool fin");
//
//        sqs.deleteQueue(new DeleteQueueRequest(url));



    }

}
