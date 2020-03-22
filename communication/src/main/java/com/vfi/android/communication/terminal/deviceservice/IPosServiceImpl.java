package com.vfi.android.communication.terminal.deviceservice;

import android.content.Context;

import com.vfi.android.communication.terminal.beans.CardInformation;
import com.vfi.android.communication.terminal.beans.CheckCardParamIn;
import com.vfi.android.communication.terminal.interfaces.IPosService;
import com.vfi.smartpos.deviceservice.aidl.IDeviceService;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class IPosServiceImpl implements IPosService {
    private Context context;

    private IDeviceService handler;

    @Inject
    IPosServiceImpl(Context context) {
        this.context = context;
    }

    public IDeviceService getHandler() {
        return this.handler;
    }

    void setHandler(IDeviceService handler) {
        this.handler = handler;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public Observable<Boolean> bind() {
        return BindObservable.getInstance().build(this).create();
    }

    @Override
    public Observable<CardInformation> startCheckCard(CheckCardParamIn checkCardParamIn) {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).start(checkCardParamIn));
    }

    @Override
    public Observable<Object> stopCheckCard() {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).stop());
    }

    @Override
    public Observable<Boolean> isIcCardPresent() {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).isIcCardPresent());
    }

    @Override
    public Observable<Boolean> checkIsCardRemoved(int checkTimeSecond) {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).checkIsCardRemoved(checkTimeSecond));
    }

    @Override
    public Observable<Boolean> simulatorCardRemoved() {
        return bind().flatMap(unused -> CheckCardObservable.getInstance().build(this).simulatorCheckCardRemoved());
    }

    @Override
    public Observable<Boolean> isRfCardPresent() {
        return bind().flatMap(unused -> RfCardReaderObservable.getInstance().build(this).isRfCardPresent());
    }

    @Override
    public Observable<Boolean> powerOnSmartCardReader() {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).icCardPowerOn());
    }

    @Override
    public Observable<Boolean> powerOffSmartCardReader() {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).icCardPowerOff());
    }

    @Override
    public Observable<byte[]> executeAPDU(byte[] apduCmd) {
        return bind().flatMap(unused -> ICCardReaderObservable.getInstance().build(this).exchangeApdu(apduCmd));
    }
}
