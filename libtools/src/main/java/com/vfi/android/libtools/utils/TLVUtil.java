package com.vfi.android.libtools.utils;

import com.vfi.android.libtools.consts.TAGS;

import java.util.HashMap;
import java.util.Map;

public class TLVUtil {
    private final static String TAG = TAGS.TLV;

    public static Map<String, String> toTlvMap(String tlvHexStr) {
        Map<String, String> tlvMap = new HashMap<>();

        if (tlvHexStr == null || tlvHexStr.length() == 0) {
            return tlvMap;
        }

        byte[] tlvStrBytes = StringUtil.hexStr2Bytes(tlvHexStr);

        parseTags(tlvMap, tlvStrBytes, 0);

        return tlvMap;
    }

    public static void parseTags(Map<String, String> map, byte[] tlvStrBytes, int start) {
        if (tlvStrBytes == null || start >= tlvStrBytes.length) {
            return;
        }

        String tag;
        int len, tagStart;
        String value;
        boolean isIncludeSubTag;
        boolean hasNextTag;
        for (int i = start; i < tlvStrBytes.length; ) {
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
            isIncludeSubTag = (tlvStrBytes[i] & 0x20) > 0;
            hasNextTag = (tlvStrBytes[i] & 0x1F) == 0x1F;
            LogUtil.d(TAG, "hasNextTag=" + hasNextTag);
            LogUtil.d(TAG, "isIncludeSubTag=" + isIncludeSubTag);
            i++;
            if (hasNextTag) {
                while (i < tlvStrBytes.length && (tlvStrBytes[i] & 0x80) > 0) {
                    i++;
                }
                i++;
            }

            tag = StringUtil.byte2HexStr(tlvStrBytes, tagStart, i - tagStart);
            LogUtil.d(TAG, "TAG=[" + tag + "]" + " i=" + i);

//            /**
//             * Len 编码规则
//             * 第一个字节 bit8 为1 表明本tag是用来记录后面用于表示Len的字节有多少个
//             * 第一个字节 bit8 为0 即保存数值为0-127表示为长度
//             * 第二个字节及后续的字节表示长度，以网络字节序表示
//             */
            boolean isTwoByteLen = false;
            if ((tlvStrBytes[i] & 0x80) > 0) {
                isTwoByteLen = true;
            }

            if (isTwoByteLen) {
                tlvStrBytes[i] |= 0x7f;
                len = Integer.parseInt(StringUtil.byte2HexStr(tlvStrBytes, i, 2), 16);
                i += 2;
            } else {
                len = Integer.parseInt(StringUtil.byte2HexStr(tlvStrBytes, i, 1), 16);
                i++;
            }
            LogUtil.d(TAG, "len=[" + len + "]" + " i=" + i);

            value = StringUtil.byte2HexStr(tlvStrBytes, i, len);
            LogUtil.d(TAG, "value=[" + value + "]");

            map.put(tag, value);

            if (isIncludeSubTag) {
                parseTags(map, StringUtil.hexStr2Bytes(value), 0);
            }

            i += len;
            LogUtil.d(TAG, "i=" + i + " tlvStrBytes len=" + tlvStrBytes.length);
        }
    }

    public static String toTlvStr(Map<String, String> tlvMap) {
        return null;
    }
}
