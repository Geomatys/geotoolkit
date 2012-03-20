/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.geotoolkit.gui.swing.tree.Trees;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 *
 * @since 3.07
 */
public final class StringUtilities {

    public static final String TREE_BLANK = "\u00A0\u00A0\u00A0\u00A0";
    public static final String TREE_LINE  = "\u00A0\u00A0\u2502\u00A0";
    public static final String TREE_CROSS = "\u00A0\u00A0\u251C\u2500";
    public static final String TREE_END   = "\u00A0\u00A0\u2514\u2500";

    private static final int[] EMPTY_INT_ARRAY = new int[0];

    /**
     * static array containing all possible characters used by method for URL
     * encoding and URL decoding.
     */
    private static final String[] URLhex = {
        "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
        "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f",
        "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
        "%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f",
        "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
        "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f",
        "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
        "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
        "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
        "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f",
        "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
        "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
        "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
        "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f",
        "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
        "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
        "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
        "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
        "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
        "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f",
        "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
        "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af",
        "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7",
        "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf",
        "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
        "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf",
        "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7",
        "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df",
        "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
        "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
        "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
        "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"
    };

    private StringUtilities() {}

    /**
     * Encode a string to the "x-www-form-urlencoded" form, enhanced
     * with the UTF-8-in-URL proposal. This is what happens:
     *
     * <ul>
     * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
     *        and '0' through '9' remain the same.
     *
     * <li><p>The unreserved characters - _ . ! ~ * ' ( ) remain the same.
     *
     * <li><p>The space character ' ' is converted into a plus sign '+'.
     *
     * <li><p>All other ASCII characters are converted into the
     *        3-character string "%xy", where xy is
     *        the two-digit hexadecimal representation of the character
     *        code
     *
     * <li><p>All non-ASCII characters are encoded in two steps: first
     *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
     *        secondly each of these bytes is encoded as "%xx".
     * </ul>
     *
     * @param s The string to be encoded
     * @return The encoded string
     */
    public static String encodeToUTF8URL(final String s) {
        StringBuilder sbuf = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int ch = s.charAt(i);
            if ('A' <= ch && ch <= 'Z') {		// 'A'..'Z'
                sbuf.append((char) ch);
            } else if ('a' <= ch && ch <= 'z') {	// 'a'..'z'
                sbuf.append((char) ch);
            } else if ('0' <= ch && ch <= '9') {	// '0'..'9'
                sbuf.append((char) ch);
            } else if (ch == ' ') {			// space
                sbuf.append('+');
            } else if (ch == '-' || ch == '_' // unreserved
                    || ch == '.' || ch == '!'
                    || ch == '~' || ch == '*'
                    || ch == '\'' || ch == '('
                    || ch == ')') {
                sbuf.append((char) ch);
            } else if (ch <= 0x007f) {		// other ASCII
                sbuf.append(URLhex[ch]);
            } else if (ch <= 0x07FF) {		// non-ASCII <= 0x7FF
                sbuf.append(URLhex[0xc0 | (ch >> 6)]);
                sbuf.append(URLhex[0x80 | (ch & 0x3F)]);
            } else {					// 0x7FF < ch <= 0xFFFF
                sbuf.append(URLhex[0xe0 | (ch >> 12)]);
                sbuf.append(URLhex[0x80 | ((ch >> 6) & 0x3F)]);
                sbuf.append(URLhex[0x80 | (ch & 0x3F)]);
            }
        }
        return sbuf.toString();
    }

    /**
     * decoding an UTF8/URL encoded strings
     * this is the reverse of {@link encodeToUTF8URL} method.
     *
     * @param s string to decode ie: FORMAT%3dimage
     * @return the decoded string  ie: FORMAT=image/png
     */
    public static String decodeUTF8URL(final String s) {
        StringBuilder sbuf = new StringBuilder();
        int l = s.length();
        int ch = -1;
        int b, sumb = 0;
        for (int i = 0, more = -1; i < l; i++) {
            /* Get next byte b from URL segment s */
            switch (ch = s.charAt(i)) {
                case '%':
                    ch = s.charAt(++i);
                    int hb = (Character.isDigit((char) ch)
                            ? ch - '0'
                            : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                    ch = s.charAt(++i);
                    int lb = (Character.isDigit((char) ch)
                            ? ch - '0'
                            : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                    b = (hb << 4) | lb;
                    break;
                case '+':
                    b = ' ';
                    break;
                default:
                    b = ch;
            }
            /* Decode byte b as UTF-8, sumb collects incomplete chars */
            if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
                sumb = (sumb << 6) | (b & 0x3f);	// Add 6 bits to sumb
                if (--more == 0) {
                    sbuf.append((char) sumb); // Add char to sbuf
                }
            } else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
                sbuf.append((char) b);			// Store in sbuf
            } else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
                sumb = b & 0x1f;
                more = 1;				// Expect 1 more byte
            } else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
                sumb = b & 0x0f;
                more = 2;				// Expect 2 more bytes
            } else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
                sumb = b & 0x07;
                more = 3;				// Expect 3 more bytes
            } else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
                sumb = b & 0x03;
                more = 4;				// Expect 4 more bytes
            } else /*if ((b & 0xfe) == 0xfc)*/ {	// 1111110x (yields 1 bit)
                sumb = b & 0x01;
                more = 5;				// Expect 5 more bytes
            }
            /* No need to test if the UTF-8 encoding is well-formed */
        }
        return sbuf.toString();
    }

    /*
     * Encode the specified string with MD5 algorithm.
     *
     * @param key :  the string to encode.
     * @return the value (string) hexadecimal on 32 bits
     */
    public static String MD5encode(final String key) {

        final byte[] uniqueKey = key.getBytes();
        byte[] hash = null;
        try {
            // we get an object allowing to crypt the string
            hash = MessageDigest.getInstance("MD5").digest(uniqueKey);

        } catch (NoSuchAlgorithmException e) {
            throw new Error("no MD5 support in this VM");
        }
        final StringBuffer hashString = new StringBuffer();
        for (int i = 0; i < hash.length; ++i) {
            final String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length() - 1));
            } else {
                hashString.append(hex.substring(hex.length() - 2));
            }
        }
        return hashString.toString();
    }

    /**
     * This method clean a string encoded in a database LATIN1, this is a performed method.
     * @param str
     * @return
     *
     * @deprecated Try to specify the right encoding to {@link java.io.InputStreamReader} instead.
     */
    @Deprecated
    public static String cleanString(String str) {
        if (str != null) {
            str = str.replaceAll("Ã©", "é");
            str = str.replaceAll("Ãª", "ê");
            str = str.replaceAll("Ã¨", "è");
            str = str.replaceAll("\"", "'");
            str = str.replaceAll("Â°", "°");
            str = str.replaceAll("Ã¯", "ï");
            str = str.replaceAll("Ã´", "ô");
            str = str.replaceAll("à§", "ç");
            str = str.replaceAll("Ã", "à");
            str = str.replaceAll("Â", "");
        }
        return str;
    }

    /**
     * A utility method whitch replace the special character (é, è, à, É).
     *
     * @param s the string to clean.
     * @return a String without special character.
     *
     * @deprecated Replaced by {@link org.geotoolkit.internal.StringUtilities#toASCII}.
     */
    @Deprecated
    public static String cleanSpecialCharacter(String s) {
        if (s != null) {
            s = s.replace('é', 'e');
            s = s.replace('è', 'e');
            s = s.replace('à', 'a');
            s = s.replace('É', 'E');
        }
        return s;
    }

    /**
     * Clean a string from its leading and trailing whitespaces, and the tabulation or end of line
     * characters.
     *
     * @param s
     * @return
     */
    public static String clean(String s) {
        s = s.trim();
        s = s.replace("\t", "");
        s = s.replace("\n", "");
        s = s.replace("\r", "");
        return s;
    }

    /**
     * Clean a list of String by removing all the white space, tabulation and carriage in all the strings.
     *
     * @param list
     * @return
     */
    public static List<String> cleanStrings(final List<String> list) {
        final List<String> result = new ArrayList<String>();
        for (String s : list) {
            //we remove the bad character before the real value
           s = s.replace(" ", "");
           s = s.replace("\t", "");
           s = s.replace("\n", "");
           result.add(s);
        }
        return result;
    }

    /**
     * Returns true if the list contains a string in one of the list elements.
     * This test is not case sensitive.
     *
     * @see org.geotoolkit.util.XArrays#containsIgnoreCase(String[], String)
     *
     * @param list A list of String.
     * @param str The value searched.
     * @return
     */
    public static boolean containsIgnoreCase(final List<String> list, final String str) {
        boolean strAvailable = false;
        if (list != null) {
            for (String s : list) {
                if (s.equalsIgnoreCase(str)) {
                    strAvailable = true;
                    break;
                }
            }
        }
        return strAvailable;
    }

    /**
     * Converts all spaces from a string into the URL convention, %20.
     * Note that the given string <strong>MUST</strong> not be {@code null}.
     *
     * @param s The initial string. Should not be {@code null}.
     * @return A string with all spaces replaced by the matching URL character %20.
     *
     * @deprecated Use {@link java.net.URLEncoder#encode(String, String)} instead.
     */
    @Deprecated
    public static String convertSpacesForUrl(final String s) {
        return s.replaceAll(" ", "%20");
    }

    /**
     * Generate a {@code String} whose first character will be in upper case.
     * <p>
     * For example: {@code firstToUpper("hello")} would return {@code "Hello"},
     * while {@code firstToUpper("Hi!") would return {@code "Hi!"} unchanged.
     *
     * @param s The {@code String} to evaluate, not {@code null}.
     *
     * @return A {@code String} with the first character in upper case.
     */
    public static String firstToUpper(final String s) {
        if (s != null && !s.isEmpty()) {
            final String first = s.substring(0, 1);
            String result      = s.substring(1);
            result             = first.toUpperCase() + result;
            return result;
        }
        return s;
    }

    /**
     * Search a string for all occurence of the char.
     *
     * @param s : String to search in
     * @param occ : Occurence to search
     * @return array of all occurence indexes
     */
    public static int[] getIndexes(final String s, final char occ) {
        int pos = s.indexOf(occ);
        if(pos <0){
            return EMPTY_INT_ARRAY;
        }else{
            int[] indexes = new int[]{pos};
            pos = s.indexOf(occ, pos+1);
            for(; pos >= 0; pos = s.indexOf(occ, pos+1)){
                int end = indexes.length;
                indexes = XArrays.resize(indexes, end+1);
                indexes[end] = pos;
            }
            return indexes;
        }

    }

    /**
     * Convert the given string into a string recognize by HTML.
     *
     * @param text The string to convert.
     * @return The string wit no special chars, which are not recognized in HTML.
     */
    public static String htmlEncodeSpecialChars(final String text) {
        final StringBuilder sb = new StringBuilder();
        for (int i=0; i<text.length(); i++) {
            final char c = text.charAt(i);
            if (c > 127) { // special chars
                sb.append("&#").append((int)c).append(";");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Returns true if one of the {@code String} elements in a {@code List}
     * matches the given {@code String}, insensitive to case.
     *
     * @param list A {@code List<String>} with elements to be tested.
     * @param str  The {@code String} to evaluate.
     *
     * @return {@code true}, if at least one element of the list matches the
     *           parameter, {@code false} otherwise.
     */
    public static boolean matchesStringfromList(final List<String> list,final String str) {
        boolean strAvailable = false;
        for (String s : list) {
            final Pattern pattern = Pattern.compile(str,Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
            final Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                strAvailable = true;
            }
        }
        return strAvailable;
    }

    /**
     * Replace all the <ns**:localPart and </ns**:localPart by <prefix:localPart and </prefix:localPart>
     *
     * @param s
     * @param localPart
     * @return
     */
    public static String replacePrefix(final String s, final String localPart, final String prefix) {

        return s.replaceAll("[a-zA-Z0-9]*:" + localPart, prefix + ":" + localPart);
    }

    /**
     * Remove all the XML namespace declaration.
     * @param xml
     * @return
     */
    public static String removeXmlns(final String xml) {
        String s = xml;
        s = s.replaceAll("xmlns=\"[^\"]*\" ", "");
        s = s.replaceAll("xmlns=\"[^\"]*\"", "");
        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\" ", "");
        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\"", "");
        return s;
    }
    /**
     * Remove the prefix on propertyName.
     * example : removePrefix(csw:GetRecords) return "GetRecords".
     */
    public static String removePrefix(String s) {
        final int i = s.indexOf(':');
        if ( i != -1) {
            s = s.substring(i + 1, s.length());
        }
        return s;
    }

    /**
     * Returns true if <code>string1</code> starts with <code>string2</code> (ignoring case).
     * @param string1 the first string
     * @param string2 the second string
     * @return true if <code>string1</code> starts with <code>string2</code>; false otherwise
     *
     * @deprecated Replaced by {@link org.geotoolkit.util.Strings#startsWith(CharSequence, CharSequence, boolean)}.
     */
    @Deprecated
    public static boolean startsWithIgnoreCase(final String string1, final String string2) {
        // this could be optimized, but anyway it doesn't seem to be a performance killer
        return string1.toUpperCase().startsWith(string2.toUpperCase());
    }

    /**
     * Returns the values of the list separated by commas.
     *
     * @param values The collection to extract values.
     * @return A string which contains values concatened with comma(s), or an empty
     *         string if the list is empty or {@code null}.
     */
    public static String toCommaSeparatedValues(final Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(Object obj : values){
            if(first){
                first = false;
            }else{
                builder.append(',');
            }
            builder.append(obj);
        }
        return builder.toString();
    }

    /**
     * Returns the values of the array separated by commas.
     *
     * @param values The array to extract values.
     * @return A string which contains values concatened with comma(s), or an empty
     *         string if the array is empty or {@code null}.
     */
    public static String toCommaSeparatedValues(final Object ... values) {
        if (values == null || values.length == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        final int size = values.length;
        for (int i=0; i<size; i++) {
            if (i>0) {
                builder.append(',');
            }
            builder.append(values[i]);
        }
        return builder.toString();
    }

    /**
     * Split a string on commas, and return it as a list.
     *
     * @param commaSeparatedString A string which contains comma(s).
     * @return A list of elements that were contained in the string separated by comma(s).
     */
    public static List<String> toStringList(final String commaSeparatedString) {
        return toStringList(commaSeparatedString, ',');
    }

    /**
     * Split a string on a special character, and return it as a list.
     *
     * @param toSplit   A string to split on a specific character.
     * @param separator The special character on which the given string will be splitted.
     * @return A list of elements that were contained in the string separated by the special
     *         character.
     *
     * @deprecated Replaced by {@link org.geotoolkit.util.Strings#split(String)}.
     */
    @Deprecated
    public static List<String> toStringList(String toSplit, final char separator) {
        if (toSplit == null) {
            return Collections.emptyList();
        }
        final List<String> strings = new ArrayList<String>();
        int last = 0;
        toSplit = toSplit.trim();
        for (int i=toSplit.indexOf(separator); i>=0; i=toSplit.indexOf(separator, i)) {
            strings.add(toSplit.substring(last, i).trim());
            last = ++i;
        }
        strings.add(toSplit.substring(last).trim());
        return strings;
    }

    /**
     * Transform an exception code into the OWS specification.
     * Example : MISSING_PARAMETER_VALUE become MissingParameterValue.
     *
     * @param code
     * @return
     */
    public static String transformCodeName(String code) {
        final StringBuilder result = new StringBuilder();
        while (code.indexOf('_') != -1) {
            final String tmp = code.substring(0, code.indexOf('_')).toLowerCase();
            result.append(firstToUpper(tmp));
            code = code.substring(code.indexOf('_') + 1, code.length());
        }
        code = code.toLowerCase();
        result.append(firstToUpper(code));
        return result.toString();
    }

    public static String toStringTree(final Object ... objects){
        return Trees.toString("", Arrays.asList(objects));
    }

}
