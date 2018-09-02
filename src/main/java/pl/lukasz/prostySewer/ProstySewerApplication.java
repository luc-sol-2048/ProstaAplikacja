package pl.lukasz.prostySewer;


import io.vavr.collection.List;
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
    private List<Message> messages = List.empty(); // lista niemutowalna z vavr.io
    private ProstySewerApplication(){
        addMessage(new Message("witaj ","Zenon"));
        addMessage(new Message("sprawdzamy Zenka ","Marian"));
    }

    public static void main(String[] args) {
        new ProstySewerApplication().serve();

    }

    private void serve() {

        RouterFunction route = nest(path("/api"),
                route(GET("/time"), getTime())
                .andRoute(GET("/messages"),getMessages())
                .andRoute(POST("/messages"),postMessage()));
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        HttpServer server = HttpServer.create("localhost", 8080);
        server.startAndAwait(new ReactorHttpHandlerAdapter(httpHandler));

    }

    private HandlerFunction<ServerResponse> postMessage() {
        return request -> {
            Mono<Message> postedMessage = request.bodyToMono(Message.class);
            return  postedMessage.flatMap(message -> {
                addMessage(message);
                return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(messages.toJavaList()));//wyślietlenie z listy potrzeba to JavaList bo inaczej serwer
                                                                //sobie nie radzi
            });
        };
    }


    private HandlerFunction<ServerResponse> getMessages() {
        return request -> {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromObject(messages.toJavaList()));
        };
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

    private synchronized void addMessage(Message message) {
        messages=messages.append(message); // synchronizowanie dodawania wiadomości
    }

}

