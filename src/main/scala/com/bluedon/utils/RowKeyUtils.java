package main.scala.com.bluedon.utils;

import java.util.Date;

/**
 * Created by huoguang on 2017/6/2.
 */
public class RowKeyUtils {

    public static String genaralROW(){
        String row = (int)((Math.random()*9+1)*100000) + "";	//六位随机数
        row += new Date().getTime()/1000;
        return row;
    }
}
