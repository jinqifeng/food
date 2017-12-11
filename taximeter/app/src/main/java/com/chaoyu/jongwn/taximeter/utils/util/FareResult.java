package com.chaoyu.jongwn.taximeter.utils.util;

/**
 * Created by JongWN on 12/10/2017.
 */

public class FareResult {
    public String vTitle;
    public String vTax;
    public String tvPeriod;
    public String tvDistance;
    public String tvStart;
    public String tvEnd;
    protected static final String NAME_PREFIX = "Name_";
    protected static final String SURNAME_PREFIX = "Surname_";
    protected static final String EMAIL_PREFIX = "email_";

    public void setValue(String title,String tax,String period,String distance,String start,String end){
        vTitle = title;
        vTax = tax;
        tvPeriod = period;
        tvDistance = distance;
        tvEnd = end;
        tvStart = start;
    }
}
