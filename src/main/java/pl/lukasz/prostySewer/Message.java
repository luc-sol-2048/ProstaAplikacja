package pl.lukasz.prostySewer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {

    public final String content;
    public final String author;
    @JsonProperty("time")
    public final String time;
    @JsonCreator
    public Message(@JsonProperty("content") String content,@JsonProperty("author") String author) {
        this.content = content;
        this.author = author;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") ;
        LocalDateTime local = LocalDateTime.now();
        this.time = dateTimeFormatter.format(local);
    }
}
