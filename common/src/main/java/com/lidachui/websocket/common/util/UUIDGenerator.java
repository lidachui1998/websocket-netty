package com.lidachui.websocket.common.util;

import java.security.SecureRandom;
import java.util.*;

/**
 * UUIDGenerator
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/2/28 16:20
 */
public class UUIDGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateUUID(int length, boolean withHyphen) {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f;
        randomBytes[6] |= 0x40;
        randomBytes[8] &= 0x3f;
        randomBytes[8] |= 0x80;
        UUID uuid = UUID.nameUUIDFromBytes(randomBytes);
        String result = withHyphen ? uuid.toString() : uuid.toString().replaceAll("-", "");
        return result.substring(0, Math.min(length, result.length()));
    }

    public static String uuid() {
        return generateUUID(32, false);
    }

}

