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
 * <p>Classe Java pour DatasetLanguageEnum.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="DatasetLanguageEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="English"/>
 *     &lt;enumeration value="Afrikaans"/>
 *     &lt;enumeration value="Arabic"/>
 *     &lt;enumeration value="Bosnian"/>
 *     &lt;enumeration value="Bulgarian"/>
 *     &lt;enumeration value="Chinese"/>
 *     &lt;enumeration value="Croatian"/>
 *     &lt;enumeration value="Czech"/>
 *     &lt;enumeration value="Danish"/>
 *     &lt;enumeration value="Dutch"/>
 *     &lt;enumeration value="Estonian"/>
 *     &lt;enumeration value="Finnish"/>
 *     &lt;enumeration value="French"/>
 *     &lt;enumeration value="German"/>
 *     &lt;enumeration value="Hebrew"/>
 *     &lt;enumeration value="Hungarian"/>
 *     &lt;enumeration value="Indonesian"/>
 *     &lt;enumeration value="Italian"/>
 *     &lt;enumeration value="Japanese"/>
 *     &lt;enumeration value="Korean"/>
 *     &lt;enumeration value="Latvian"/>
 *     &lt;enumeration value="Lithuanian"/>
 *     &lt;enumeration value="Norwegian"/>
 *     &lt;enumeration value="Polish"/>
 *     &lt;enumeration value="Portuguese"/>
 *     &lt;enumeration value="Romanian"/>
 *     &lt;enumeration value="Russian"/>
 *     &lt;enumeration value="Slovak"/>
 *     &lt;enumeration value="Spanish"/>
 *     &lt;enumeration value="Ukrainian"/>
 *     &lt;enumeration value="Vietnamese"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "DatasetLanguageEnum")
@XmlEnum
public enum DatasetLanguageEnum {

    @XmlEnumValue("English")
    ENGLISH("English"),
    @XmlEnumValue("Afrikaans")
    AFRIKAANS("Afrikaans"),
    @XmlEnumValue("Arabic")
    ARABIC("Arabic"),
    @XmlEnumValue("Bosnian")
    BOSNIAN("Bosnian"),
    @XmlEnumValue("Bulgarian")
    BULGARIAN("Bulgarian"),
    @XmlEnumValue("Chinese")
    CHINESE("Chinese"),
    @XmlEnumValue("Croatian")
    CROATIAN("Croatian"),
    @XmlEnumValue("Czech")
    CZECH("Czech"),
    @XmlEnumValue("Danish")
    DANISH("Danish"),
    @XmlEnumValue("Dutch")
    DUTCH("Dutch"),
    @XmlEnumValue("Estonian")
    ESTONIAN("Estonian"),
    @XmlEnumValue("Finnish")
    FINNISH("Finnish"),
    @XmlEnumValue("French")
    FRENCH("French"),
    @XmlEnumValue("German")
    GERMAN("German"),
    @XmlEnumValue("Hebrew")
    HEBREW("Hebrew"),
    @XmlEnumValue("Hungarian")
    HUNGARIAN("Hungarian"),
    @XmlEnumValue("Indonesian")
    INDONESIAN("Indonesian"),
    @XmlEnumValue("Italian")
    ITALIAN("Italian"),
    @XmlEnumValue("Japanese")
    JAPANESE("Japanese"),
    @XmlEnumValue("Korean")
    KOREAN("Korean"),
    @XmlEnumValue("Latvian")
    LATVIAN("Latvian"),
    @XmlEnumValue("Lithuanian")
    LITHUANIAN("Lithuanian"),
    @XmlEnumValue("Norwegian")
    NORWEGIAN("Norwegian"),
    @XmlEnumValue("Polish")
    POLISH("Polish"),
    @XmlEnumValue("Portuguese")
    PORTUGUESE("Portuguese"),
    @XmlEnumValue("Romanian")
    ROMANIAN("Romanian"),
    @XmlEnumValue("Russian")
    RUSSIAN("Russian"),
    @XmlEnumValue("Slovak")
    SLOVAK("Slovak"),
    @XmlEnumValue("Spanish")
    SPANISH("Spanish"),
    @XmlEnumValue("Ukrainian")
    UKRAINIAN("Ukrainian"),
    @XmlEnumValue("Vietnamese")
    VIETNAMESE("Vietnamese");
    private final String value;

    DatasetLanguageEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DatasetLanguageEnum fromValue(String v) {
        for (DatasetLanguageEnum c: DatasetLanguageEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
