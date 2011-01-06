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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    
    private StringUtilities() {}

    /*
     * Encode the specified string with MD5 algorithm.
     *
     * @param key :  the string to encode.
     * @return the value (string) hexadecimal on 32 bits
     */
    public static String MD5encode(String key) {

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
     */
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
     */
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
     * @param list A list of String.
     * @param str The value searched.
     * @return
     */
    public static boolean containsIgnoreCase(List<String> list, String str) {
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
     */
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
    public static int[] getIndexes(String s, char occ) {
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
     * This method returns a number of occurences occ in the string s.
     *
     * @param s : String to search in
     * @param occ : Occurence to search
     * @return number of occurence
     *
     * @deprecated Moved to {@link org.geotoolkit.util.Strings#count(String, String)}.
     */
    @Deprecated
    public static int getOccurence(String s, String occ) {
        int cnt = 0;
        int pos = s.indexOf(occ);
        for(; pos >= 0; pos = s.indexOf(occ, pos+1)){
            cnt++;
        }
        return cnt;
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
    public static String removeXmlns(String xml) {
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
     * This method sort alphabeticely a list of String
     *
     * @param toSort
     * @return
     *
     * @deprecated Use {@link java.util.Collections#sort(List)} instead.
     */
    @Deprecated
    public static List<String> sortStringList(List<String> toSort) {
        final int elements = toSort.size();
        for (int i = (elements - 1); i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (toSort.get(j).compareTo(toSort.get(j + 1)) > 0) {
                    final String inter = toSort.get(j);
                    toSort.set(j, toSort.get(j + 1));
                    toSort.set(j + 1,inter);
                }
            }
        }
        return toSort;
    }

    /**
     * Returns true if <code>string1</code> starts with <code>string2</code> (ignoring case).
     * @param string1 the first string
     * @param string2 the second string
     * @return true if <code>string1</code> starts with <code>string2</code>; false otherwise
     */
    public static boolean startsWithIgnoreCase(String string1, String string2) {
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
    public static List<String> toStringList(String commaSeparatedString) {
        return toStringList(commaSeparatedString, ',');
    }

    /**
     * Split a string on a special character, and return it as a list.
     *
     * @param toSplit   A string to split on a specific character.
     * @param separator The special character on which the given string will be splitted.
     * @return A list of elements that were contained in the string separated by the special
     *         character.
     */
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

    public static String toStringTree(Object ... objects){
        return toStringTree(Arrays.asList(objects));
    }

    public static String toStringTree(Collection<?> objects){
        final StringBuilder sb = new StringBuilder();

        final int size = objects.size();

        final Iterator ite = objects.iterator();
        int i=1;
        while(ite.hasNext()){
            String sub = ite.next().toString();

            if(i==size){
                sb.append(TREE_END);
                //move text to the right
                sub = sub.replaceAll("\n", "\n"+TREE_BLANK);
                sb.append(sub);
            }else{
                sb.append(TREE_CROSS);
                //move text to the right
                sub = sub.replaceAll("\n", "\n"+TREE_LINE);
                sb.append(sub);
                sb.append('\n');
            }
            i++;
        }
        return sb.toString();
    }

}
