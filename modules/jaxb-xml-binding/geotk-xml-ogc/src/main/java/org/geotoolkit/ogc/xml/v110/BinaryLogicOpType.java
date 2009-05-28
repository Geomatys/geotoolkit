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
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BinaryLogicOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryLogicOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}LogicOpsType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="2">
 *         &lt;element ref="{http://www.opengis.net/ogc}comparisonOps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}logicOps"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryLogicOpType", propOrder = {
    "comparisonOpsOrSpatialOpsOrLogicOps"
})
public class BinaryLogicOpType
    extends LogicOpsType
{

    @XmlElementRefs({
        @XmlElementRef(name = "comparisonOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "logicOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class),
        @XmlElementRef(name = "spatialOps", namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> comparisonOpsOrSpatialOpsOrLogicOps;

    /**
     * Gets the value of the comparisonOpsOrSpatialOpsOrLogicOps property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the comparisonOpsOrSpatialOpsOrLogicOps property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComparisonOpsOrSpatialOpsOrLogicOps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyIsBetweenType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link LogicOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyIsNullType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryLogicOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     * {@link JAXBElement }{@code <}{@link PropertyIsLikeType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BBOXType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link ComparisonOpsType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getComparisonOpsOrSpatialOpsOrLogicOps() {
        if (comparisonOpsOrSpatialOpsOrLogicOps == null) {
            comparisonOpsOrSpatialOpsOrLogicOps = new ArrayList<JAXBElement<?>>();
        }
        return this.comparisonOpsOrSpatialOpsOrLogicOps;
    }

}
