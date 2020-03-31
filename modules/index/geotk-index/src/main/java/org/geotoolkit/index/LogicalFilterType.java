/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007-2016, Geomatys
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
package org.geotoolkit.index;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public enum LogicalFilterType {

    AND,
    OR,
    NOT,
    XOR,
    DEFAULT;

    /**
     * Return the filterName correspounding to the specified flag.
     *
     * @param flag an int flag.
     *
     * @return A filter name : And, Or, Xor or Not.
     */
    public String valueOf() {
        switch (this) {
            case AND:
                return "AND";
            case OR:
                return "OR";
            case NOT:
                return "NOT";
            case XOR:
                return "XOR";
            default:
               return "unknow";
        }
    }

    /**
     * Return the flag correspounding to the specified filterName.
     *
     * @param filterName A filter name : And, Or, Xor or Not.
     *
     * @return an int flag.
     */
    public static LogicalFilterType valueOfIgnoreCase(final String filterName) {

        if (filterName.equalsIgnoreCase("AND")) {
            return AND;
        } else if (filterName.equalsIgnoreCase("OR")) {
            return OR;
        } else if (filterName.equalsIgnoreCase("XOR")) {
            return XOR;
        } else if (filterName.equalsIgnoreCase("NOT")) {
            return NOT;
        } else {
            return DEFAULT;
        }
    }

}
