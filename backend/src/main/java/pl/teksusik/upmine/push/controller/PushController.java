package pl.teksusik.upmine.push.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.push.PushMonitor;
import pl.teksusik.upmine.push.service.PushService;

import java.util.Optional;

public class PushController {
    private final PushService pushService;

    public PushController(PushService pushService) {
        this.pushService = pushService;
    }

    public void acceptPush(Context context) {
        String secret = context.pathParam("secret");
        if (secret.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<PushMonitor> pushMonitor = this.pushService.findByPushSecret(secret);
        if (pushMonitor.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        Heartbeat heartbeat = this.pushService.addHeartbeat(pushMonitor.get());
        context.status(HttpStatus.OK)
                .json(heartbeat);
    }
}
