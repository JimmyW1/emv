package com.vfi.android.libtools.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static String getCurrentDateYYMM() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String YYMM = simpleDateFormat.format(date).substring(2, 6);

        return YYMM;
    }

    public static String formatDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// yyyyMMdd
        return simpleDateFormat.format(date);
    }

    public static String formatTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");// HH:mm:ss
        return simpleDateFormat.format(date);
    }
}
