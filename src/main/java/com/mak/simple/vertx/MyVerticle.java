package com.mak.simple.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.thymeleaf.TemplateEngine;

public class MyVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        //powolanie do zycia nowej instacji vertexa
        Vertx vertx = Vertx.vertx();
        //wypuszczenie nowego verticle'a do zycia, tym samym uruchomienie aplikacji
        vertx.deployVerticle(new MyVerticle());
    }

    //hash mapa, bo wazna jest kolejnosc wrzucanych meetingow
    private Map<Integer, Meeting> meetings = new LinkedHashMap<>();

    final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();

    //dzialanie na request meetings
    private void getMeetings(RoutingContext routingContext) {

        if (meetings.isEmpty()) {
            //204 no content
            routingContext.response().setStatusCode(204).end();

        } else {

            routingContext.put("meetings", meetings);

            engine.render(routingContext, "meetings.html", res -> {
                if (res.succeeded()) {
                    routingContext.response().end(res.result());
                } else {
                    routingContext.fail(res.cause());
                }
            });
        }
        
        
        /**
         * 
         * Wersja stricte REST API
         * 
         * 
         * if (meetings.isEmpty()) {
            //204 no content
            routingContext.response().setStatusCode(204).end();
            
        } else {
            
            routingContext.response()
                    .putHeader("content-type", "applicaiton/json; charset=utf-8")
                    .end(Json.encodePrettily(meetings.values()));
        }
         */
    }
    //dzialanie na request add

    private void addMeeting(RoutingContext routingContext) {

        
        /*
        Wersja stricte REST API
        
        final Meeting meeting = Json.decodeValue(routingContext.getBodyAsString(), Meeting.class);
        
        */
        
        //wyci¹gniêcie z formularza danych
        Meeting meeting = new Meeting(routingContext.request().getFormAttribute("description"),
                routingContext.request().getFormAttribute("date"));

        meetings.put(meeting.getId(), meeting);

        //201 created
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(meeting));
    }

    //dzialanie na request delete
    private void deleteMeeting(RoutingContext routingContext) {

        String id = routingContext.request().getParam("id");

        if (id == null) {

            routingContext.response().setStatusCode(400).end();

        } else {

            Integer idToRemove = Integer.valueOf(id);
            meetings.remove(idToRemove);
            routingContext.response().setStatusCode(204).end();

        }
    }

    @Override
    public void start() {

        Router router = Router.router(vertx);

        //zezwolenie wszystkich requestom na dostep do body  
        router.route().handler(BodyHandler.create());

        //przekierowanie wywo³ania get /meetings do metody 
        router.get("/meetings").handler(this::getMeetings);

        //przekierowanie wywolania post /add do metody
        router.post("/add").handler(this::addMeeting);

        //przekierowanie wywowalani delete i sprecyzowanie po jakim id ma byc usuniete w metodzie 
        router.delete("/delete/:id").handler(this::deleteMeeting);

        router.route().handler(StaticHandler.create("static"));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);

    }

}
