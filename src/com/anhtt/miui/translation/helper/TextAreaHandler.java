package com.anhtt.miui.translation.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

class TextAreaHandler extends StreamHandler {
    private void configure() {
        setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return new SimpleDateFormat("\nyyyy-MM-dd HH:mm:ss: ").format(System.currentTimeMillis()) + record.getMessage();
            }
        });
        try {
            setEncoding("UTF-8");
        } catch (IOException ex) {
            try {
                setEncoding(null);
            } catch (IOException ex2) {
                // doing a setEncoding with null should always work.
                // assert false;
                ex2.printStackTrace();
            }
        }
    }

    public TextAreaHandler(OutputStream os) {
        super();
        configure();
        setOutputStream(os);
    }

    //@see java/util/logging/ConsoleHandler.java
    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public void close() {
        flush();
    }
}