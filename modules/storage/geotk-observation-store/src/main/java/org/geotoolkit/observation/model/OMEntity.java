/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.observation.model;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public enum OMEntity {

    FEATURE_OF_INTEREST("featureOfInterest"),
    OBSERVED_PROPERTY("observedProperty"),
    PROCEDURE("procedure"),
    LOCATION("location"),
    HISTORICAL_LOCATION("historicalLocation"),
    OFFERING("offering"),
    OBSERVATION("observation"),
    RESULT("result");

    private final String name;

    private OMEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OMEntity fromName(String name) {
        if (name != null) {
            switch (name) {
                case "featureOfInterest" : return FEATURE_OF_INTEREST;
                case "observedProperty" : return OBSERVED_PROPERTY;
                case "procedure" : return PROCEDURE;
                case "location" : return LOCATION;
                case "offering" : return OFFERING;
                case "observation" : return OBSERVATION;
                case "result" : return RESULT;
                default: throw new IllegalArgumentException("Unexpected entity name: " + name);
            }
        }
        throw new IllegalArgumentException("Specified name must be not null.");
    }
}
