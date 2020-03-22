package com.vfi.android.communication.terminal.deviceservice;



import com.vfi.android.libtools.utils.LogUtil;

import io.reactivex.Observable;

/**
 * Created by yichao.t on 2018/1/8.
 */

public class RfCardReaderObservable {
    public final static int CARDREADER_TYPE = 0;
    private static final String TAG = "RfCardReaderObservable";
    private IPosServiceImpl posService;

    private static class SingletonHolder {
        private static final RfCardReaderObservable INSTANCE = new RfCardReaderObservable();
    }

    private RfCardReaderObservable() {
    }

    public static final RfCardReaderObservable getInstance() {
        return RfCardReaderObservable.SingletonHolder.INSTANCE;
    }

    public RfCardReaderObservable build(IPosServiceImpl posService) {
        this.posService = posService;
        return this;
    }


    Observable<Boolean> isRfCardPresent() {
        return Observable.create(e -> {
            Boolean isCardExist = posService.getHandler().getRFCardReader().isExist();
            LogUtil.i(TAG, "isCardIn isExist: " + isCardExist);
            e.onNext(isCardExist);
            e.onComplete();

        });
    }
}
