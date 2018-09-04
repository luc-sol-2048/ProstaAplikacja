package pl.lukasz.prostySewer;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public class Service {

    public static final String MESAGES_JSONS = "mesages.jsons";
    private Map<String, Topic> topicMap;
    private BoarMessageWriter writer = new BoarMessageWriter(MESAGES_JSONS); // inicjalizowanie pliku;


    Service(){
        this.topicMap=new BoardMessageReader().readAllTopics(MESAGES_JSONS);
    }

   synchronized Option<Topic> getTopics(String topicName){
        return topicMap.get(topicName);
    }

     synchronized Option<Topic> addMessageToTopic(String topicName,Message message){
        Option<Topic> newTopic = getTopics(topicName).map(topic -> topic.addMesage(message));// wiadomość dopisana do topicu
        //nadpisywanie topicu do nowej mapy
         newTopic.forEach(topic -> writer.write(topic.topicName,message)); // zapisanie do pliku;
        Option<Map<String, Topic>> newMap = newTopic.map(topic -> this.topicMap.put(topicName, topic));
       //dopisywanie topicu do istniejącej mapy;
        newMap.forEach(topics->this.topicMap=topics);
        return newTopic;
    }
}
