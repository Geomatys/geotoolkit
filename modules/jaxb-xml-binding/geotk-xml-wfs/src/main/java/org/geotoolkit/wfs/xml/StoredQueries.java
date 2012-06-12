/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wfs.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlRootElement(name="StoredQueries")
@XmlAccessorType(XmlAccessType.FIELD)
public class StoredQueries {
    
    @XmlJavaTypeAdapter(StoredQueryDescriptionAdapter.class)
    private List<StoredQueryDescription> storedQuery;

    public StoredQueries() {
        
    }
    
    public StoredQueries(final List<StoredQueryDescription> storedQuery) {
        this.storedQuery = storedQuery;
    }
    
    /**
     * @return the storedQuery
     */
    public List<StoredQueryDescription> getStoredQuery() {
        return storedQuery;
    }

    /**
     * @param storedQuery the storedQuery to set
     */
    public void setStoredQuery(List<StoredQueryDescription> storedQuery) {
        this.storedQuery = storedQuery;
    }
}
