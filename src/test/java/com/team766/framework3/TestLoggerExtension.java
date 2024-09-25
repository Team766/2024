package com.team766.framework3;

import com.team766.logging.Category;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestLoggerExtension
        implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, LoggingBase {

    @Override
    public Category getLoggerCategory() {
        return Category.FRAMEWORK;
    }

    @Override
    public void afterEach(ExtensionContext context) {
        log("Finished test: " + context.getDisplayName());
    }

    @Override
    public void beforeAll(ExtensionContext context) {

        log("Starting test suite");
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        log("Starting test: " + context.getDisplayName());
    }
}
