package com.vfi.android.emvkernel.utils;

import com.vfi.android.emvkernel.data.beans.DOLBean;
import com.vfi.android.emvkernel.data.consts.TagFormat;
import com.vfi.android.emvkernel.data.consts.TerminalTag;
import com.vfi.android.libtools.consts.TAGS;
import com.vfi.android.libtools.utils.LogUtil;
import com.vfi.android.libtools.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOLUtil {
    private final static String TAG = TAGS.DOL;
    private static Map<String, Integer> tagTypeMap = new HashMap<>();

    static {
        tagTypeMap.put(TerminalTag.E_CASH_INDICATOR, TagFormat.FM_B);
        tagTypeMap.put(TerminalTag.tag9F02, TagFormat.FM_N);
        tagTypeMap.put(TerminalTag.tag5F2A, TagFormat.FM_N);
    }

    public static List<DOLBean> toDOLDataList(String dolHexStr) {
        List<DOLBean> dolBeanList = new ArrayList<>();

        if (dolHexStr == null || dolHexStr.length() == 0) {
            return dolBeanList;
        }

        byte[] dolStrBytes = StringUtil.hexStr2Bytes(dolHexStr);

        parseDOL(dolBeanList, dolStrBytes, 0);

        return dolBeanList;
    }

    public static void parseDOL(List<DOLBean> dolBeanList, byte[] dolStrBytes, int start) {
        if (dolStrBytes == null || start >= dolStrBytes.length) {
            return;
        }

        String tag;
        int len, tagStart;
        boolean isIncludeSubTag;
        boolean hasNextTag;
        for (int i = start; i < dolStrBytes.length; ) {
            tagStart = i;
//            /**
//             * TAG 编码规则
//             * 第一个字节 bit8~bit7 表明tag所属类型
//             * 00 通用级 universal class
//             * 01 应用级 application class
//             * 10 规范级 context-specific class
//             * 11 私有级 private class
//             * 第一个字节 bit6 表明本tag的value部分是否包含子tag
//             * 第一个字节 bit5~bit1 表明是否有第二个字节一起表示本tag
//             * 11111 全为1表示有第二个字节
//             * 第二个字节 bit8 为1，同时bit7~bit1 大于0 表示有下一个tag
//             * 第三~N个字节同第二个字节
//             */
            isIncludeSubTag = (dolStrBytes[i] & 0x20) > 0;
            hasNextTag = (dolStrBytes[i] & 0x1F) == 0x1F;
            LogUtil.d(TAG, "hasNextTag=" + hasNextTag);
            LogUtil.d(TAG, "isIncludeSubTag=" + isIncludeSubTag);
            i++;
            if (hasNextTag) {
                while (i < dolStrBytes.length && (dolStrBytes[i] & 0x80) > 0) {
                    i++;
                }
                i++;
            }

            tag = StringUtil.byte2HexStr(dolStrBytes, tagStart, i - tagStart);
            LogUtil.d(TAG, "TAG=[" + tag + "]" + " i=" + i);

//            /**
//             * DOL only one byte length - page 39 of Book 3
//             */
            len = Integer.parseInt(StringUtil.byte2HexStr(dolStrBytes, i, 1), 16);
            i++;
            LogUtil.d(TAG, "len=[" + len + "]" + " i=" + i);

            dolBeanList.add(new DOLBean(tag, len, getFormatType(tag)));
        }
    }

    private static int getFormatType(String tag) {
        if (tagTypeMap.containsKey(tag)) {
            return tagTypeMap.get(tag);
        }

        return TagFormat.FM_AN;
    }
}
