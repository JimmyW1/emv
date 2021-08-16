package com.vfi.android.emvkernel.data.consts;

public class CDAMode {
    /**
     *                                Table 30: CDA Modes
     * -----------------------------------------------------------------------------------------------------
     * Mode   |  Request CDA on ARQC  | Request CDA on 2nd GEN AC (TC) after approved online authorisation |
     * 1             Yes                                 Yes
     * 2             Yes                                 No
     * 3             No                                  No
     * 4             No                                  Yes
     * -----------------------------------------------------------------------------------------------------
     */

    public static final int MODE1  = 1;
    public static final int MODE2  = 2;
    public static final int MODE3  = 3;
    public static final int MODE4  = 4;
}
