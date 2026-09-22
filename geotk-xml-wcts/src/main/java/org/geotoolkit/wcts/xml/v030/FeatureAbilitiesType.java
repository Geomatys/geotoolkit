/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.wcts.xml.v030;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Describes the vector-feature transformation abilities of a WCTS ({@code wcts:FeatureAbilities}).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureAbilitiesType", propOrder = {"geometryType", "featureFormat"})
public class FeatureAbilitiesType {

    @XmlElement(name = "GeometryType", required = true)
    private List<GeometryTypeType> geometryType;
    @XmlElement(name = "FeatureFormat", required = true)
    private List<InputOutputFormatType> featureFormat;
    @XmlAttribute(required = true)
    private boolean remoteProperties;

    public List<GeometryTypeType> getGeometryType() {
        if (geometryType == null) {
            geometryType = new ArrayList<>();
        }
        return geometryType;
    }

    public List<InputOutputFormatType> getFeatureFormat() {
        if (featureFormat == null) {
            featureFormat = new ArrayList<>();
        }
        return featureFormat;
    }

    public boolean isRemoteProperties() {
        return remoteProperties;
    }

    public void setRemoteProperties(boolean remoteProperties) {
        this.remoteProperties = remoteProperties;
    }
}
