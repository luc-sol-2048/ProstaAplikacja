package pl.lukasz.prostySewer;


import io.vavr.control.Option;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;


public class ProstySewerApplication {
    private Service service = new Service();

    public static void main(String[] args) {
        new ProstySewerApplication().serve();

    }

    private void serve() {

        RouterFunction route = nest(path("/api"),
                route(GET("/time"), getTime())
                .andRoute(GET("/messages/{topic}"), renderMessages()) // użytkowink musi wprowadzić topic aby pobrać wiadomości
                .andRoute(POST("/messages/{topic}"),postMessage()));//  użytkowink musi wprowadzić topic aby dodać wiadomość
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        HttpServer server = HttpServer.create("localhost", 8080);
        server.startAndAwait(new ReactorHttpHandlerAdapter(httpHandler));

    }

    private HandlerFunction<ServerResponse> postMessage() {

        return request -> {

            Mono<Message> postedMessage = request.bodyToMono(Message.class);
            return  postedMessage.flatMap(message -> {
                final String topicName=request.pathVariable("topic");
               final Option<Topic> topicOption = service.addMessageToTopic(topicName, message);
                return messageOrErrorFromTopic(topicOption);
            });
        };
    }


    private HandlerFunction<ServerResponse> renderMessages() {
        return request -> {
           final String topicName = request.pathVariable("topic"); // pobranie nazwy topicu od uzytkownika
            Option<Topic> topicOpion = service.getTopics(topicName); // sprawdzenie czy mamy topic w naszej mapie
             return messageOrErrorFromTopic(topicOpion);
        };
    }


    private Mono<ServerResponse> messageOrErrorFromTopic(Option<Topic> topicOpion) {
        return topicOpion.map(topic -> ServerResponse.ok() // jeżeli mamy to zwracamy listę wiadomości dla topicu
                   .contentType(MediaType.APPLICATION_JSON)
                   .body(fromObject(topic.messages.toJavaList())) )
                   .getOrElse(()->ServerResponse.notFound().build());// jeżeli nie mamy to zwracamy błąd 404
    }

    private HandlerFunction<ServerResponse> getTime() {
        return request -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(fromObject(myFormatter.format(now)));
        };
    }

}

