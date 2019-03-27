/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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


package org.geotoolkit.dif.xml.v102;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ProductFlagEnum.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ProductFlagEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Not provided"/>
 *     &lt;enumeration value="DATA_PRODUCT_FILE"/>
 *     &lt;enumeration value="INSTRUMENT_ANCILLARY_FILE"/>
 *     &lt;enumeration value="SYSTEM/SPACECRAFT_FILE"/>
 *     &lt;enumeration value="EXTERNAL_DATA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ProductFlagEnum")
@XmlEnum
public enum ProductFlagEnum {

    @XmlEnumValue("Not provided")
    NOT_PROVIDED("Not provided"),
    DATA_PRODUCT_FILE("DATA_PRODUCT_FILE"),
    INSTRUMENT_ANCILLARY_FILE("INSTRUMENT_ANCILLARY_FILE"),
    @XmlEnumValue("SYSTEM/SPACECRAFT_FILE")
    SYSTEM_SPACECRAFT_FILE("SYSTEM/SPACECRAFT_FILE"),
    EXTERNAL_DATA("EXTERNAL_DATA");
    private final String value;

    ProductFlagEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProductFlagEnum fromValue(String v) {
        for (ProductFlagEnum c: ProductFlagEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
