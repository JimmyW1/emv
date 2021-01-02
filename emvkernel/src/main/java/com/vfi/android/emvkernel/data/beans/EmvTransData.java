package com.vfi.android.emvkernel.data.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmvTransData {
    private List<EmvApplication> candidateList;
    private List<Map<String, String>> terminalApplicationMapList;
    private List<String> caPublicKeyList;
    private int errorCode;

    public EmvTransData() {
        candidateList = new ArrayList<>();
    }

    public void resetEmvTransData() {
        candidateList.clear();

        if (terminalApplicationMapList != null) {
            terminalApplicationMapList.clear();
        }

        if (caPublicKeyList != null) {
            caPublicKeyList.clear();
        }
    }

    public List<EmvApplication> getCandidateList() {
        if (candidateList == null) {
            candidateList = new ArrayList<>();
        }

        return candidateList;
    }

    public void setCandidateList(List<EmvApplication> candidateList) {
        if (candidateList == null) {
            candidateList = new ArrayList<>();
        }

        this.candidateList = candidateList;
    }

    public List<Map<String, String>> getTerminalApplicationMapList() {
        return terminalApplicationMapList;
    }

    public void setTerminalApplicationMapList(List<Map<String, String>> terminalApplicationMapList) {
        if (terminalApplicationMapList == null) {
            terminalApplicationMapList = new ArrayList<>();
        }

        this.terminalApplicationMapList = terminalApplicationMapList;
    }

    public List<String> getCaPublicKeyList() {
        return caPublicKeyList;
    }

    public void setCaPublicKeyList(List<String> caPublicKeyList) {
        if (caPublicKeyList == null) {
            caPublicKeyList = new ArrayList<>();
        }

        this.caPublicKeyList = caPublicKeyList;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
