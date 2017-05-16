/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import org.geotoolkit.ows.xml.AbstractCodeType;

/**
 *
 * @author guilhem
 */
public interface OutputDefinition {

    AbstractCodeType getIdentifier();

    String getMimeType();

    void setMimeType(String value);

    String getEncoding();

    void setEncoding(String value);

    String getSchema();

    void setSchema(final String value);

    String getUom();

    void setUom(final String value);

    boolean isReference();

    DocumentOutputDefinition asDoc();
}
