package pl.lukasz.prostySewer;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    public void getTopicWithCorrectTopicName(){
        String test ="vavr.io";
        Service service = new Service();

        String topicName = service.getTopics(test)
                .map(topic -> topic.topicName)
                .getOrElse("brak");


        assertEquals(test,topicName);
    }

    @Test
    public void  getErrorWithIncorrectTopicName(){
        String test ="NowyTopic";
        Service service = new Service();

        String topicName = service.getTopics(test)
                .map(topic -> topic.topicName)
                .getOrElse("");


        assertNotEquals(test,topicName);
    }

    @Test
    public void addMessageToIncorrectTopic(){
        Service service = new Service();
        Message newMsg=new Message("Witaj na forum","Krzysztof");

        assertTrue( service.addMessageToTopic("nowy Topic",newMsg).isEmpty());

    }


    @Test
    public void addMessageToCorrectTopic(){
        Service service = new Service();
        Message newMsg=new Message("Witaj na forum","Krzysztof");

        assertFalse( service.addMessageToTopic("java",newMsg).isEmpty());

    }



}