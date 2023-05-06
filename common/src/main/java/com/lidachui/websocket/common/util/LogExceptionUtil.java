package com.lidachui.websocket.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;

/**
 * LogExceptionUtil
 *
 * @Author lihuijie
 * @Description: 异常message相关问题
 * @SINCE 2022/9/7 18:45
 */
public class LogExceptionUtil {


    private static final StringWriter SW = new StringWriter();
    private static final PrintWriter PW = new PrintWriter(SW);

    public static String getExceptionMessage(Throwable ex) {
        try (StringWriter sw = SW; PrintWriter pw = PW) {
            sw.getBuffer().setLength(0);
            ex.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
