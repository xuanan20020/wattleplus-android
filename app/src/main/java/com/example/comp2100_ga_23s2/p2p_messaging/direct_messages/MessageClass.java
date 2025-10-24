package com.example.comp2100_ga_23s2.p2p_messaging.direct_messages;


/**
 * @author Harry Randall u7499609
 * This is a fairly simple function that users the constructor
 * MessageClass to create a message. I then used the generator
 * for a 'getter and setter' to generate the below voids.
 * This is so we can store the message in an object and fetch items.
 */
public class MessageClass {
    String message;
    String messageType;
    String author;
    String time;
    public MessageClass(String message, String messageType, String author, String time) {
        this.message = message;
        this.messageType = messageType;
        this.author = author;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
