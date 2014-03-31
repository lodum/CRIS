package de.ifgi.lodum.util;

import java.util.*;
import java.io.*;
import java.security.*;

/* Saxon extension for generating unique hash values. */

public class Md5 {
public static String hex(byte[] array) {
StringBuffer sb = new StringBuffer();
for (int i = 0; i < array.length; ++i) {
sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1,3));
}
return sb.toString();
}
public static String md5 (String message) throws NoSuchAlgorithmException, UnsupportedEncodingException {
MessageDigest md = MessageDigest.getInstance("MD5");
return hex (md.digest(message.getBytes("CP1252")));
}
}