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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * gml:ConcatenatedOperation is an ordered sequence of two or more coordinate operations. This sequence of operations is constrained by the requirement that the source coordinate reference system of step (n+1) must be the same as the target coordinate reference system of step (n). The source coordinate reference system of the first step and the target coordinate reference system of the last step are the source and target coordinate reference system associated with the concatenated operation. Instead of a forward operation, an inverse operation may be used for one or more of the operation steps mentioned above, if the inverse operation is uniquely defined by the forward operation.
 * The gml:coordOperation property elements are an ordered sequence of associations to the two or more operations used by this concatenated operation. The AggregationAttributeGroup should be used to specify that the coordOperation associations are ordered.
 *
 * <p>Java class for ConcatenatedOperationType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConcatenatedOperationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractCoordinateOperationType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}coordOperation" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AggregationAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(/*name = "ConcatenatedOperationType",*/ propOrder = {
    "coordOperation"
})
public class ConcatenatedOperationType
    extends AbstractCoordinateOperationType
{

    @XmlElementRef(name = "coordOperation", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private List<JAXBElement<CoordinateOperationPropertyType>> coordOperation;
    @XmlAttribute
    private AggregationType aggregationType;

    /**
     * Gets the value of the coordOperation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coordOperation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoordOperation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link CoordinateOperationPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link CoordinateOperationPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link CoordinateOperationPropertyType }{@code >}
     *
     *
     */
    public List<JAXBElement<CoordinateOperationPropertyType>> getCoordOperation() {
        if (coordOperation == null) {
            coordOperation = new ArrayList<JAXBElement<CoordinateOperationPropertyType>>();
        }
        return this.coordOperation;
    }

    /**
     * Gets the value of the aggregationType property.
     *
     * @return
     *     possible object is
     *     {@link AggregationType }
     *
     */
    public AggregationType getAggregationType() {
        return aggregationType;
    }

    /**
     * Sets the value of the aggregationType property.
     *
     * @param value
     *     allowed object is
     *     {@link AggregationType }
     *
     */
    public void setAggregationType(AggregationType value) {
        this.aggregationType = value;
    }

}
