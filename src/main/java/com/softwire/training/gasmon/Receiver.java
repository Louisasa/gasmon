package com.softwire.training.gasmon;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

public class Receiver extends Thread {
    private Thread t;
    private EventHandler eventHandler;
    private String threadName;
    private AmazonSQS sqs;
    private String url;
    private boolean continueToRun;

    Receiver(EventHandler eventHandler, String threadName, String url, AmazonSQS sqs) {
        this.eventHandler = eventHandler;
        this.threadName = threadName;
        this.url = url;
        this.sqs = sqs;
        this.continueToRun = true;
    }

    private static void formatEvents(EventHandler eventHandler, String message) {
        String messageResult = eventHandler.checkForEnglish(message, "eventId");
        if (messageResult != null) {
            MessageId messageId = eventHandler.jsonIntoMessageId(messageResult);
            String eventResult = eventHandler.checkForEnglish(message, "location");
            if (eventResult != null) {
                try {
                    Event event = eventHandler.jsonIntoEvent(messageId.toString());
                    if (event != null) {
                        eventHandler.checkForDuplicates(event);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void killThread() {
        continueToRun = false;
    }

    public void start() {
        System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    public void run() {
        while (continueToRun) {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(url).withWaitTimeSeconds(1);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            if (messages.size() > 0) {
                formatEvents(eventHandler, messages.get(0).getBody());
                final String messageReceiptHandle = messages.get(0).getReceiptHandle();
                sqs.deleteMessage(new DeleteMessageRequest(url,
                        messageReceiptHandle));

            }
        }
        System.out.println(threadName + " dead");
    }
}
