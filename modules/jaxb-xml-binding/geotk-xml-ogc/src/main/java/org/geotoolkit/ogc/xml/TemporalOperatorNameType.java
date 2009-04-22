/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p> A enumeration of temporal operator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TemporalOperatorNameType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TM_After"/>
 *     &lt;enumeration value="TM_Before"/>
 *     &lt;enumeration value="TM_Begins"/>
 *     &lt;enumeration value="TM_BegunBy"/>
 *     &lt;enumeration value="TM_Contains"/>
 *     &lt;enumeration value="TM_During"/>
 *     &lt;enumeration value="TM_Equals"/>
 *     &lt;enumeration value="TM_Overlaps"/>
 *     &lt;enumeration value="TM_Meets"/>
 *     &lt;enumeration value="TM_OverlappedBy"/>
 *     &lt;enumeration value="TM_MetBy"/>
 *     &lt;enumeration value="TM_EndedBy"/>
 *     &lt;enumeration value="TM_Ends"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TemporalOperatorNameType")
@XmlEnum
public enum TemporalOperatorNameType {

    @XmlEnumValue("TM_After")
    TM_AFTER("TM_After"),
    @XmlEnumValue("TM_Before")
    TM_BEFORE("TM_Before"),
    @XmlEnumValue("TM_Begins")
    TM_BEGINS("TM_Begins"),
    @XmlEnumValue("TM_BegunBy")
    TM_BEGUN_BY("TM_BegunBy"),
    @XmlEnumValue("TM_Contains")
    TM_CONTAINS("TM_Contains"),
    @XmlEnumValue("TM_During")
    TM_DURING("TM_During"),
    @XmlEnumValue("TM_Equals")
    TM_EQUALS("TM_Equals"),
    @XmlEnumValue("TM_Overlaps")
    TM_OVERLAPS("TM_Overlaps"),
    @XmlEnumValue("TM_Meets")
    TM_MEETS("TM_Meets"),
    @XmlEnumValue("TM_OverlappedBy")
    TM_OVERLAPPED_BY("TM_OverlappedBy"),
    @XmlEnumValue("TM_MetBy")
    TM_MET_BY("TM_MetBy"),
    @XmlEnumValue("TM_EndedBy")
    TM_ENDED_BY("TM_EndedBy"),
    @XmlEnumValue("TM_Ends")
    TM_ENDS("TM_Ends");
    private final String value;

    TemporalOperatorNameType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TemporalOperatorNameType fromValue(String v) {
        for (TemporalOperatorNameType c: TemporalOperatorNameType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
