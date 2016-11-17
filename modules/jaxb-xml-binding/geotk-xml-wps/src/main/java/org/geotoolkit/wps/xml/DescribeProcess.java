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
package org.geotoolkit.wps.xml;

import java.util.List;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.RequestBase;

/**
 *
 * @author guilhem
 */
public interface DescribeProcess extends RequestBase {
    
    /**
     * Returns identifiers, never {@code null}.
     * List can be modified.
     *
     * @return
     */
    List<? extends AbstractCodeType> getIdentifier();

    void setIdentifier(List<String> ids);

    /**
     * RFC 4646 language code of the human-readable text (e.g. "en-CA") in the process description.
     *
     * @return possible object is {@link String }
     */
    String getLanguage();

    /**
     * Sets the value of the lang property.
     *
     * @param language allowed object is {@link String }
     */
    void setLanguage(String language);

}
