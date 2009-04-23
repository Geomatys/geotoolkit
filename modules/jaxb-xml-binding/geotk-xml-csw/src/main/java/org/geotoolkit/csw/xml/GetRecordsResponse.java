/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.csw.xml;

/**
 *
 * @author Mehdi Sidhoum
 */
public interface GetRecordsResponse {
    
    /**
     * Gets the value of the requestId property.
     * 
     */
    public String getRequestId();

    /**
     * Sets the value of the requestId property.
     * 
     */
    public void setRequestId(String value);

    /**
     * Gets the value of the searchStatus property.
     * 
     */
    public RequestStatus getSearchStatus();

    /**
     * Gets the value of the searchResults property.
     * 
     */
    public SearchResults getSearchResults();

    /**
     * Gets the value of the version property.
     * 
     */
    public String getVersion();

    /**
     * Sets the value of the version property.
     * 
     */
    public void setVersion(String value);

}
