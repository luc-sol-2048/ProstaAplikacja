package pl.lukasz.prostySewer;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Service {

    public static final String MESAGES_JSONS = "mesages.jsons";
    private Map<String, Topic> topicMap;
    //private BoarMessageWriter writer = new BoarMessageWriter(MESAGES_JSONS); // inicjalizowanie pliku;
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

        //this.topicMap=new BoardMessageReader().readAllTopics(MESAGES_JSONS);
    }

    private void ensureTopic(String java) {
        this.topicMap = topicMap.computeIfAbsent(java, key -> Topic.create(key))._2;
    }

    synchronized Option<Topic> getTopics(String topicName){
        return topicMap.get(topicName);
    }

     synchronized Option<Topic> addMessageToTopic(String topicName,Message message){
        Option<Topic> newTopic = getTopics(topicName).map(topic -> topic.addMesage(message));// wiadomość dopisana do topicu
        //nadpisywanie topicu do nowej mapy

         newTopic.forEach(topic -> repository.save(new BoardMessage(message,topicName))); //zapisywanie do repozytorium i bazy
         //newTopic.forEach(topic -> writer.write(topic.topicName,message)); // zapisanie do pliku;
        Option<Map<String, Topic>> newMap = newTopic.map(topic -> this.topicMap.put(topicName, topic));
       //dopisywanie topicu do istniejącej mapy;
        newMap.forEach(topics->this.topicMap=topics);
        return newTopic;
    }
}
