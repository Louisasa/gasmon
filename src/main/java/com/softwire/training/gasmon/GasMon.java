package com.softwire.training.gasmon;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class GasMon {

    private static final Logger LOG = LoggerFactory.getLogger(GasMon.class);
    private static final ClasspathPropertiesFileCredentialsProvider CREDENTIALS_PROVIDER = new ClasspathPropertiesFileCredentialsProvider();
    private static String bucket_name = "samcap-07-2019-gasmon-locationss3bucket-14b07twikku90";
    private static String key_name = "locations.json";
    private static String key_name_2 = "locations-part2.json";
    private static String arn = "arn:aws:sns:eu-west-1:552908040772:samcap-07-2019-gasmon-snsTopicSensorDataPart1-15WY99JMOTFSY";
    private static String arn_2 = "arn:aws:sns:eu-west-1:552908040772:samcap-07-2019-gasmon-snsTopicSensorDataPart2-RH904EAMCDRY";

    private static void objContentToJava(AmazonS3 s3) {
        S3Object o = s3.getObject(bucket_name, key_name_2);
        S3ObjectInputStream s3is = o.getObjectContent();
        try {
            String result = IOUtils.toString(s3is, "UTF-8");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type collectionType = new TypeToken<ArrayList<ObjectContent>>() {
            }.getType();
            ArrayList<ObjectContent> objectContent = gson.fromJson(result, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void discardOldEvents(EventHandler eventHandler, Long currentTime) {
        ArrayList<Event> eventsToRemove = eventHandler.findOldEvents(currentTime - 600000);
        eventHandler.trashOldEvents(eventsToRemove);
    }

    private static void averageEvents(EventHandler eventHandler, Long currentTime) {
        ArrayList<Event> eventsFiveMinsAgo = eventHandler.findOldEvents(currentTime - 300000);
        ArrayList<Event> eventsSixMinsAgo = eventHandler.findOldEvents(currentTime - 360000);
        double averagedEvents = eventHandler.averageEvents(eventsSixMinsAgo, eventsFiveMinsAgo);
        eventHandler.writeToFile("Average number of events at time" + (currentTime - 300000) + ": " + averagedEvents);
    }

    private static void receiveEvents(AmazonSQS sqs, String url) {
        DateTime dateTime = new DateTime();
        DateTime dateTimeForAveragedEvents = new DateTime();
        DateTime dateTimeForTrashingOldEvents = new DateTime();
        Period period = new Period().withMinutes(12);

        EventHandler eventHandler = new EventHandler();

        ArrayList<Receiver> receivers = new ArrayList<>();

        for (int index = 0; index < 10; index++) {
            Receiver receiver = new Receiver(eventHandler, String.valueOf(index), url, sqs);
            receivers.add(receiver);
            receiver.start();
        }

        while (dateTime.plus(period).isAfterNow()) {


            if (new DateTime().isAfter(dateTimeForAveragedEvents.plusMinutes(5))) {
                dateTimeForAveragedEvents = dateTimeForAveragedEvents.plusMinutes(1);
                Long currentTime = System.currentTimeMillis();
                averageEvents(eventHandler, currentTime);
            }
            if (new DateTime().isAfter(dateTimeForTrashingOldEvents.plusMinutes(10))) {
                ArrayList<Event> eventsToAverage = eventHandler.findOldEvents(System.currentTimeMillis());
                eventHandler.addToAverageLocations(eventsToAverage);
                dateTimeForTrashingOldEvents = dateTimeForTrashingOldEvents.plusMinutes(1);
                discardOldEvents(eventHandler, System.currentTimeMillis());
            }
        }

        for (Receiver receiver : receivers) {
            receiver.killThread();
        }

        System.out.println("Done big loop");

        Long currentTime = System.currentTimeMillis();

        while(dateTimeForAveragedEvents.isBeforeNow()) {
            dateTimeForAveragedEvents = dateTimeForAveragedEvents.plusMinutes(1);
            averageEvents(eventHandler, currentTime);

            ArrayList<Event> eventsToAverage = eventHandler.findOldEvents(currentTime);
            eventHandler.addToAverageLocations(eventsToAverage);
            discardOldEvents(eventHandler, currentTime);

            currentTime += 60000;
        }

        eventHandler.averageLocation();
    }

    public static void main(String[] args) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(CREDENTIALS_PROVIDER)
                .withRegion("eu-west-1")
                .build();

        final AmazonSNS sns = AmazonSNSClientBuilder.standard()
                .withCredentials(CREDENTIALS_PROVIDER)
                .withRegion("eu-west-1")
                .build();
        final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(CREDENTIALS_PROVIDER)
                .withRegion("eu-west-1")
                .build();

        //sqs.setEndpoint("sqs.eu-west-1.amazonaws.com");

        objContentToJava(s3);

        final String url = sqs.createQueue(new CreateQueueRequest("louQueue")).getQueueUrl();
        Topics.subscribeQueue(sns, sqs, arn_2, url);

        receiveEvents(sqs, url);

        System.out.println("cool fin");

        sqs.deleteQueue(new DeleteQueueRequest(url));



    }

}
