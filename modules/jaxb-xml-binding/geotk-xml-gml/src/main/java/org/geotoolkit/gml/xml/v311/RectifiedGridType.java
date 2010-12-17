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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.coverage.grid.RectifiedGrid;


/**
 * A rectified grid has an origin and vectors that define its post locations.
 * 
 * <p>Java class for RectifiedGridType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RectifiedGridType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}GridType">
 *       &lt;sequence>
 *         &lt;element name="origin" type="{http://www.opengis.net/gml}PointType"/>
 *         &lt;element name="offsetVector" type="{http://www.opengis.net/gml}VectorType" maxOccurs="unbounded"/>
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
@XmlType(name = "RectifiedGridType", propOrder = {
    "origin",
    "offsetVector"
})
public class RectifiedGridType extends GridType {

    @XmlElement(required = true)
    private PointType origin;
    @XmlElement(required = true)
    private List<VectorType> offsetVector;

    public RectifiedGridType() {

    }

    public RectifiedGridType(RectifiedGrid grid) {
       super(grid);
       if (grid != null) {
           origin       = new PointType(grid.getOrigin(), false);
           offsetVector = new ArrayList<VectorType>();
           
           final List<double[]> vectors = grid.getOffsetVectors();
           for (double[] vector : vectors) {
                offsetVector.add(new VectorType(vector));
           }
       }
    }

    /**
     * Gets the value of the origin property.
     */
    public PointType getOrigin() {
        return origin;
    }

    public void setOrigin(PointType origin) {
        this.origin = origin;
    }

    /**
     * Gets the value of the offsetVector property.
     */
    public List<VectorType> getOffsetVector() {
        return offsetVector;
    }

    /**
     * Sets the value of the offsetVector property
     */
    public void setOffsetVector(List<VectorType> offsetVector) {
        this.offsetVector = offsetVector;
    }

}
