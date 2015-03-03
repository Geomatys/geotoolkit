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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.opengis.coverage.grid.Grid;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * <p>Java class for GridType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GridType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;element name="limits" type="{http://www.opengis.net/gml/3.2}GridLimitsType"/>
 *         &lt;choice>
 *           &lt;element name="axisLabels" type="{http://www.opengis.net/gml/3.2}NCNameList"/>
 *           &lt;element name="axisName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="dimension" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridType", propOrder = {
    "rest"
})
@XmlSeeAlso({
    RectifiedGridType.class
})
public class GridType extends AbstractGeometryType {

    @XmlElementRefs({
        @XmlElementRef(name = "axisName", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class),
        @XmlElementRef(name = "limits", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class),
        @XmlElementRef(name = "axisLabels", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> rest;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
    private Integer dimension;

    public GridType() {
        
    }
    
    public GridType(final Grid grid) {
        this(grid, null);
    }
    
    /**
     * 
     * @param grid 
     * @param crs 
     */
    public GridType(final Grid grid, final CoordinateReferenceSystem crs) {
        final ObjectFactory factory = new ObjectFactory();
        if (grid != null) {
            this.dimension = grid.getDimension();
            final GridEnvelopeType limits = new GridEnvelopeType(grid.getExtent());
            this.rest = new ArrayList<>();
            this.rest.add(factory.createGridTypeLimits(new GridLimitsType(limits)));
            if (grid.getAxisNames() != null) {
                this.rest.add(factory.createGridTypeAxisLabels(grid.getAxisNames()));
            } else if (crs != null){
                final List<String> axisNames = new ArrayList<>();
                for (int i = 0; i < crs.getCoordinateSystem().getDimension(); i++) {
                    axisNames.add(crs.getCoordinateSystem().getAxis(i).getAbbreviation());
                }
                this.rest.add(factory.createGridTypeAxisLabels(axisNames));
            }
        }
    }
    
    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "AxisLabels" is used by two different parts of a schema. See: 
     * line 28 of file:/home/guilhem/xsd/gml/3.2.1/grids.xsd
     * line 46 of file:/home/guilhem/xsd/gml/3.2.1/geometryBasic0d1d.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the rest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link GridLimitsType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getRest() {
        if (rest == null) {
            rest = new ArrayList<>();
        }
        return this.rest;
    }

    /**
     * Gets the value of the dimension property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDimension() {
        return dimension;
    }

    /**
     * Sets the value of the dimension property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDimension(Integer value) {
        this.dimension = value;
    }

    public Object evaluate(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T evaluate(Object o, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(ExpressionVisitor ev, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
