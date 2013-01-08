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
package org.geotoolkit.gml.xml.v212;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *  A Polygon is defined by an outer boundary and zero or more inner 
 *  boundaries which are in turn defined by LinearRings.
 *       
 * 
 * <p>Java class for PolygonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolygonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}outerBoundaryIs"/>
 *         &lt;element ref="{http://www.opengis.net/gml}innerBoundaryIs" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolygonType", propOrder = {
    "outerBoundaryIs",
    "innerBoundaryIs"
})
public class PolygonType extends AbstractGeometryType {

    @XmlElement(required = true)
    private LinearRingMemberType outerBoundaryIs;
    private List<LinearRingMemberType> innerBoundaryIs;

    public PolygonType() {
        
    }
    
    public PolygonType(final PolygonType that) {
        super(that);
        if (that != null) {
            if (that.outerBoundaryIs != null) {
                this.outerBoundaryIs = new LinearRingMemberType(that.outerBoundaryIs);
            }
            if (that.innerBoundaryIs != null) {
                this.innerBoundaryIs = new ArrayList<LinearRingMemberType>();
                for (LinearRingMemberType lrm : that.innerBoundaryIs) {
                    this.innerBoundaryIs.add(new LinearRingMemberType(lrm));
                }
            }
        }
    }
    /**
     * Gets the value of the outerBoundaryIs property.
     * 
     * @return
     *     possible object is
     *     {@link LinearRingMemberType }
     *     
     */
    public LinearRingMemberType getOuterBoundaryIs() {
        return outerBoundaryIs;
    }

    /**
     * Sets the value of the outerBoundaryIs property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinearRingMemberType }
     *     
     */
    public void setOuterBoundaryIs(final LinearRingMemberType value) {
        this.outerBoundaryIs = value;
    }

    /**
     * Gets the value of the innerBoundaryIs property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link LinearRingMemberType }
     * 
     * 
     */
    public List<LinearRingMemberType> getInnerBoundaryIs() {
        if (innerBoundaryIs == null) {
            innerBoundaryIs = new ArrayList<LinearRingMemberType>();
        }
        return this.innerBoundaryIs;
    }

    @Override
    public AbstractGeometryType getClone() {
        return new PolygonType(this);
    }
}
