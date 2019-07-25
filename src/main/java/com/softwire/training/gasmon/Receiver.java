package com.softwire.training.gasmon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Receives messages and handles them */
public class Receiver {

    public static void jsonIntoJava(String message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MessageId messageId = gson.fromJson(message, MessageId.class);
        System.out.println(messageId.toString());
        Event event = gson.fromJson(messageId.toString(), Event.class);
        System.out.println(messageId.Message);
        System.out.println(event.eventId);
    }
}
