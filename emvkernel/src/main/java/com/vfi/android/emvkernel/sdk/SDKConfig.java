package com.vfi.android.emvkernel.sdk;

import com.vfi.android.emvkernel.interfaces.IEmvComm;

public class SDKConfig {
    private String dbRootPath;  // O
    private IEmvComm emvComm;   // M

    public String getDbRootPath() {
        return dbRootPath;
    }

    public void setDbRootPath(String dbRootPath) {
        this.dbRootPath = dbRootPath;
    }

    public IEmvComm getEmvComm() {
        return emvComm;
    }

    public void setEmvComm(IEmvComm emvComm) {
        this.emvComm = emvComm;
    }
}
