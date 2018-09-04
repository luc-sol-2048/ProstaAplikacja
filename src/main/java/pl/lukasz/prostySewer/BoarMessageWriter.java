package pl.lukasz.prostySewer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BoarMessageWriter {

    private final ObjectMapper objectMapper= new ObjectMapper(); // mapuje obiekty Java na json
    private final JsonFactory jsonFactory = new JsonFactory();//obiekt potrzebny do generowania json

    private final OutputStream outputStream; // strumień zapisu
    private final JsonGenerator jsonGenerator;// generator json

    BoarMessageWriter(String file){
        try {
                this.outputStream = Files.newOutputStream(Paths.get(file), StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE); // tworzenie strumienia do zapisu danych to wskazanego pliku lub stworzenie nowego pliku
                this.jsonGenerator = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8);//stworzenie generatora z kodowaniem UTF-8
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void write(String topic, Message message){

        final BoardMessage toWrite = new BoardMessage(message,topic);

        try {
            objectMapper.writeValue(jsonGenerator,toWrite);
            this.outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
