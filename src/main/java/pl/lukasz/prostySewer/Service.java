package pl.lukasz.prostySewer;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public class Service {

    private Map<String, Topic> topicMap;
    private BoarMessageWriter writer = new BoarMessageWriter("mesages.jsons"); // inicjalizowanie pliku;


    Service(){
        this.topicMap=List.of("java", "vavr.io","spring")//lista z nazwami topiców
            .map(name->Topic.create(name))//tworzenie topic
            .toMap(topic -> topic.topicName,topic -> topic);//dopisywanie do mapy nazyw Topicu i samego Topicu!!!
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
