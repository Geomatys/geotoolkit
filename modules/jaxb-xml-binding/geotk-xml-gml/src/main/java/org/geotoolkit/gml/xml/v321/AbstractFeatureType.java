/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.AbstractFeature;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * The basic feature model is given by the gml:AbstractFeatureType.
 * The content model for gml:AbstractFeatureType adds two specific properties suitable for geographic features to the content model defined in gml:AbstractGMLType. 
 * The value of the gml:boundedBy property describes an envelope that encloses the entire feature instance, and is primarily useful for supporting rapid searching for features that occur in a particular location. 
 * The value of the gml:location property describes the extent, position or relative location of the feature.
 * 
 * <p>Java class for AbstractFeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGMLType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}boundedBy" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}location" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractFeatureType", propOrder = {
    "boundedBy",
    "location"
})
@XmlSeeAlso({
    ObservationType.class,
    AbstractFeatureCollectionType.class,
    AbstractCoverageType.class,
    DynamicFeatureType.class,
    BoundedFeatureType.class
})
public abstract class AbstractFeatureType extends AbstractGMLType implements AbstractFeature {

    @XmlElement(nillable = true)
    private BoundingShapeType boundedBy;
    @XmlElementRef(name = "location", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<? extends LocationPropertyType> location;

    /**
     *  Empty constructor used by JAXB.
     */
    public AbstractFeatureType() {}

    public AbstractFeatureType(final AbstractFeature af) {
        super(af);
        if (af != null) {
            if (af.getBoundedBy() != null) {
                this.boundedBy = new BoundingShapeType(af.getBoundedBy());
            }
            if (af.getLocation() != null) {
                final ObjectFactory factory = new ObjectFactory();
                if (af.getLocation() instanceof LocationPropertyType) {
                    this.location  = factory.createLocation((LocationPropertyType)af.getLocation());
                } else if (af.getLocation() instanceof PriorityLocationPropertyType) {
                    this.location  = factory.createPriorityLocation((PriorityLocationPropertyType)af.getLocation());
                } else  {
                    throw new IllegalArgumentException("LocationProperty clone not implemented yet");
                }
            }
        }
    }

    /**
     * Build a new light "Feature"
     */
    public AbstractFeatureType(final String id, final String name, final String description) {
        super(id, name, description, null);
        //this.boundedBy = new BoundingShapeType("not_bounded"); not mandatory
    }

    /**
     * Build a new "Feature"
     */
    public AbstractFeatureType(final String id, final String name, final String description, final ReferenceType descriptionReference,
            final BoundingShapeType boundedBy) {
        super(id, name, description, descriptionReference);
        if (boundedBy == null) {
            this.boundedBy = new BoundingShapeType("not_bounded");
        } else {
            this.boundedBy = boundedBy;
        }
    }

    /**
     * Gets the value of the boundedBy property.
     * 
     * @return
     *     possible object is
     *     {@link BoundingShapeType }
     *     
     */
    @Override
    public BoundingShapeType getBoundedBy() {
        return boundedBy;
    }

    /**
     * Sets the value of the boundedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoundingShapeType }
     *     
     */
    public void setBoundedBy(BoundingShapeType value) {
        this.boundedBy = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LocationPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PriorityLocationPropertyType }{@code >}
     *     
     */
    public JAXBElement<? extends LocationPropertyType> getjbLocation() {
        return location;
    }

    @Override
    public LocationPropertyType getLocation() {
        if (location != null) {
            return location.getValue();
        }
        return null;
    }

    
    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LocationPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PriorityLocationPropertyType }{@code >}
     *     
     */
    public void setLocation(JAXBElement<? extends LocationPropertyType> value) {
        this.location = ((JAXBElement<? extends LocationPropertyType> ) value);
    }

    @Override
    public List<String> getSrsName(){
        return new ArrayList<String>();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (location != null) {
            sb.append("location:\n").append(location.getValue()).append('\n');
        }
        if (boundedBy != null) {
            sb.append("boundedBy: ").append(boundedBy).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractFeatureType && super.equals(object, mode)) {
            final AbstractFeatureType that = (AbstractFeatureType) object;

            boolean loc = false;
            if (this.location != null && that.location != null) {
                loc = Utilities.equals(this.location.getValue(), that.location.getValue());
            } else if (this.location == null && that.location == null) {
                loc = true;
            }
            return Utilities.equals(this.boundedBy,    that.boundedBy)    &&
                   loc;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + super.hashCode();
        hash = 83 * hash + (this.boundedBy != null ? this.boundedBy.hashCode() : 0);
        hash = 83 * hash + (this.location != null ? this.location.hashCode() : 0);
        return hash;
    }
}
