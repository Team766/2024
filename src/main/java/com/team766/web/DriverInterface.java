package com.team766.web;

import java.util.Map;

public class DriverInterface implements WebServer.Handler {

    AutonomousSelector autonomousSelector;

    public DriverInterface(final AutonomousSelector autonomousSelector_) {
        this.autonomousSelector = autonomousSelector_;
    }

    @Override
    public String endpoint() {
        return "/driver";
    }

    @Override
    public String title() {
        return "Driver Interface";
    }

    @Override
    public String handle(final Map<String, Object> params) {
        return Dashboard.makeDashboardPage()
                + LogViewer.makeAllErrorsPage()
                + autonomousSelector.handle(params);
    }
}
