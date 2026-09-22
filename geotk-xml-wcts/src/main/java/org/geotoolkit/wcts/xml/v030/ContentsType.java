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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Describes the transformations, methods and CRSs a WCTS knows about ({@code wcts:Contents}).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentsType", propOrder = {
    "transformation", "method", "sourceCRS", "targetCRS", "featureAbilities", "coverageAbilities"
})
public class ContentsType {

    @XmlElement(name = "Transformation")
    private List<TransformationRefType> transformation;
    @XmlElement(name = "Method")
    private List<MethodRefType> method;
    @XmlElement(name = "SourceCRS", required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> sourceCRS;
    @XmlElement(name = "TargetCRS", required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> targetCRS;
    @XmlElement(name = "FeatureAbilities")
    private FeatureAbilitiesType featureAbilities;
    @XmlElement(name = "CoverageAbilities")
    private CoverageAbilitiesType coverageAbilities;
    @XmlAttribute(required = true)
    private boolean userDefinedCRSs;

    public List<TransformationRefType> getTransformation() {
        if (transformation == null) {
            transformation = new ArrayList<>();
        }
        return transformation;
    }

    public List<MethodRefType> getMethod() {
        if (method == null) {
            method = new ArrayList<>();
        }
        return method;
    }

    public List<String> getSourceCRS() {
        if (sourceCRS == null) {
            sourceCRS = new ArrayList<>();
        }
        return sourceCRS;
    }

    public List<String> getTargetCRS() {
        if (targetCRS == null) {
            targetCRS = new ArrayList<>();
        }
        return targetCRS;
    }

    public FeatureAbilitiesType getFeatureAbilities() {
        return featureAbilities;
    }

    public void setFeatureAbilities(FeatureAbilitiesType featureAbilities) {
        this.featureAbilities = featureAbilities;
    }

    public CoverageAbilitiesType getCoverageAbilities() {
        return coverageAbilities;
    }

    public void setCoverageAbilities(CoverageAbilitiesType coverageAbilities) {
        this.coverageAbilities = coverageAbilities;
    }

    public boolean isUserDefinedCRSs() {
        return userDefinedCRSs;
    }

    public void setUserDefinedCRSs(boolean userDefinedCRSs) {
        this.userDefinedCRSs = userDefinedCRSs;
    }
}
