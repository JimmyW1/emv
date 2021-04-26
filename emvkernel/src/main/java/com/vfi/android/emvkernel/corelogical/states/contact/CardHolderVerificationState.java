package com.vfi.android.emvkernel.corelogical.states.contact;

import com.vfi.android.emvkernel.corelogical.states.base.AbstractEmvState;
import com.vfi.android.emvkernel.corelogical.states.base.EmvStateType;

public class CardHolderVerificationState extends AbstractEmvState {
    public CardHolderVerificationState() {
        super(EmvStateType.STATE_CARDHOLDER_VERIFICATION);
    }
}
