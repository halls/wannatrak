/*
 * Copyright 2009 Andrey Khalzov, and individual contributors as indicated by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * Created by Andrey Khalzov
 * 29.07.2008 23:56:56
 */
package org.wannatrak.middleware.util;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class PasswordHelper {
    private static final MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static BigInteger getHashCode(String string) {
        try {
            final MessageDigest messageDigest = (MessageDigest) md5.clone();
            return getHashcodeBI(messageDigest, string);
        } catch (CloneNotSupportedException e) {
            synchronized (md5) {
                return getHashcodeBI(md5, string);
            }
        }
    }

    public static String getHashCodeAsHex(String string) {
        md5.update(string.getBytes());
        final byte[] hashcode = md5.digest();
        return ConvertHelper.bytes2Hex(hashcode);
    }

    private static BigInteger getHashcodeBI(MessageDigest md5, String string) {
        md5.update(string.getBytes());
        final byte[] hashcode = md5.digest();
        return new BigInteger(hashcode);
    }
}
