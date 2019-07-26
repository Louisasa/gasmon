package com.softwire.training.gasmon;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GasMon {

    private static final Logger LOG = LoggerFactory.getLogger(GasMon.class);
    private static final ClasspathPropertiesFileCredentialsProvider CREDENTIALS_PROVIDER = new ClasspathPropertiesFileCredentialsProvider();
    private static String bucket_name = "samcap-07-2019-gasmon-locationss3bucket-14b07twikku90";
    private static String key_name = "locations.json";
    private static String arn = "arn:aws:sns:eu-west-1:552908040772:samcap-07-2019-gasmon-snsTopicSensorDataPart1-15WY99JMOTFSY";

    private static void objContentToJava(AmazonS3 s3) {
        S3Object o = s3.getObject(bucket_name, key_name);
        S3ObjectInputStream s3is = o.getObjectContent();
        try {
            String result = IOUtils.toString(s3is, "UTF-8");
            //todo: problem is it's an array of json and it just wants the jsons
            // todo: can I turn from str to array or am I gonna have to split?
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //ObjectContent objectContent = gson.fromJson(result.substring(1,result.length()-1), ObjectContent.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void receiveEvents(AmazonSQS sqs, String url) {
        DateTime dateTime = new DateTime();
        Period period = new Period().withMinutes(10);

        EventHandler eventHandler = new EventHandler();

        while (dateTime.plus(period).isAfterNow()) {
            List<Message> messages = sqs.receiveMessage(new ReceiveMessageRequest(url)).getMessages();
            if (messages.size() > 0) {
                String messageResult = eventHandler.checkForEnglish(messages.get(0).getBody());
                if (messageResult != null) {
                    MessageId messageId = eventHandler.jsonIntoMessageId(messageResult);
                    Event event = eventHandler.jsonIntoEvent(messageId.toString());
                    eventHandler.checkForDuplicates(event);
                }

            }
            if (new DateTime().isAfter(dateTime.plusMinutes(10))) {
                DateTime tenMinsAgo = new DateTime().minusMinutes(10);
                ArrayList<Event> eventsToRemove = eventHandler.findOldEvents(tenMinsAgo);
                eventHandler.trashOldEvents(eventsToRemove);
            }

            if (new DateTime().isAfter(dateTime.plusMinutes(5))) {
                DateTime timeRN = new DateTime();
                ArrayList<Event> eventsFiveMinsAgo = eventHandler.findOldEvents(timeRN.minusMinutes(5));
                ArrayList<Event> eventsSixMinsAgo = eventHandler.findOldEvents(timeRN.minusMinutes(6));
                int averagedEvents = eventHandler.averageEvents(eventsSixMinsAgo, eventsFiveMinsAgo);
                eventHandler.writeToFile("Average number of events at time" + timeRN.minusMinutes(5) + ": " + averagedEvents);
            }
        }
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

        objContentToJava(s3);

        String url = sqs.createQueue(new CreateQueueRequest("louQueue3")).getQueueUrl();
        Topics.subscribeQueue(sns, sqs, arn, url);

        receiveEvents(sqs, url);

        System.out.println("cool fin");

        sqs.deleteQueue(new DeleteQueueRequest(url));



    }

}
