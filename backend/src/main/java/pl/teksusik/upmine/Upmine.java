package pl.teksusik.upmine;

import pl.teksusik.upmine.monitor.service.MonitorService;

public class Upmine {
    private MonitorService monitorService;

    public void launch() {
        this.monitorService = new MonitorService();
    }
}
