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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * <p>Java class for RectifiedGridType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RectifiedGridType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}GridType">
 *       &lt;sequence>
 *         &lt;element name="origin" type="{http://www.opengis.net/gml/3.2}PointPropertyType"/>
 *         &lt;element name="offsetVector" type="{http://www.opengis.net/gml/3.2}VectorType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RectifiedGridType", propOrder = {
    "origin",
    "offsetVector"
})
@XmlRootElement(name = "RectifiedGrid")
public class RectifiedGridType extends GridType {

    @XmlElement(required = true)
    private PointType origin;
    @XmlElement(required = true)
    private List<VectorType> offsetVector;

    public RectifiedGridType() {

    }

    public RectifiedGridType(final RectifiedGrid grid) {
        this(grid, null);
    }
    
    public RectifiedGridType(final RectifiedGrid grid, final CoordinateReferenceSystem crs) {
       super(grid, crs);
       if (grid != null) {
           origin       = new PointType(grid.getOrigin(), false);
           offsetVector = new ArrayList<>();
           
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

    public void setOrigin(final PointType origin) {
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
    public void setOffsetVector(final List<VectorType> offsetVector) {
        this.offsetVector = offsetVector;
    }

}
