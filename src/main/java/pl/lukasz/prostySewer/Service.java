package pl.lukasz.prostySewer;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Service {

    private Map<String, Topic> topicMap;
    private final Repository repository; //repozytorium

    Service(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringInit.class);//stworzenie kontekstu dla spring
        this.repository = context.getBean(Repository.class);//wyciągniety odpowiedni bean dla repository
        this.topicMap = List.ofAll(repository.findAll())
                        .foldLeft(HashMap.<String,Topic>empty(),
                                (existingMap,newElement)->
                                 existingMap.put(newElement.topic,existingMap.get(newElement.topic)
                                         .getOrElse(Topic.create(newElement.topic))
                                         .addMesage(newElement.message)));
        ensureTopic("java");
        ensureTopic("vavr.io");
        ensureTopic("ogolny");
    }

    private void ensureTopic(String java) {
        this.topicMap = topicMap.computeIfAbsent(java, key -> Topic.create(key))._2;
    }

    synchronized Option<Topic> getTopics(String topicName){
        return topicMap.get(topicName);
    }

      Option<Topic> addMessageToTopic(String topicName,Message message){
         Option<Topic> existingTopic = getTopics(topicName).map(topic -> topic.addMesage(message));// wiadomość dopisana do topicu
         existingTopic.forEach(topic -> repository.save(new BoardMessage(message,topicName))); //zapisywanie do repozytorium i bazy
         Option<Topic> newTopic = updateTopic(topicName, message, existingTopic);
        return newTopic;
    }

    private synchronized Option<Topic> updateTopic(String topicName, Message message, Option<Topic> existingTopic) {
        Option<Topic> newTopic = existingTopic.map(topic -> topic.addMesage(message));// wiadomość dopisana do topicu
        //nadpisywanie topicu do nowej mapy

        Option<Map<String, Topic>> newMap = newTopic.map(topic -> this.topicMap.put(topicName, topic));
        //dopisywanie topicu do istniejącej mapy;
        newMap.forEach(topics->this.topicMap=topics);
        return newTopic;
    }
}
