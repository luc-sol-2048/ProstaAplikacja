package pl.lukasz.prostySewer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.HashMap;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BoardMessageReader {


    private final ObjectMapper objectMapper= new ObjectMapper(); // mapuje obiekty Java na json
    private final JsonFactory jsonFactory = new JsonFactory();//obiekt potrzebny do generowania json

    Map<String,Topic> readAllTopics(String path){

        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path)); // strumien odczytu
            JsonParser parser = jsonFactory.createParser(reader);//parsowanie jsona do objektu w java

           final Iterable<BoardMessage> iterable = () ->  // tworzenie iteratora który przechodzi po pliku
           {
               return new Iterator<BoardMessage>() {
                   Try<BoardMessage> nextMessage = Try.failure(new IllegalStateException()); // gdy jesteśmy na końcu pliku lub jest pusty

                   @Override
                   public boolean hasNext() {
                       try {
                           nextMessage = Try.success(objectMapper.readValue(parser, BoardMessage.class)); // próba odczytania z pliku
                       } catch (IOException e) {
                           nextMessage = Try.failure(e); //gdy coś nie jest ok zwrócenie błędu
                       }

                       return nextMessage.isSuccess();//jezeli udało się wyciągnąć wiadomość to iteruj dalej
                   }

                   @Override
                   public BoardMessage next() {
                       return nextMessage.get();//pobranie wiadomości, gdy nic nie będzie to odrazu wyskoczy błąd
                   }
               };
           };


            return List.ofAll(iterable)
                    .foldLeft(HashMap.<String,Topic>empty(), //bierzemy elementy z listy i dorzucamy do pustej  mapy(tutaj deklarujemy pusta mapę)
                            (existingMap,newElement)->   //do istniejącej mapy dokładamy nowy element
                                existingMap.put(newElement.topic, existingMap.get(newElement.topic) // dokładamy nazwę topicu i topic
                                                .getOrElse(Topic.create(newElement.topic)) // lub tworzymy nowy topic
                                                .addMesage(newElement.message))); // na koniec dokładamy wiadomość do topicu
        } catch (IOException e) {
            return HashMap.<String,Topic>empty();// w przypadku błędu zwracamy pustą mapę
        }


    }
}
