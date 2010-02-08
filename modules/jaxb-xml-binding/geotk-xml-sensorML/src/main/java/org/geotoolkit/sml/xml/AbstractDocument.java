/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

package org.geotoolkit.sml.xml;

import java.util.List;
import org.geotoolkit.gml.xml.v311.StringOrRefType;

/**
 *
 * @author Mehdi Sidhoum (Geomatys).
 */
public interface AbstractDocument {

    public StringOrRefType getDescription();

    public String getDate();

    public AbstractContact getContact();

    public String getFormat();

    public List<? extends AbstractOnlineResource> getOnlineResource();

    public String getVersion();

    public String getId();
}
