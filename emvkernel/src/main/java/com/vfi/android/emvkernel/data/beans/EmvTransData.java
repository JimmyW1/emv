package com.vfi.android.emvkernel.data.beans;

import com.vfi.android.emvkernel.data.beans.tagbeans.TSI;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.consts.TerminalTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmvTransData {
    private List<EmvApplication> candidateList;
    private List<Map<String, String>> terminalApplicationMapList;
    private Map<String, String> selectAppTerminalParamsMap;
    private List<String> caPublicKeyList;
    private int errorCode;
    private Map<String, String> tagMap;
    private com.vfi.android.emvkernel.data.beans.tagbeans.TVR tvr;
    private com.vfi.android.emvkernel.data.beans.tagbeans.TSI tsi;

    public EmvTransData() {
        candidateList = new ArrayList<>();
        tagMap = new HashMap<>();
        tvr = new com.vfi.android.emvkernel.data.beans.tagbeans.TVR();
        tsi = new com.vfi.android.emvkernel.data.beans.tagbeans.TSI();
    }

    public void resetEmvTransData() {
        candidateList.clear();
        tagMap.clear();

        if (terminalApplicationMapList != null) {
            terminalApplicationMapList.clear();
        }

        if (caPublicKeyList != null) {
            caPublicKeyList.clear();
        }
    }

    public void clearTVRAndTSI() {
        tvr.clear();
        tsi.clear();

        updateTVR();
        updateTSI();
    }

    public void updateTVR() {
        tagMap.put(TerminalTag.tag95, tvr.getTVRHex());
    }

    public void updateTSI() {
        tagMap.put(TerminalTag.tag9B, tsi.getTSIHex());
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

    public Map<String, String> getTagMap() {
        return tagMap;
    }

    public TSI getTsi() {
        return tsi;
    }

    public TVR getTvr() {
        return tvr;
    }

    public Map<String, String> getSelectAppTerminalParamsMap() {
        return selectAppTerminalParamsMap;
    }

    public void setSelectAppTerminalParamsMap(Map<String, String> selectAppTerminalParamsMap) {
        this.selectAppTerminalParamsMap = selectAppTerminalParamsMap;
    }
}
