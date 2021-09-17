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
    /**
     * AIP Byte 1 (Leftmost)
     * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
     * 0 x x x x x x x RFU
     * x 1 x x x x x x SDA supported
     * x x 1 x x x x x DDA supported
     * x x x 1 x x x x Cardholder verification is supported
     * x x x x 1 x x x Terminal risk management is to be performed
     * x x x x x 1 x x Issuer authentication is supported 19
     * x x x x x x 0 x RFU
     * x x x x x x x 1 CDA supported
     *
     * AIP Byte 2 (Rightmost)
     * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
     * 0 x x x x x x x Reserved for use by the EMV
     * Contactless Specifications
     * x 0 x x x x x x RFU
     * x x 0 x x x x x RFU
     * x x x 0 x x x x RFU
     * x x x x 0 x x x RFU
     * x x x x x 0 x x RFU
     * x x x x x x 0 x RFU
     * x x x x x x x 0 RFU
     *
     */
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
    /**
     * Service Code Service code as defined in ISO/IEC 7813 for
     * track 1 and track 2
     * ICC n 3 '70' or '77' '5F30' 2
     */
    public final static String tag5F30 = "5F30";
    /**
     * Certification
     * Authority Public
     * Key Index
     * Identifies the certification authority’s public
     * key in conjunction with the RID
     * ICC b '70' or '77' '8F' 1
     */
    public final static String tag8F = "8F";
    /**
     * Issuer Public Key Certificate Issuer public key certified by a certification authority
     * ICC b '70' or '77' '90' NCA
     */
    public final static String tag90 = "90";
    /**
     * Signed Static Application Data Digital signature on critical application parameters for SDA
     * ICC b '70' or '77' '93' NI
     */
    public final static String tag93 = "93";
    /**
     * Issuer Public Key Remainder Remaining digits of the Issuer Public Key Modulus
     * ICC b '70' or '77' '92' NI −  NCA + 36
     */
    public final static String tag92 = "92";
    /**
     * Issuer Public Key Exponent Issuer public key exponent used for the verification of the Signed Static Application
     * Data and the ICC Public Key Certificate
     * ICC b '70' or '77' '9F32' 1 to 3
     */
    public final static String tag9F32 = "9F32";
    /**
     * Integrated Circuit Card (ICC) Public Key Certificate
     * ICC Public Key certified by the issuer ICC b '70' or '77' '9F46' NI
     */
    public final static String tag9F46 = "9F46";
    /**
     * Integrated Circuit Card (ICC) Public Key Exponent ICC Public Key Exponent used for the
     * verification of the Signed Dynamic Application Data
     * ICC b '70' or '77' '9F47' 1 to 3
     */
    public final static String tag9F47 = "9F47";
    /**
     * Integrated Circuit Card (ICC) Public Key Remainder Remaining digits of the ICC Public Key Modulus
     * ICC b '70' or '77' '9F48' NIC − NI + 42
     */
    public final static String tag9F48 = "9F48";
    /**
     * Dynamic Data Authentication Data Object List (DDOL) List of data objects (tag and length) to be
     * passed to the ICC in the INTERNAL AUTHENTICATE command
     * ICC b '70' or '77' '9F49' up to 252
     */
    public final static String tag9F49 = "9F49";

    /**
     * optional Static Data Authentication Tag List
     */
    public final static String tag9F4A = "9F4A";

    /**
     * Data Authentication Code
     */
    public final static String tag9F45 = "9F45";

    /**
     * Unpredictable Number , 4 bytes
     */
    public final static String tag9F37 = "9F37";

    /**
     * Signed Dynamic Application Data
     */
    public final static String tag9F4B = "9F4B";

    /**
     * ICC Dynamic Number
     * The ICC Dynamic Number is a time-variant parameter generated by the ICC (it can for example be an
     * unpredictable number or a counter incremented each time the ICC receives
     * an INTERNAL AUTHENTICATE command).
     */
    public final static String tag9F4C = "9F4C";

    /**
     * Application Version Number Version number assigned by the payment system for the application
     * ICC b '70' or '77' '9F08' 2
     */
    public final static String tag9F08 = "9F08";

    /**
     * Application Usage Control Indicates issuer’s specified restrictions on the geographic usage
     * and services allowed for the application
     * ICC b '70' or '77' '9F07' 2
     *
     * Application Usage Control Byte 1 (Leftmost)
     * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
     * 1 x x x x x x x Valid for domestic cash transactions
     * x 1 x x x x x x Valid for international cash transactions
     * x x 1 x x x x x Valid for domestic goods
     * x x x 1 x x x x Valid for international goods
     * x x x x 1 x x x Valid for domestic services
     * x x x x x 1 x x Valid for international services
     * x x x x x x 1 x Valid at ATMs
     * x x x x x x x 1 Valid at terminals other than ATMs
     *
     * Application Usage Control Byte 2 (Rightmost)
     * b8 b7 b6 b5 b4 b3 b2 b1 Meaning
     * 1 x x x x x x x Domestic cashback allowed
     * x 1 x x x x x x International cashback allowed
     * x x 0 x x x x x RFU
     * x x x 0 x x x x RFU
     * x x x x 0 x x x RFU
     * x x x x x 0 x x RFU
     * x x x x x x 0 x RFU
     * x x x x x x x 0 RFU
     */
    public final static String tag9F07 = "9F07";

    /**
     * Issuer Country Code Indicates the country of the issuer according to ISO 3166
     * ICC n 3 '70' or '77' '5F28' 2
     */
    public final static String tag5F28 = "5F28";

    /**
     * Cardholder Verification Method (CVM) List Identifies a method of verification of the cardholder supported by the application
     * ICC b '70' or '77' '8E' 10-252
     */
    public final static String tag8E = "8E";

    /**
     * ATC (tag '9F36')
     */
    public final static String tag9F36 = "9F36";

    /**
     * Last Online ATC Register (tag '9F13')
     */
    public final static String tag9F13 = "9F13";

    /**
     * PIN Try Counter (tag '9F17')
     */
    public final static String tag9F17 = "9F17";

    /**
     * Log Format (tag '9F4F')
     */
    public final static String tag9F4F = "9F4F";

    /**
     * NI ICC PIN Encipherment Public Key Certificate b
     */
    public final static String tag9F2D = "9F2D";

    /**
     * 1 or 3 ICC PIN Encipherment Public Key Exponent b
     */
    public final static String tag9F2E = "9F2E";

    /**
     * NPE – NI + 42 ICC PIN Encipherment Public Key Remainder, if present
     */
    public final static String tag9F2F = "9F2F";

    /**
     * Lower Consecutive Offline Limit
     * Issuer-specified preference for the maximum number of consecutive offline transactions for this ICC application allowed in a terminal with online capability
     * ICC b '70' or '77' '9F14' 1
     */
    public final static String tag9F14 = "9F14";

    /**
     * Upper Consecutive Offline Limit
     * Issuer-specified preference for the maximum number of consecutive offline transactions for this ICC application allowed in a terminal without online capability
     * ICC b '70' or '77' '9F23' 1
     */
    public final static String tag9F23 = "9F23";

    /**
     * Issuer Action Code - Default
     * Specifies the issuer’s conditions that cause a transaction to be rejected if it might have been approved online, but the terminal is unable to process the transaction online
     * ICC b '70' or '77' '9F0D' 5
     */
    public final static String tag9F0D = "9F0D";

    /**
     * Issuer Action Code - Denial
     * Specifies the issuer’s conditions that cause the denial of a transaction without attempt to go online
     * ICC b '70' or '77' '9F0E' 5
     */
    public final static String tag9F0E = "9F0E";

    /**
     * Issuer Action Code - Online
     * Specifies the issuer’s conditions that cause a transaction to be transmitted online
     * ICC b '70' or '77' '9F0F' 5
     */
    public final static String tag9F0F = "9F0F";

    /**
     * Transaction Certificate Data Object List (TDOL)
     * List of data objects (tag and length) to be used by the terminal in generating the TC Hash Value
     * ICC b '70' or '77' '97' var. up to 252
     */
    public final static String tag97 = "97";

    /**
     * Cryptogram Information Data
     * Indicates the type of cryptogram and the actions to be performed by the terminal
     * ICC b '77' or '80' '9F27' 1
     */
    public final static String tag9F27 = "9F27";


    /**
     * Issuer Application Data
     * Contains proprietary application data for transmission to the issuer in an online transaction.
     * Note: For CCD-compliant applications, Annex C, section C7 defines the specific coding of the Issuer Application Data (IAD). To avoid potential conflicts
     * with CCD-compliant applications, it is strongly recommended that the IAD data element in an application that is not CCD-compliant should not
     * use the coding for a CCD-compliant application
     * ICC b '77' or '80' '9F10' var. up to 32
     */
    public final static String tag9F10 = "9F10";

    /**
     * TAG 9F7C
     * Visa Customer Exclusive Data
     * an VAR ..64
     */
    public final static String tag9F7C = "9F7C";

    /**
     * Transaction Certificate (TC) Hash Value
     * Result of a hash function specified in Book 2, Annex B3.1
     * Terminal b — '98' 20
     */
    public final static String tag98 = "98";
}
