package com.vuga.paybus;

import java.net.InetAddress;

/**
 * Created by DOUGLAS on 10/08/2016.
 */
public class util {

  //  public static String Url = "http://192.168.1.196/paybus/index.php/";
  //  public  static  String FileUrl = "http://192.168.1.196/paybus/";

    public static String Url = "http://paybus.ugabus.com/index.php/";
    public static String FileUrl = "http://paybus.ugabus.com/";

    public static final String PREFS_NAME = "MyPrefsFile";
    public static String USER_ROLE;
    public static String USER_ID;
    public static String COMPANY_ID;
    public static String COMPANY;
    public static String COMPANY_LOGO;
    public static String USER_NAME;
    public static String USER_IMAGE;
    public static String SESSION_ID;
    public static String SESSION_ROUTE;
    public static String SESSION_ROUTE_ID;
    public static String SESSION_BUS;
    public static String SESSION_COST;
    public static String SESSION_TIME;
    public static String MAC_ADDR;
    public static String BUS;
    public static String ROUTE;
    public static String MAX_SEATS;

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
