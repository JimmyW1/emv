package com.vfi.android.emvkernel.data.consts;

public class TerminalTag {
    public final static String ASI = "DF01";  // ASI - Application Selection Indicator
    public final static String tag9F7A = "9F7A"; // PBOC 电子现金终端支持指示器（EC Terminal Support Indicator） 9F7A 1 b
    public final static String tag9F7B = "9F7B"; //PBOC 电子现金终端交易限额（EC Terminal Transaction Limit） 9F7B 6 n 12
    public final static String tag5F57 = "5F57"; // page 123 book 3 Account Type n 2 {@link AccountType}
    public final static String tag9F01 = "9F01"; // page 123 book 3 Acquirer Id  n 6-11
    public final static String tag9F40 = "9F40"; // page 123 book 3 Additional Terminal Capabilities b
    public final static String tag81 = "81"; // page 123 book 3 Amount Authorised(Binary) b - Authorised amount of transaction( Excluding adjustments)
    public final static String tag9F02 = "9F02";  // Amount, Authorised (Numeric) n 12 - Authorised amount of transaction (Excluding adjustments)
    public final static String tag9F04 = "9F04"; // page 123 book 3 Amount Other(Binary) b - Secondary amount associated with the transaction representing a cashback amount
    public final static String tag9F03 = "9F03"; // page 123 book 3 Amount Other(Numeric) n 12 - Secondary amount associated with the transaction representing a cashback amount
    public final static String tag9F3A = "9F3A"; // page 123 book 3 Amount Reference Currency b - Authorised amount expressed in the reference currency
    public final static String tag9F06 = "9F06"; // page 123 book 3 Application Identifier(AID) terminal b length 5-16 - Identifies the application as described in ISO/IEC 7816-5
    public final static String tag9F09 = "9F09";  // Application Version Number b length 2 - Version number assigned by the payment system for the application
    public final static String tag8A = "8A";  // Authorisation Response Code, Issuer/Terminal an2 length 2 - Code that defines the disposition of a message
    public final static String tag9F34 = "9F34";  // Cardholder Verification Method(CVM) Results , b length 3  - Indicate the results of the last CVM performed
    public final static String CERT_PUB_KEY_CHECKSUM = "CERT_PUB_KEY_CHECKSUM";  // Certification Authority Public Key Check Sum b length 20 - A check value calculated on the concatenation of all parts of the Certification Authority Public Key(RID, Certification Authority Public Key Index, Certification Authority Public Key Modulus, Certification Authority Public Key Exponent) using SHA-1
    public final static String CERT_PUB_KEY_EXP = "CERT_PUB_KEY_EXP";  // Certification Authority Public key Exponent. b length 1 or 3 - Value of the exponent part of the Certification Authority Public key
    public final static String tag9F22 = "9F22";  // Certification Authority Public key Index. b length 1 - Identifies the certification authority's public key in conjunction with the RID
    public final static String CERT_PUB_KEY_MODULUS = "CERT_PUB_KEY_MODULUS";  // Certification Authority Public key Modulus. b NAC up to 248 - Value of the modulus part of the certification authority public key
    public final static String tag83 = "83";  // Command Template var. - Identifies the data field of a command message
    public final static String DDOL = "DDOL"; // Default Dynamic Data Authentication Data Object List(DDOL) b var - DDOL to be used for constructing the INTERNAL AUTHENTICATE command if the DDOL in the card is not present
    public final static String TDOL = "DDOL"; // Default Transaction Certificate Data Object List(TDOL) b var - TDOL to be used for generating the TC Hash Value if the TDOL in the card is not present
    public final static String ENCRYPTED_PIN_BLOCK = "ENCRYPTED_PIN_BLOCK"; // Enciphered Personal Identification Number(PIN) Data, b length 8 - Transaction PIN enciphered at the PIN pad for online verification or for offline verification if the PIN pad and IFD are not a single integrated device
    public final static String tag9F1E = "9F1E"; // Interface Device(IFD) Serial Number an 8 length 8 - Unique and permanent serial number assigned to the IFD by the manufacturer
    public final static String ISSUER_SCRIPT_RESULTS = "ISSUER_SCRIPT_RESULTS"; // Issuer Script Results b var - Indicates the result of the terminal script processing
    public final static String MAXIMUM_TARGET_PERCENTAGE = "MAXIMUM_TARGET_PERCENTAGE"; // Maximum Target Percentage to be used for Biased Random Selection - Value used in terminal risk management for random transaction selection
    public final static String tag9F15 = "9F15"; // Merchant Category Code n 4 length 2  - Classifies the type of business being done by the merchant, represented according to ISO 8583:1993 for Card Acceptor Business Code
    public final static String tag9F16 = "9F16"; // Merchant Identifier ans 15 length 15 - When concatenated with the Acquirer Identifier, uniquely identifies a given merchant
    public final static String tag9F4E = "9F4E"; // Merchant Name and Location var - Indicates the name and location of the merchant
    public final static String MESSAGE_TYPE = "MESSAGE_TYPE"; // Message type n 2 length 1 - Indicates whether the batch data capture records is a financial record or advice
    public final static String PIN_SECRET_KEY = "PIN_SECRET_KEY"; // Personal Identification Number(PIN) Pad Secret Key
    public final static String tag9F39 = "9F39"; // Point-of-Service(POS) Entry Mode n 2 length 1 - Indicates the method by which the PAN was entered, according to the first two digits of the ISO 8583:1987 POS Entry Mode
    public final static String TARGET_PERCENTAGE = "TARGET_PERCENTAGE"; // Target Percentage to be Used for Random Selection - Value used in terminal risk management for random transaction selection
    public final static String DEFAULT_TAC = "DEFAULT_TAC"; // Terminal Action Code - Default b length 5 - Specifies the acquirer's conditions that cause a transaction to be rejected if it might have been approved online, but the terminal is unable to process the transaction online
    public final static String DENIAL_TAC = "DENIAL_TAC"; // Terminal Action Code - Denial b length 5 - Specifies the acquirer's conditions that cause the denial of a transaction without attempt to go online
    public final static String ONLINE_TAC = "ONLINE_TAC"; // Terminal Action Code - Online b length 5 - Specifies the acquirer's conditions that cause the denial of a transaction to be transmitted online
    public final static String tag9F33 = "9F33"; // Terminal Capabilities b length 3 - Indicates the card data input , CVM and Security capabilities of the terminal
    public final static String tag9F1A = "9F1A"; // Terminal Country Code n 3 length 2 - Indicates the country of the terminal , represented according to ISO 3166
    public final static String tag9F1B = "9F1B"; // Terminal Floor Limit b length 4 - Indicates the floor limit in the terminal in conjunction with the AID
    public final static String tag9F1C = "9F1C"; // Terminal Identification an 8 length 8 - Designates the unique location of a terminal at a merchant
    public final static String tag9F1D = "9F1D"; // Terminal Risk Management Data b length 1-8. - Application-specific value used by the card for risk management purposes
    public final static String tag9F35 = "9F35"; // Terminal Type n 2 length 1 - Indicates the environment of the terminal , its communications capability, and its operational control
    public final static String tag95 = "95"; // Terminal Verification Results b length 5 - Status of the different functions as seen from the terminal
    public final static String THRESHOLD_VALUE = "THRESHOLD_VALUE"; // Threshold Value for Biased Random Selection
    public final static String TRANS_AMOUNT = "TRANS_AMOUNT"; // Transaction amount n 12 length 6 - Clearing amount of the transaction, including tips and other adjustments
    public final static String tag98 = "98"; // Transaction Certificate(TC) Hash Value b length 20 - Result of a hash function specified in Book 2 Annex B3.1
    public final static String tag5F2A = "5F2A"; // Transaction Currency Code n 3 length 2 - Indicates the currency code of the transaction according to ISO 4217
    public final static String tag5F36 = "5F36"; // Transaction Currency Exponent n 1 length 1 - Indicates the implied position of the decimal point from the right of the transaction amount represented according to ISO 4217
    public final static String tag9A = "9A"; // Transaction Date n 6 YYMMDD length 3 - Local date that the transaction was authorised
    public final static String tag99 = "99"; // Transaction Personal Identification Number(PIN) Data b length var - Data entered by the cardholder for the purpose of the PIN verification
    public final static String tag9F3C = "9F3C"; // Transaction Reference Currency Code n 3 length 2 - Code defining the common currency used by the terminal in case the Transaction Currency Code is different from the Application Currency code
    public final static String TRANS_REF_CURRENCY_CONVERSION = "TRANS_REF_CURRENCY_CONVERSION"; // Transaction reference currency conversion
    public final static String tag9F3D = "9F3D"; // Transaction reference currency Exponent n 1 length 1 - Indicates the implied position of the decimal point from the right of the transaction amount, with the Transaction Reference Currency Code represented according to ISO 4217
    public final static String tag9F41 = "9F41"; // Transaction Sequence Counter n 4-8 length 2-4 - Counter maintained by the terminal that is incremented by one for each transaction
    public final static String tag9B = "9B"; // Transaction Status Information b length 2 - Indicates the functions performed in a transaction
    public final static String tag9F21 = "9F21"; // Transaction Time n 6 HHMMSS length 3 - Local time that the transaction was authorised
    public final static String tag9C = "9C"; // Transaction Type n 2 length 1 - Indicates the type of financial transaction, represented by the first two digits of the ISO 8583:1987 Processing Code. The actual values to be used for the Transaction Type data element are defined by the relevant payment system.
    public final static String tag9F37 = "9F37"; // Unpredictable Number b length 4 - Value to provide variability and uniqueness to the generation of a cryptogram

