package javax.xml.bind;

import java.util.Base64;

/**
 * Small JAXB compatibility shim for Java runtimes that removed JAXB.
 *
 * AE2 0.56.x only needs the binary conversion methods when it persists grid
 * storage. Java 8 supplies these methods from JAXB; Cleanroom installations
 * may run the same 1.12.2 pack on Java 25, where the package no longer exists.
 */
public final class DatatypeConverter {
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    private DatatypeConverter() {
    }

    public static byte[] parseHexBinary(String lexicalXSDHexBinary) {
        if (lexicalXSDHexBinary == null) {
            throw new IllegalArgumentException("hex value must not be null");
        }
        int length = lexicalXSDHexBinary.length();
        if ((length & 1) != 0) {
            throw new IllegalArgumentException("hex value must contain an even number of characters");
        }
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            int high = Character.digit(lexicalXSDHexBinary.charAt(i), 16);
            int low = Character.digit(lexicalXSDHexBinary.charAt(i + 1), 16);
            if (high < 0 || low < 0) {
                throw new IllegalArgumentException("invalid hexadecimal character");
            }
            result[i / 2] = (byte) ((high << 4) | low);
        }
        return result;
    }

    public static String printHexBinary(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("byte value must not be null");
        }
        char[] result = new char[value.length * 2];
        for (int i = 0; i < value.length; i++) {
            int current = value[i] & 0xFF;
            result[i * 2] = HEX[current >>> 4];
            result[i * 2 + 1] = HEX[current & 0x0F];
        }
        return new String(result);
    }

    public static byte[] parseBase64Binary(String lexicalXSDBase64Binary) {
        if (lexicalXSDBase64Binary == null) {
            throw new IllegalArgumentException("base64 value must not be null");
        }
        return Base64.getDecoder().decode(lexicalXSDBase64Binary);
    }

    public static String printBase64Binary(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("byte value must not be null");
        }
        return Base64.getEncoder().encodeToString(value);
    }
}
