package be.fnord.util.QUAL.Prefs;

/**
 * Created by edm92 on 25/03/2014.
 */
public class Ranges {

    public enum type{
        POS_REAL_NUM,
        POS_NAT_NUM,
        NEG_REAL_NUM,
        NEG_NAT_NUM,
        PROBABILITY,
        HIGH_MED_LOW,
        TIME,
        OTHER
    };
    
    public static int INFTY = 99999999;	// Approximation :P
    public static String LNGTIME = "P3Y";	// Long times
    public static String SHRTTIME = "PT0M";	// Long times

}
