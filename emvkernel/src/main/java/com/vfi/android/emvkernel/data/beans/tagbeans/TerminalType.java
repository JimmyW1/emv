package com.vfi.android.emvkernel.data.beans.tagbeans;

import com.vfi.android.libtools.utils.StringUtil;

public class TerminalType {
    /**
     * A1 Terminal Type
     *                                                      Operational Control Provided By:
     *      Environment                              Financial Institution | Merchant | Cardholder
     * Attended
     *      Online only                                       11           |    21    |    —
     *      Offline with online capability                    12           |    22    |    —
     *      Offline only                                      13           |    23    |    —
     * Unattended
     *      Online only                                       14           |    24    |   34
     *      Offline with online capability                    15           |    25    |   35
     *      Offline only                                      16           |    26    |   36
     *                             Table 24: Terminal Type
     *
     * Terminal Types '14', '15', and '16' with cash disbursement capability (Additional
     * Terminal Capabilities, byte 1, ‘cash’ bit = 1) are considered to be ATMs. All other
     * Terminal Types are not considered to be ATMs.
     *
     * Examples of terminal types are:
     * • Attended and controlled by financial institution: Branch terminal
     * • Attended and controlled by merchant: Electronic cash register, portable POS
     * terminal, stand-alone POS terminal, host concentrating POS terminal
     * • Unattended and controlled by financial institution: ATM, banking automat
     * • Unattended and controlled by merchant: Automated fuel dispenser, pay
     * telephone, ticket dispenser, vending machine
     * • Unattended and controlled by cardholder: Home terminal, personal computer,
     * screen telephone, Payphones, Digital interactive Television / Set Top Boxes.
     * See Annex E for more detailed examples.
     */

    private int type;

    public TerminalType(String hexStr) {
        this.type = StringUtil.parseInt(hexStr, 22);
    }

    public boolean isAttendedTerminalType() {
        if (type == 11 || type == 12 || type == 13 || type == 21 || type == 22 || type == 23) {
            return true;
        }

        return false;
    }

    public boolean isOnlineOnly() {
       if (type == 11 || type == 21 || type == 14 || type == 24 || type == 34) {
           return true;
       }

       return false;
    }

    public boolean isOfflineWithOnlineCapability() {
        if (type == 12 || type == 22 || type == 15 || type == 25 || type == 35) {
            return true;
        }

        return false;
    }

    public boolean isOfflineOnly() {
        if (type == 13 || type == 23 || type == 16 || type == 26 || type == 36) {
            return true;
        }

        return false;
    }
}
