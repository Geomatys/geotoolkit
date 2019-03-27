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
 * <p>Classe Java pour PlatformTypeEnum.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PlatformTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Not provided"/>
 *     &lt;enumeration value="Not applicable"/>
 *     &lt;enumeration value="Aircraft"/>
 *     &lt;enumeration value="Balloons/Rockets"/>
 *     &lt;enumeration value="Earth Observation Satellites"/>
 *     &lt;enumeration value="In Situ Land-based Platforms"/>
 *     &lt;enumeration value="In Situ Ocean-based Platforms"/>
 *     &lt;enumeration value="Interplanetary Spacecraft"/>
 *     &lt;enumeration value="Maps/Charts/Photographs"/>
 *     &lt;enumeration value="Models/Analyses"/>
 *     &lt;enumeration value="Navigation Platforms"/>
 *     &lt;enumeration value="Solar/Space Observation Satellites"/>
 *     &lt;enumeration value="Space Stations/Manned Spacecraft"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "PlatformTypeEnum")
@XmlEnum
public enum PlatformTypeEnum {

    @XmlEnumValue("Not provided")
    NOT_PROVIDED("Not provided"),
    @XmlEnumValue("Not applicable")
    NOT_APPLICABLE("Not applicable"),
    @XmlEnumValue("Aircraft")
    AIRCRAFT("Aircraft"),
    @XmlEnumValue("Balloons/Rockets")
    BALLOONS_ROCKETS("Balloons/Rockets"),
    @XmlEnumValue("Earth Observation Satellites")
    EARTH_OBSERVATION_SATELLITES("Earth Observation Satellites"),
    @XmlEnumValue("In Situ Land-based Platforms")
    IN_SITU_LAND_BASED_PLATFORMS("In Situ Land-based Platforms"),
    @XmlEnumValue("In Situ Ocean-based Platforms")
    IN_SITU_OCEAN_BASED_PLATFORMS("In Situ Ocean-based Platforms"),
    @XmlEnumValue("Interplanetary Spacecraft")
    INTERPLANETARY_SPACECRAFT("Interplanetary Spacecraft"),
    @XmlEnumValue("Maps/Charts/Photographs")
    MAPS_CHARTS_PHOTOGRAPHS("Maps/Charts/Photographs"),
    @XmlEnumValue("Models/Analyses")
    MODELS_ANALYSES("Models/Analyses"),
    @XmlEnumValue("Navigation Platforms")
    NAVIGATION_PLATFORMS("Navigation Platforms"),
    @XmlEnumValue("Solar/Space Observation Satellites")
    SOLAR_SPACE_OBSERVATION_SATELLITES("Solar/Space Observation Satellites"),
    @XmlEnumValue("Space Stations/Manned Spacecraft")
    SPACE_STATIONS_MANNED_SPACECRAFT("Space Stations/Manned Spacecraft");
    private final String value;

    PlatformTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PlatformTypeEnum fromValue(String v) {
        for (PlatformTypeEnum c: PlatformTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
