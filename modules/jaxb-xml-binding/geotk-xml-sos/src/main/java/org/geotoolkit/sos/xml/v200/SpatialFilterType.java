/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.BBOXType;
import org.geotoolkit.ogc.xml.v200.BeyondType;
import org.geotoolkit.ogc.xml.v200.ContainsType;
import org.geotoolkit.ogc.xml.v200.CrossesType;
import org.geotoolkit.ogc.xml.v200.DWithinType;
import org.geotoolkit.ogc.xml.v200.DisjointType;
import org.geotoolkit.ogc.xml.v200.EqualsType;
import org.geotoolkit.ogc.xml.v200.IntersectsType;
import org.geotoolkit.ogc.xml.v200.OverlapsType;
import org.geotoolkit.ogc.xml.v200.SpatialOpsType;
import org.geotoolkit.ogc.xml.v200.TouchesType;
import org.geotoolkit.ogc.xml.v200.WithinType;
import org.opengis.filter.Filter;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}spatialOps"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spatialFilterType", propOrder = {
    "spatialOps"
})
public class SpatialFilterType {

    // enumerate the classes instead of using @XmlElementRefs because of an issue with JAX-WS
    @XmlElements({
        @XmlElement(name = "Contains", namespace = "http://www.opengis.net/fes/2.0", type = ContainsType.class),
        @XmlElement(name = "BBOX", namespace = "http://www.opengis.net/fes/2.0", type = BBOXType.class),
        @XmlElement(name = "Beyond", namespace = "http://www.opengis.net/fes/2.0", type = BeyondType.class),
        @XmlElement(name = "DWithin", namespace = "http://www.opengis.net/fes/2.0", type = DWithinType.class),
        @XmlElement(name = "Within", namespace = "http://www.opengis.net/fes/2.0", type = WithinType.class),
        @XmlElement(name = "Touches", namespace = "http://www.opengis.net/fes/2.0", type = TouchesType.class),
        @XmlElement(name = "Equals", namespace = "http://www.opengis.net/fes/2.0", type = EqualsType.class),
        @XmlElement(name = "Disjoint", namespace = "http://www.opengis.net/fes/2.0", type = DisjointType.class),
        @XmlElement(name = "Overlaps", namespace = "http://www.opengis.net/fes/2.0", type = OverlapsType.class),
        @XmlElement(name = "Crosses", namespace = "http://www.opengis.net/fes/2.0", type = CrossesType.class),
        @XmlElement(name = "Intersects", namespace = "http://www.opengis.net/fes/2.0", type = IntersectsType.class)
    })
    private SpatialOpsType spatialOps;

    public SpatialFilterType() {
    }

    public SpatialFilterType(final SpatialOpsType filter) {
        if (filter != null) {
            this.spatialOps = filter;
        }
    }
    
    public SpatialFilterType(final Filter filter) {
        if (filter instanceof SpatialOpsType) {
            this.spatialOps = (SpatialOpsType)filter;
        } else if (filter != null) {
            throw new IllegalArgumentException("Unexpected spatial filter type:" + filter);
        }
    }

    /**
     * Gets the value of the spatialOps property.
     *
     * @return possible object is      {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *
     */
    public SpatialOpsType getSpatialOps() {
        return spatialOps;
    }

    /**
     * Sets the value of the spatialOps property.
     *
     * @param value allowed object is      {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     *
     */
    public void setSpatialOps(final SpatialOpsType value) {
        this.spatialOps = value;
    }
}
