/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.FeatureType;
import org.geotoolkit.wfs.xml.FeatureTypeList;
import org.geotoolkit.wfs.xml.v110.OperationsType;


/**
 * <p>Java class for FeatureTypeListType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FeatureTypeListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FeatureType" type="{http://www.opengis.net/wfs/2.0}FeatureTypeType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureTypeListType", propOrder = {
    "featureType"
})
public class FeatureTypeListType implements FeatureTypeList {

    @XmlElement(name = "FeatureType", required = true)
    private List<FeatureTypeType> featureType;

    public FeatureTypeListType() {

    }

    public FeatureTypeListType(final OperationsType operations, final List<FeatureTypeType> featureType) {
        this.featureType = featureType;
    }

    /**
     * Gets the value of the featureType property.
     *
    */
    @Override
    public List<FeatureTypeType> getFeatureType() {
        if (featureType == null) {
            featureType = new ArrayList<FeatureTypeType>();
        }
        return this.featureType;
    }

    @Override
    public void addFeatureType(final FeatureType ft) {
        if (ft instanceof FeatureTypeType) {
            getFeatureType().add((FeatureTypeType)ft);
        } else if (ft != null) {
            throw new IllegalArgumentException("unexpected version of the featureType object");
        }
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof FeatureTypeListType) {
            final FeatureTypeListType that = (FeatureTypeListType) object;

            return Objects.equals(this.featureType, that.featureType);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.featureType != null ? this.featureType.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[FeatureTypeListType]\n");
        if(featureType != null) {
            s.append("featureType:\n");
            for (FeatureTypeType feat : featureType) {
                s.append(feat).append('\n');
            }
        }
        return s.toString();
    }
}
