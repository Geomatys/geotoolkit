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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.Grid;
import org.opengis.referencing.cs.CoordinateSystem;


/**
 * Implicitly defines an unrectified grid, which is a network composed of two or more sets of equally spaced parallel lines in which the members of each set intersect the members of the other sets at right angles. This profile does not extend AbstractGeometryType, so it defines the srsName attribute.
 *
 * <p>Java class for GridType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GridType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;element name="limits" type="{http://www.opengis.net/gml}GridLimitsType"/>
 *         &lt;element name="axisName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="dimension" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridType", propOrder = {
    "limits",
    "axisName"
})
@XmlSeeAlso({
    RectifiedGridType.class
})
public class GridType extends AbstractGeometryType {

    @XmlElement(required = true)
    private GridLimitsType limits;
    @XmlElement(required = true)
    private List<String> axisName;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
    private Integer dimension;

    /**
     * Empty constructor used by JAXB
     */
    GridType() {
    }

    /**
     * Build a new GridType.
     */
    public GridType(final GridLimitsType limits, final List<String> axisName) {
        this.axisName = axisName;
        if (axisName != null) {
            dimension = axisName.size();
        }
        this.limits = limits;
    }

    public GridType(final Grid grid) {
        if (grid != null) {
            this.axisName  = grid.getAxisNames();
            this.dimension = grid.getDimension();
            this.limits    = new GridLimitsType(grid.getExtent());
        }
    }

    public GridType(final GridGeometry gg) {
        if (gg != null) {
            this.dimension = gg.getDimension();
            this.limits = new GridLimitsType(gg.getExtent());
            if (gg.isDefined(GridGeometry.CRS)) {
                CoordinateSystem cs = gg.getCoordinateReferenceSystem().getCoordinateSystem();
                this.axisName = new ArrayList<>();
                for (int i = 0; i < cs.getDimension(); i++) {
                    axisName.add(cs.getAxis(i).getAbbreviation());
                }
            }
        }
    }

    /**
     * Gets the value of the limits property.
     */
    public GridLimitsType getLimits() {
        return limits;
    }

    /**
     * Sets the value of the limits property.
     */
    public void setLimits(final GridLimitsType limits) {
        this.limits = limits;
    }

    /**
     * Gets the value of the axisName property.
     */
    public List<String> getAxisName() {
        if (axisName == null) {
            axisName = new ArrayList<String>();
        }
        return axisName;
    }

    /**
     * Sets the value of the axisName property.
     */
    public void setAxisName(final List<String> axisName) {
        this.axisName = axisName;
    }

    /**
     * Gets the value of the dimension property.
     */
    public Integer getDimension() {
        return dimension;
    }

    /**
     * Gets the value of the dimension property.
     */
    public void setDimension(final Integer dimension) {
        this.dimension = dimension;
    }
}
