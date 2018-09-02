package pl.lukasz.prostySewer;

import io.vavr.collection.List;

public class Topic {

    public final String topicName;
    public final List<Message> messages;

    public Topic(String topicName, List<Message> messages) {
        this.topicName = topicName;
        this.messages = messages;
    }

    public static Topic create(String testowy) {
        return new Topic(testowy, List.empty());
    }


    public Topic addMesage(Message message) {
        return new Topic(this.topicName,this.messages.append(message));
        }
}
