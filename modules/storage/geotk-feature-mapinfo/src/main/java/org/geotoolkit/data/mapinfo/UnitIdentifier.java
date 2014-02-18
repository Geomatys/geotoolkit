/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class which binds mapinfo unit codes with Geotk unit constants.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 07/03/13
 */
public class UnitIdentifier {

    private static final HashMap<Integer, Unit> UNIT_TABLE = new HashMap<Integer, Unit>();

    public static Unit getUnitFromCode(String code) {
        Unit result = null;
        Matcher codeMatch = Pattern.compile("\\d+").matcher(code);
        if(codeMatch.find()) {
            final int intCode = Integer.decode(codeMatch.group());
            result = UNIT_TABLE.get(intCode);
        } else {
            Matcher strMatch = Pattern.compile("\\w+").matcher(code);
            if(strMatch.find()) {
                result = Unit.valueOf(strMatch.group());
            }
        }
        return result;
    }
    /**
     * Search an unit for the given unit code.
     * @param code The integer which is mapinfo unit code.
     * @return Return the {@link Unit} object pointed by given mapInfo code, or null if we can't find equivalent.
     */
    public static Unit getUnitFromCode(int code) {
        return UNIT_TABLE.get(code);
    }

    /**
     * Search a code for the given unit.
     * @param source The {@link Unit} to get map
     * @return
     */
    public static int getCodeFromUnit(Unit source) {
        for(Map.Entry<Integer, Unit> pair : UNIT_TABLE.entrySet()) {
            if(pair.getValue().equals(source)) {
                return pair.getKey();
            }
        }
        return -1;
    }

    static {
        UNIT_TABLE.put(0, NonSI.MILE);
        UNIT_TABLE.put(1, SI.KILOMETRE);
        UNIT_TABLE.put(2, NonSI.INCH);
        UNIT_TABLE.put(3, NonSI.FOOT);
        UNIT_TABLE.put(4, NonSI.YARD);
        UNIT_TABLE.put(5, SI.MILLIMETRE);
        UNIT_TABLE.put(6, SI.CENTIMETRE);
        UNIT_TABLE.put(7, SI.METRE);
        UNIT_TABLE.put(8, NonSI.FOOT_SURVEY_US);
        UNIT_TABLE.put(9, NonSI.NAUTICAL_MILE);
        // 30
        // 31
        // 32
    }
}
