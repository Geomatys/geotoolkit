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

import org.geotoolkit.wfs.xml.CreateStoredQuery;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateStoredQueryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateStoredQueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}BaseRequestType">
 *       &lt;sequence>
 *         &lt;element name="StoredQueryDefinition" type="{http://www.opengis.net/wfs/2.0}StoredQueryDescriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateStoredQueryType", propOrder = {
    "storedQueryDefinition"
})
public class CreateStoredQueryType extends BaseRequestType implements CreateStoredQuery {

    public CreateStoredQueryType() {
        
    }
    
    public CreateStoredQueryType(final String service, final String version, final String handle, 
            final List<StoredQueryDescriptionType> storedQueryDefinition) {
        super(service, version, handle);
        this.storedQueryDefinition = storedQueryDefinition;
    }
    
    @XmlElement(name = "StoredQueryDefinition")
    private List<StoredQueryDescriptionType> storedQueryDefinition;

    /**
     * Gets the value of the storedQueryDefinition property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link StoredQueryDescriptionType }
     * 
     * 
     */
    @Override
    public List<StoredQueryDescriptionType> getStoredQueryDefinition() {
        if (storedQueryDefinition == null) {
            storedQueryDefinition = new ArrayList<>();
        }
        return this.storedQueryDefinition;
    }

}
