/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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
package org.geotoolkit.data.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.apache.sis.util.Static;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.apache.sis.internal.feature.AttributeConvention;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CSVUtils extends Static {

    public static final String COMMENT_STRING = "#";

    /**
     * Read lines from input {@link java.util.Scanner} until it finds a non-commented line, then send it back.
     * @param source The scanner to read lines from.
     * @return The first non-commented line found, or null if scanner is empty or contains only comments.
     */
    static String getNextLine(final Scanner source) {
        String line;
        while (source.hasNextLine()) {
            line = source.nextLine();
            if (line.startsWith(COMMENT_STRING)) {
                continue;
            } else {
                final int commentIndex = line.indexOf(COMMENT_STRING);
                if (commentIndex < 0) {
                    return line;
                } else {
                    return line.substring(0, commentIndex);
                }
            }
        }
        return null;
    }


    static Feature defaultFeature(final FeatureType type, final String id){
        final Feature feature = type.newInstance();
        feature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), id);
        return feature;
    }

    /**
     *
     * @param toSplit
     * @param separator
     * @return
     */
    static List<String> toStringList(Scanner scanner, String toSplit, final char separator) {
        if (toSplit == null) {
            return Collections.emptyList();
        }
        final List<String> strings = new ArrayList<>();
        int last = 0;
        toSplit = toSplit.trim();

        String currentValue = null;
        boolean inEscape = false;
        for(;;){
            if(inEscape){

                int end = toSplit.indexOf('\"',last);
                while(end>=0){
                    if(end>=toSplit.length()){
                        //found escape end
                        break;
                    }else if(toSplit.charAt(end+1)=='\"'){
                        //double quote, not an escape
                        end = toSplit.indexOf('\"',end+2);
                    }else{
                        break;
                    }
                }

                if(end>=0){
                    currentValue += toSplit.substring(last, end);
                    currentValue = currentValue.replace("\"\"", "\"");
                    strings.add(currentValue);
                    last = end+2;
                    inEscape = false;
                }else{
                    currentValue += toSplit.substring(last);
                    currentValue += "\n";
                    last = 0;
                    toSplit = scanner.nextLine();
                }

            }else if(last>=toSplit.length()){
                break;
            }else if(toSplit.charAt(last)=='\"'){
                inEscape = true;
                last++;
                currentValue = "";
            }else{
                final int end = toSplit.indexOf(separator, last);
                if(end>=0){
                    strings.add(toSplit.substring(last, end).trim());
                    last = end+1;
                }else{
                    strings.add(toSplit.substring(last).trim());
                    break;
                }
            }
        }
        return strings;
    }


}
