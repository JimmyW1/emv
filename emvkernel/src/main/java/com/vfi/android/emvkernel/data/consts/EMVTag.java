package com.vfi.android.emvkernel.data.consts;

public class EMVTag {
    // select cmd response tags
    public final static String tag6F = "6F"; // FCI Template (M)
    public final static String tag84 = "84"; // DF Name (M)
    public final static String tagA5 = "A5"; // FCI Proprietary Template (M)
    public final static String tag88 = "88"; // SFI of the Directory Elementary File (M)
    public final static String tag5F2D = "5F2D"; // Language Preference (O)
    public final static String tag9F11 = "9F11"; // Issuer Code Table Index (O)
    public final static String tagBF0C = "BF0C"; // FCI Issuer Discretionary Data (O)
    public final static String tag50 = "50"; // Application Label (O)
    public final static String tag87 = "87"; // Application Priority Indicator (O)
    public final static String tag9F38 = "9F38"; // PDOL (O)
    public final static String tag9F12 = "9F12"; // Application Preferred Name (O)
    public final static String tag9F4D = "9F4D"; // Log Entry (O)
    //read record
    public final static String tag70 = "70"; // Payment System Directory Record Format (M)
    public final static String tag61 = "61"; // an Application Template  (M) // subTag of Tag70
    public final static String tag4F = "4F"; // ADF Name  (M)  // subTag of Tag61
//    public final static String tag50 = "50"; // Application Label  (M)  // subTag of Tag61
//    public final static String tag9F12 = "9F12"; // Application Preferred Name (O)  // subTag of Tag61
//    public final static String tag87 = "87"; // Application Priority Indicator (O)  // subTag of Tag61
    public final static String tag73 = "73"; // Directory Discretionary Template (O)  // subTag of Tag61

}
