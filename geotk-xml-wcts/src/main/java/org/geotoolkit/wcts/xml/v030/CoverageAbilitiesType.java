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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Describes the coverage transformation abilities of a WCTS ({@code wcts:CoverageAbilities}).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageAbilitiesType", propOrder = {"coverageType", "coverageFormat", "interpolationMethod"})
public class CoverageAbilitiesType {

    @XmlElement(name = "CoverageType", required = true)
    private List<CoverageTypeType> coverageType;
    @XmlElement(name = "CoverageFormat", required = true)
    private List<InputOutputFormatType> coverageFormat;
    @XmlElement(name = "InterpolationMethod", required = true, namespace = "http://www.opengeospatial.net/ows")
    private List<InterpolationMethodType> interpolationMethod;

    public List<CoverageTypeType> getCoverageType() {
        if (coverageType == null) {
            coverageType = new ArrayList<>();
        }
        return coverageType;
    }

    public List<InputOutputFormatType> getCoverageFormat() {
        if (coverageFormat == null) {
            coverageFormat = new ArrayList<>();
        }
        return coverageFormat;
    }

    public List<InterpolationMethodType> getInterpolationMethod() {
        if (interpolationMethod == null) {
            interpolationMethod = new ArrayList<>();
        }
        return interpolationMethod;
    }
}
