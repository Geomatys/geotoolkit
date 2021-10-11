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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wfs.xml.DescribeStoredQueries;


/**
 * <p>Java class for DescribeStoredQueriesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DescribeStoredQueriesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}BaseRequestType">
 *       &lt;sequence>
 *         &lt;element name="StoredQueryId" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeStoredQueriesType", propOrder = {
    "storedQueryId"
})
@XmlRootElement(name="DescribeStoredQueries", namespace="http://www.opengis.net/wfs/2.0")
public class DescribeStoredQueriesType extends BaseRequestType implements DescribeStoredQueries {

    @XmlElement(name = "StoredQueryId")
    @XmlSchemaType(name = "anyURI")
    private List<String> storedQueryId;

    public DescribeStoredQueriesType() {

    }

    public DescribeStoredQueriesType(final String service, final String version, final String handle, final List<String> storedQueryId) {
        super(service, version, handle);
        this.storedQueryId = storedQueryId;
    }

    /**
     * Gets the value of the storedQueryId property.
     *
     */
    @Override
    public List<String> getStoredQueryId() {
        if (storedQueryId == null) {
            storedQueryId = new ArrayList<String>();
        }
        return this.storedQueryId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (storedQueryId != null) {
            for (String sq : storedQueryId) {
                sb.append(sq).append('\n');
            }
        }
        return sb.toString();
    }
}
