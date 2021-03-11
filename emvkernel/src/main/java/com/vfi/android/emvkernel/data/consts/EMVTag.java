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
//    public final static String tag50 = "50"; // Application Label (O)
//    public final static String tag87 = "87"; // Application Priority Indicator (O)
    public final static String tag9F38 = "9F38"; // PDOL (O)
//    public final static String tag9F12 = "9F12"; // Application Preferred Name (O)
    public final static String tag9F4D = "9F4D"; // Log Entry (O)
    //read record
    public final static String tag70 = "70"; // Payment System Directory Record Format (M)
    // =====subTags of tag70
    public final static String tag61 = "61"; // an Application Template  (M) // subTag of Tag70
//    public final static String tag4F = "4F"; // ADF Name  (M)  // subTag of Tag61
//    public final static String tag50 = "50"; // Application Label  (M)  // subTag of Tag61
//    public final static String tag9F12 = "9F12"; // Application Preferred Name (O)  // subTag of Tag61
//    public final static String tag87 = "87"; // Application Priority Indicator (O)  // subTag of Tag61
    public final static String tag73 = "73"; // Directory Discretionary Template (O)  // subTag of Tag61

    /**
     * Some APDU response data format
     * tag 77 - several BERTLV coded objects
     * tag 80 - The value field consists of the concatenation without delimiters (tag and length) of the value
     */
    public final static String tag77 = "77"; // The data object returned in the response message is a constructed data object with tag equal to '77'. The value field may contain several BERTLV coded objects
    public final static String tag80 = "80"; // The data object returned in the response message is a primitive data object with tag equal to '80'. The value field consists of the concatenation without delimiters (tag and length) of the value

    /**
     * Below is ICC Tags which define in EMV book3 Table 33
     */
    public final static String tag9F26 = "9F26"; // Application Cryptogram b length 8 Template 77 or 80 - Cryptogram returned by the ICC in response of the GENERATE AC command public final static String tag9F26 = "9F26"; // Application Cryptogram b 8 Template 77 or 80 - Cryptogram returned by the ICC in response of the GENERATE AC command
    public final static String tag9F42 = "9F42"; // Application Currency Code b 3 length 2 Template 70 or 77 - Indicate the currency in which the account is managed according to ISO 4217
    public final static String tag9F05 = "9F05"; // Application Discretionary Data b length 1-32  Template 70 or 77 - Issuer or payment system specified data relating to the application
    public final static String tag5F25 = "5F25"; // Application Effective Date n 6 length 3 Template 70 or 77 - Date from which the application may be used
    public final static String tag5F24 = "5F24"; // Application Expiration Date n 6 length 3 Template 70 or 77 - Date from which the application may be used
    public final static String tag94 = "94"; // Application File Locator(AFL) var. length Var up to 252 Template 77 or 80 - Indicates the location (SFI, range of records) of the AEFs related to a given application
    public final static String tag4F = "4F"; // Application Dedicated File(ADF) Name b length 5-16. Template 61 - Identifies the application as described in ISO/IEC 7816-5
    public final static String tag82 = "82"; // Application Interchange Profile b length 2. Template 77 or 80 - Indicates the capabilities of the card to support specific functions in the application
    public final static String tag50 = "50"; // Application Label ans with the special character limited to space length 1-16. Template 61 or A5 - Mnemonic associated with the AID according to ISO/IEC 7816-5
    public final static String tag9F12 = "9F12"; // Application Preferred Name ans length 1-16 Template 61 or A5  - Preferred mnemonic associated with the AID
    public final static String tag5A = "5A"; // Application Primary Account Number(PAN) cn var up to 19 length var. up to 10. Template 70 or 77 - Valid cardholder account number
    public final static String tag5F34 = "5F34"; // Application Primary Account Number(PAN) Sequence Number, n 2 length 1 . Template 70 or 77 - Identifies and differentiates cards with the same PAN
    public final static String tag87 = "87"; // Application Priority Indicator b length 1 . Template 61 or A5 - Indicates the priority of a given application or group of applications in a directory
    public final static String tag9F3B = "9F3B"; // Application Reference Currency n 3 length 2-8 . Template 70 or 77 - 1-4 currency codes used between the terminal and the ICC when the Transaction Currency Code is different from the Application Currency code; each code is 3 digits according to ISO 4217
    public final static String tag9F43 = "9F43"; // Application Reference Currency Exponent n 1 length 1-4 . Template 70 or 77 - Indicates the implied position of the decimal point from the right of the amount, for each of the 1-4 reference currencies represented according to ISO 4217

    public final static String tag8C = "8C"; //
    public final static String tag8D = "8D"; //

    public final static String tag9F1F = "9F1F"; // Track 1 Discretionary Data, Discretionary part of track 1 according to ISO/IEC 7813, ans, Template 70 or 77 , var
    public final static String tag9F20 = "9F20"; // Track 2 Discretionary Data, Discretionary part of track 2 according to ISO/IEC 7813, ans, Template 70 or 77 , var
    /**
     * TAG 57 - Track 2 Equivalent Data
     * Contains the data elements of track 2
     * according to ISO/IEC 7813, excluding start
     * sentinel, end sentinel, and Longitudinal
     * Redundancy Check (LRC), as follows:
     * ICC b '70' or '77' '57' var. up
     * to 19
     * Primary Account Number n, var. up
     * to 19
     * Field Separator (Hex 'D') b
     * Expiration Date (YYMM) n 4
     * Service Code n 3
     * Discretionary Data (defined by individual
     * payment systems)
     * n, var.
     * Pad with one Hex 'F' if needed to ensure
     * whole bytes
     */
    public final static String tag57 = "57"; // format b, n var up to 19, Template 70 or 77



}
