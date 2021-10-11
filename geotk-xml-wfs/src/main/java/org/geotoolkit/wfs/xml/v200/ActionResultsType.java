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


/**
 * <p>Java class for ActionResultsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionResultsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Feature" type="{http://www.opengis.net/wfs/2.0}CreatedOrModifiedFeatureType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionResultsType", propOrder = {
    "feature"
})
public class ActionResultsType {

    @XmlElement(name = "Feature", required = true)
    private List<CreatedOrModifiedFeatureType> feature;

    public ActionResultsType() {

    }

    public ActionResultsType(final List<CreatedOrModifiedFeatureType> feature) {
        this.feature = feature;
    }

    /**
     * Gets the value of the feature property.
     *
     */
    public List<CreatedOrModifiedFeatureType> getFeature() {
        if (feature == null) {
            feature = new ArrayList<CreatedOrModifiedFeatureType>();
        }
        return this.feature;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ActionResultsType]\n");
        if (feature != null) {
           sb.append("feature: ").append('\n');
           for (CreatedOrModifiedFeatureType a : feature) {
                sb.append(a).append('\n');
           }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ActionResultsType) {
            final ActionResultsType that = (ActionResultsType) object;
            return Objects.equals(this.feature,   that.feature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.feature != null ? this.feature.hashCode() : 0);
        return hash;
    }

}
