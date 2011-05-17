/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.AbstractFeature;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * The basic feature model is given by the gml:AbstractFeatureType.
 * The content model for gml:AbstractFeatureType adds two specific properties suitable for geographic features to the content model defined in gml:AbstractGMLType.
 * The value of the gml:boundedBy property describes an envelope that encloses the entire feature instance, and is primarily useful for supporting rapid searching for features that occur in a particular location.
 * The value of the gml:location property describes the extent, position or relative location of the feature.
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractFeatureType", propOrder = {
    "srsName",
    "boundedBy",
    "location"
})
@XmlSeeAlso({
    AbstractFeatureCollectionType.class,
    BoundedFeatureType.class
})
public abstract class AbstractFeatureType extends AbstractGMLType implements AbstractFeature {

    private List<String> srsName;
    @XmlElement
    private BoundingShapeType boundedBy;
    @XmlElement
    private LocationPropertyType location;


    /**
     *  Empty constructor used by JAXB.
     */
    public AbstractFeatureType() {}

    public AbstractFeatureType(final AbstractFeature af) {
        super(af);
        if (af != null) {
            this.srsName   = af.getSrsName();
            this.boundedBy = af.getBoundedBy();
            this.location  = af.getLocation();
        }
    }

    /**
     * Build a new light "Feature"
     */
    public AbstractFeatureType(final String id, final String name, final String description) {
        super(id, name, description, null);
        this.boundedBy = new BoundingShapeType("not_bounded");
    }

    /**
     * Build a new "Feature"
     */
    public AbstractFeatureType(final String id, final String name, final String description, final ReferenceType descriptionReference,
            final BoundingShapeType boundedBy, final List<String> srsName) {
        super(id, name, description, descriptionReference);
        this.srsName = srsName;
        if (boundedBy == null) {
            this.boundedBy = new BoundingShapeType("not_bounded");
        } else {
            this.boundedBy = boundedBy;
        }
    }

    /**
     * Gets the value of the boundedBy property.
     */
    public BoundingShapeType getBoundedBy() {
        return boundedBy;
    }

    /**
     * Gets the value of the boundedBy property.
     */
    public void setBoundedBy(final BoundingShapeType boundingShape) {
        this.boundedBy = boundingShape;
    }

    /**
     * Gets the value of the boundedBy property.
     */
    public void setBoundedBy(final EnvelopeType envelope) {
        this.boundedBy = new BoundingShapeType(envelope);
    }

    /**
     * Gets the value of the location property.
     */
    public LocationPropertyType getLocation() {
        return location;
    }

    /**
     * Get srs name list
     */
    public List<String> getSrsName(){
        if (srsName == null) {
            srsName = new ArrayList<String>();
        }
        return srsName;
    }

    /**
     * Get srs name list
     */
    public void setSrsName(final List<String> srsName) {
        this.srsName = srsName;
    }

    /**
     * Get srs name list
     */
    public void getSrsName(final String name){
        if (srsName == null) {
            srsName = new ArrayList<String>();
        }
        this.srsName.add(name);
    }

    /**
     * Extends the boundingShape if the coordinate x and y are out of the previous envelope
     *
     * @param x The longitude value of the point.
     * @param y The latitude value of the point.
     */
    public void updateBoundingShape(final double x, final double y) {
        if (boundedBy != null && boundedBy.getEnvelope() != null) {
            EnvelopeType envelope = boundedBy.getEnvelope();
            if (envelope.getLowerCorner() != null) {
                Double minx = envelope.getLowerCorner().getValue().get(1);
                if (x < minx) {
                    minx = x;
                }
                Double miny = envelope.getLowerCorner().getValue().get(0);
                if (y < miny) {
                    miny = y;
                }
                envelope.getLowerCorner().setValue(Arrays.asList(miny, minx));
            }
            if (envelope.getUpperCorner() != null) {
                Double maxx = envelope.getUpperCorner().getValue().get(1);
                if (x > maxx) {
                    maxx = x;
                }
                Double maxy = envelope.getUpperCorner().getValue().get(0);
                if (y > maxy) {
                    maxy = y;
                }
                envelope.getUpperCorner().setValue(Arrays.asList(maxy, maxx));
            }
        }

    }
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractFeatureType && super.equals(object)) {
            final AbstractFeatureType that = (AbstractFeatureType) object;

            return Utilities.equals(this.boundedBy, that.boundedBy) &&
                   Utilities.equals(this.location,  that.location)  &&
                   Utilities.equals(this.srsName,   that.srsName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.boundedBy != null ? this.boundedBy.hashCode() : 0);
        hash = 23 * hash + (this.location != null ? this.location.hashCode() : 0);
        hash = 23 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (boundedBy != null)
            s.append("boundedBy:").append(boundedBy);
        if (location != null)
            s.append("location:").append(location);
        if (location != null)
            s.append("srsName:").append(srsName);

        return s.toString();
    }


}
