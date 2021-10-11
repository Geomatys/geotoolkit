/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.AbstractQueryExpressionType;
import org.geotoolkit.wfs.xml.StoredQuery;


/**
 * <p>Java class for StoredQueryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="StoredQueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}AbstractQueryExpressionType">
 *       &lt;sequence>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/wfs/2.0}ParameterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoredQueryType", propOrder = {
    "parameter"
})
public class StoredQueryType extends AbstractQueryExpressionType implements StoredQuery {

    @XmlElement(name = "Parameter")
    private List<ParameterType> parameter;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String id;

    public StoredQueryType() {

    }

    public StoredQueryType(final String id, final String handle, final List<ParameterType> parameter) {
        super(handle);
        this.id = id;
        this.parameter = parameter;
    }

    /**
     * Gets the value of the parameter property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link ParameterType }
     */
    @Override
    public List<ParameterType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<ParameterType>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

}
