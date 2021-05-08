package com.vfi.android.emvkernel.data.beans;

import com.vfi.android.emvkernel.data.beans.tagbeans.CvmResult;
import com.vfi.android.emvkernel.data.beans.tagbeans.TSI;
import com.vfi.android.emvkernel.data.beans.tagbeans.TVR;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.emvkernel.interfaces.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmvTransData {
    private List<EmvApplication> candidateList;
    private List<Map<String, String>> terminalApplicationMapList;
    private Map<String, String> selectAppTerminalParamsMap;
    private List<Map<String, String>> caPublicKeyList;
    private Map<String, String> selectCardEmvKeyParamsMap;
    private String staticDataToBeAuthenticated;
    private boolean isExistStaticDataRecordNotCodeWithTag70;
    private int errorCode;
    private Map<String, String> tagMap;
    private TVR tvr;
    private TSI tsi;
    private CvmResult cvmResult;

    public EmvTransData() {
        candidateList = new ArrayList<>();
        tagMap = new HashMap<>();
        tvr = new TVR(new Callback() {
            @Override
            public void onDataChanged(String data) {
                updateTVR();
            }
        });
        tsi = new TSI(new Callback() {
            @Override
            public void onDataChanged(String data) {
                updateTSI();
            }
        });
        cvmResult = new CvmResult(new Callback() {
            @Override
            public void onDataChanged(String data) {
                updateCvmResult();
            }
        });
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
        cvmResult.clear();

        updateTVR();
        updateTSI();
        updateCvmResult();
    }

    public void updateTVR() {
        tagMap.put(TerminalTag.tag95, tvr.getTVRHex());
    }

    public void updateTSI() {
        tagMap.put(TerminalTag.tag9B, tsi.getTSIHex());
    }

    public void updateCvmResult() {
        tagMap.put(TerminalTag.tag9F34, cvmResult.getTVRHex());
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

    public List<Map<String, String>> getCaPublicKeyList() {
        return caPublicKeyList;
    }

    public void setCaPublicKeyList(List<Map<String, String>> caPublicKeyList) {
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

    public CvmResult getCvmResult() {
        return cvmResult;
    }

    public Map<String, String> getSelectAppTerminalParamsMap() {
        return selectAppTerminalParamsMap;
    }

    public void setSelectAppTerminalParamsMap(Map<String, String> selectAppTerminalParamsMap) {
        this.selectAppTerminalParamsMap = selectAppTerminalParamsMap;
    }

    public Map<String, String> getSelectCardEmvKeyParamsMap() {
        return selectCardEmvKeyParamsMap;
    }

    public void setSelectCardEmvKeyParamsMap(Map<String, String> selectCardEmvKeyParamsMap) {
        this.selectCardEmvKeyParamsMap = selectCardEmvKeyParamsMap;
    }

    public String getStaticDataToBeAuthenticated() {
        return staticDataToBeAuthenticated;
    }

    public void setStaticDataToBeAuthenticated(String staticDataToBeAuthenticated) {
        this.staticDataToBeAuthenticated = staticDataToBeAuthenticated;
    }

    public boolean isExistStaticDataRecordNotCodeWithTag70() {
        return isExistStaticDataRecordNotCodeWithTag70;
    }

    public void setExistStaticDataRecordNotCodeWithTag70(boolean existStaticDataRecordNotCodeWithTag70) {
        isExistStaticDataRecordNotCodeWithTag70 = existStaticDataRecordNotCodeWithTag70;
    }
}
