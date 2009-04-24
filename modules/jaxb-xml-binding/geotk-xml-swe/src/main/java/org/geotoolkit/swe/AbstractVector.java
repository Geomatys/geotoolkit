/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.swe;

/**
 *
 * @author guilhem Legal
 */
public interface AbstractVector extends AbstractDataRecord {

    /**
     * Gets the value of the referenceFrame property.
     */
    public String getReferenceFrame();

    /**
     * Sets the value of the referenceFrame property.
     */
    public void setReferenceFrame(String value);

    /**
     * Gets the value of the localFrame property.
     */
    public String getLocalFrame();

    /**
     * Sets the value of the localFrame property.
     */
    public void setLocalFrame(String value);

}
