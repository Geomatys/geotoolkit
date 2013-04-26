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

package org.geotoolkit.wcs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.util.Version;
import org.geotoolkit.wcs.xml.DescribeCoverage;


/**
 * <p>Java class for DescribeCoverageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeCoverageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs/2.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}CoverageId" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeCoverageType", propOrder = {
    "coverageId"
})
public class DescribeCoverageType extends RequestBaseType implements DescribeCoverage {

    @XmlElement(name = "CoverageId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private List<String> coverageId;

    /**
     * Unordered list of identifiers of desired coverages. A client can obtain identifiers by a prior GetCapabilities request, or from a third-party source. Gets the value of the coverageId property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCoverageId() {
        if (coverageId == null) {
            coverageId = new ArrayList<String>();
        }
        return this.coverageId;
    }

    @Override
    public String toKvp() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public List<String> getIdentifier() {
        return getCoverageId();
    }

}
