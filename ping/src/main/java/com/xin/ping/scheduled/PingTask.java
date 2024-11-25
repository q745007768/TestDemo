package com.xin.ping.scheduled;

import com.xin.ping.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class PingTask {
    @Autowired
    private PingService pingService;

    @Scheduled(fixedRateString = "${fixedRate}")
    public void pingTask() {
        pingService.pingPongService();
    }
}