    public static int getTagFormat(String tag) {
        switch (tag) {
            case tag9F7A:
            case tag9F40:
            case tag81:
            case tag9F04:
            case tag9F3A:
            case tag9F06:
            case tag9F09:
            case tag9F34:
//                CERT_PUB_KEY_CHECKSUM
//                CERT_PUB_KEY_EXP
//                CERT_PUB_KEY_MODULUS
            case tag9F22:
            case tag9F33:
            case tag9F1B:
            case tag9F1D:
            case tag98:
            case tag95:
            case tag99:
            case tag9B:
            case tag9F37:
                return TagFormat.FM_B;
            case tag9F7B:
            case tag5F57:
            case tag9F01:
            case tag9F02:
            case tag9F03:
            case tag9F15:
            case tag9F39:
            case tag9F1A:
            case tag9F35:
            case tag5F2A:
            case tag5F36:
            case tag9A:
            case tag9F3D:
            case tag9F41:
            case tag9F21:
            case tag9C:
                return TagFormat.FM_N;
            case tag8A:
            case tag9F1E:
            case tag9F1C:
                return TagFormat.FM_AN;
            case tag9F16:
                return TagFormat.FM_ANS;
            default:
                return TagFormat.FM_UNKNOWN;

//            public final static String tag83 = "83";  // Command Template var. - Identifies the data field of a command message
//            public final static String DDOL = "DDOL"; // Default Dynamic Data Authentication Data Object List(DDOL) b var - DDOL to be used for constructing the INTERNAL AUTHENTICATE command if the DDOL in the card is not present
//            public final static String TDOL = "DDOL"; // Default Transaction Certificate Data Object List(TDOL) b var - TDOL to be used for generating the TC Hash Value if the TDOL in the card is not present
//            public final static String ENCRYPTED_PIN_BLOCK = "ENCRYPTED_PIN_BLOCK"; // Enciphered Personal Identification Number(PIN) Data, b length 8 - Transaction PIN enciphered at the PIN pad for online verification or for offline verification if the PIN pad and IFD are not a single integrated device
//            public final static String ISSUER_SCRIPT_RESULTS = "ISSUER_SCRIPT_RESULTS"; // Issuer Script Results b var - Indicates the result of the terminal script processing
//            public final static String MAXIMUM_TARGET_PERCENTAGE = "MAXIMUM_TARGET_PERCENTAGE"; // Maximum Target Percentage to be used for Biased Random Selection - Value used in terminal risk management for random transaction selection
//            public final static String tag9F4E = "9F4E"; // Merchant Name and Location var - Indicates the name and location of the merchant
//            public final static String MESSAGE_TYPE = "MESSAGE_TYPE"; // Message type n 2 length 1 - Indicates whether the batch data capture records is a financial record or advice
//            public final static String PIN_SECRET_KEY = "PIN_SECRET_KEY"; // Personal Identification Number(PIN) Pad Secret Key
//            public final static String TARGET_PERCENTAGE = "TARGET_PERCENTAGE"; // Target Percentage to be Used for Random Selection - Value used in terminal risk management for random transaction selection
//            public final static String DEFAULT_TAC = "DEFAULT_TAC"; // Terminal Action Code - Default b length 5 - Specifies the acquirer's conditions that cause a transaction to be rejected if it might have been approved online, but the terminal is unable to process the transaction online
//            public final static String DENIAL_TAC = "DENIAL_TAC"; // Terminal Action Code - Denial b length 5 - Specifies the acquirer's conditions that cause the denial of a transaction without attempt to go online
//            public final static String ONLINE_TAC = "ONLINE_TAC"; // Terminal Action Code - Online b length 5 - Specifies the acquirer's conditions that cause the denial of a transaction to be transmitted online
//            public final static String THRESHOLD_VALUE = "THRESHOLD_VALUE"; // Threshold Value for Biased Random Selection
//            public final static String TRANS_AMOUNT = "TRANS_AMOUNT"; // Transaction amount n 12 length 6 - Clearing amount of the transaction, including tips and other adjustments
//            public final static String tag9F3C = "9F3C"; // Transaction Reference Currency Code n 3 length 2 - Code defining the common currency used by the terminal in case the Transaction Currency Code is different from the Application Currency code
//            public final static String TRANS_REF_CURRENCY_CONVERSION = "TRANS_REF_CURRENCY_CONVERSION"; // Transaction reference currency conversion
        }
    }
}
