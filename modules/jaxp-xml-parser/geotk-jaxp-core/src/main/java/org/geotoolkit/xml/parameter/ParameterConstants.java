/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.xml.parameter;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
final class ParameterConstants {

    // URI for parameter documents
    public static final String URI_PARAMETER = "http://www.geotoolkit.org/parameter";
    public static final String PREFIX_PARAMETER = "param";

    // SCHEMA
    public static final String URI_XSD = "http://www.w3.org/2001/XMLSchema";

    public static final String TAG_XSD_ANNOTATION = "annotation";
    public static final String TAG_XSD_APP_INFO = "appinfo";
    public static final String TAG_XSD_ATTRIBUTE = "attribute";
    public static final String TAG_XSD_COMPLEX_TYPE = "complexType";
    public static final String TAG_XSD_DOCUMENTATION = "documentation";
    public static final String TAG_XSD_ELEMENT = "element";
    public static final String TAG_XSD_ENUMERATION = "enumeration";
    public static final String TAG_XSD_EXTENSION = "extension";
    public static final String TAG_XSD_MAX_INCLUSIVE = "maxInclusive";
    public static final String TAG_XSD_MIN_INCLUSIVE = "minInclusive";
    public static final String TAG_XSD_PATTERN = "pattern";
    public static final String TAG_XSD_RESTRICTION = "restriction";
    public static final String TAG_XSD_SCHEMA = "schema";
    public static final String TAG_XSD_SEQUENCE = "sequence";
    public static final String TAG_XSD_SIMPLE_CONTENT = "simpleContent";
    public static final String TAG_XSD_SIMPLE_TYPE = "simpleType";

    public static final String ATT_XSD_BASE = "base";
    public static final String ATT_XSD_DEFAULT = "default";
    public static final String ATT_XSD_MAX_OCCURS = "maxOccurs";
    public static final String ATT_XSD_MIN_OCCURS = "minOccurs";
    public static final String ATT_XSD_NAME = "name";
    public static final String ATT_XSD_TYPE = "type";
    public static final String ATT_XSD_VALUE = "value";

    public static final String TYPE_XSD_STRING = "string";
    public static final String TYPE_XSD_BOOLEAN = "boolean";
    public static final String TYPE_XSD_INT = "int";
    public static final String TYPE_XSD_LONG = "long";
    public static final String TYPE_XSD_FLOAT = "float";
    public static final String TYPE_XSD_DOUBLE = "double";

    public static final String VAL_XSD_UNBOUNDED = "unbounded";


    /**
     * <p>This enumeration provides status for parameter values as default and/or
     * maximum or minimum.</p>
     */
    public enum ValueType {

        MAX("maximumValue"),
        MIN("minimumValue");

        private final String type;

        private ValueType(String type) {
            this.type = type;
        }

        public String getType(){return this.type;}

        public static ValueType transform(String type){
            for(ValueType tv : ValueType.values()){
                if(tv.getType().equals(type)) return tv;
            }
            return null;
        }
    }

    /**
     * <p>This enumeration provides a topology for a values set which can be
     * an interval or a set of discrete values.</p>
     */
    public enum ValuesTopology {

        INTERVAL("interval"),
        DISCRETE("discrete");

        private final String topology;

        private ValuesTopology(String topology) {
            this.topology = topology;
        }

        public String getTopology(){return this.topology;}

        public static ValuesTopology transform(String topology){
            for(ValuesTopology to : ValuesTopology.values()){
                if(to.getTopology().equals(topology)) return to;
            }
            return null;
        }
    }

    private ParameterConstants(){}
}
