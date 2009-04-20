
package org.geotoolkit.internal.jaxb.v100.gml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NullType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NullType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="inapplicable"/>
 *     &lt;enumeration value="unknown"/>
 *     &lt;enumeration value="unavailable"/>
 *     &lt;enumeration value="missing"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NullType")
@XmlEnum
public enum NullType {

    @XmlEnumValue("inapplicable")
    INAPPLICABLE("inapplicable"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown"),
    @XmlEnumValue("unavailable")
    UNAVAILABLE("unavailable"),
    @XmlEnumValue("missing")
    MISSING("missing");
    private final String value;

    NullType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NullType fromValue(String v) {
        for (NullType c: NullType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
