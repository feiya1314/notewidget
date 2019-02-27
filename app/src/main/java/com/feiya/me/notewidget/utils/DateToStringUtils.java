package com.feiya.me.notewidget.utils;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by feiya on 2016/9/30.
 */

public class DateToStringUtils {
    public static String dateToString(Date date){
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static Date stringToDate(String strDate) throws Exception{
        Date date=new Date();
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try{
           date = dateFormat.parse(strDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }
}
