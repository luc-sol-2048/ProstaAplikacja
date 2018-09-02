package pl.lukasz.prostySewer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @Test
    public void createdTopicHasNoMessage(){
        Topic topic = Topic.create("testowy");

        assertTrue(topic.messages.isEmpty());
    }

    @Test
    public void createdTopicHasSameTopicName(){
        Topic topic = Topic.create("testowy");

        assertEquals("testowy",topic.topicName);
    }

    @Test
    public void afterAddingMessageTopicHasOneMessage(){
        Topic created = Topic.create("Testowy");
        Topic newTopic=created.addMesage(new Message("moja", "ja"));

        assertEquals(1,newTopic.messages.length());
    }


    @Test
    public void afterAddingMessageTopicHasSameTopic(){
        Topic created = Topic.create("Testowy");
        Topic newTopic=created.addMesage(new Message("moja", "ja"));

        assertEquals("Testowy",newTopic.topicName);
    }

    @Test
    public void  afterAdding2MessageTopicHasTwoMessage(){
        Topic newTopic = Topic.create("testowy2")
                .addMesage(new Message("nowa wiadomość", "Janek"))
                .addMesage(new Message("kolejna wiadomość","Józek"));

        assertEquals(2,newTopic.messages.length());
    }

    @Test
    public void  addingMessageIsLast(){
        Topic newTopic = Topic.create("testowy2")
                .addMesage(new Message("nowa wiadomość", "Janek"))
                .addMesage(new Message("kolejna wiadomość","Józek"))
                .addMesage(new Message("Trzecia wiadomość","Kuba"));

        assertEquals("Trzecia wiadomość",newTopic.messages.last().content);
    }



}