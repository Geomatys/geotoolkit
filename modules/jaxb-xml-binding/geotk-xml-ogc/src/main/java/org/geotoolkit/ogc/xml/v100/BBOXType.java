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
package org.geotoolkit.ogc.xml.v100;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v212.BoxType;
import org.geotoolkit.gml.xml.v212.CoordType;


/**
 * <p>Java class for BBOXType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BBOXType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}SpatialOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;element ref="{http://www.opengis.net/gml}Box"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBOXType", propOrder = {
    "propertyName",
    "box"
})
public class BBOXType extends SpatialOpsType {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElement(name = "Box", namespace = "http://www.opengis.net/gml", required = true)
    private BoxType box;

    /**
     * An empty constructor used by JAXB
     */
    public BBOXType() {

    }

    /**
     * build a new BBox with an envelope.
     */
    public BBOXType(final String propertyName, final double minx, final double miny, final double maxx, final double maxy, final String srs) {
        this.propertyName = new PropertyNameType(propertyName);
        final CoordType lower = new CoordType(minx, miny);
        final CoordType upper = new CoordType(maxx, maxy);
        this.box = new BoxType(Arrays.asList(lower, upper), srs);

    }

    public BBOXType(final BBOXType that) {
        if (that != null) {
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
            if (that.box != null) {
                this.box = new BoxType(that.box);
            }
        }
    }
    /**
     * Gets the value of the propertyName property.
     *
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     *
     */
    public void setPropertyName(final PropertyNameType value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the box property.
     *
     */
    public BoxType getBox() {
        return box;
    }

    /**
     * Sets the value of the box property.
     *
     */
    public void setBox(final BoxType value) {
        this.box = value;
    }

    @Override
    public SpatialOpsType getClone() {
        return new BBOXType(this);
    }
}
