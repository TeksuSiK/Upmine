package pl.teksusik.upmine.web;

import com.fasterxml.jackson.core.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class JsonParseExceptionHandler {
    public void handle(JsonParseException exception, Context context) {
        context.status(HttpStatus.BAD_REQUEST)
                .json("Invalid request body");
    }
}
