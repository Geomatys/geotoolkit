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

/**
 *
 * @author Mehdi Sidhoum (Geomatys).
 */
public interface AbstractKeywords {

    public AbstractKeywordList getKeywordList();

    public String getRemoteSchema();

    public void setRemoteSchema(String value);

    public String getActuate();

    public void setActuate(String value);

    public String getArcrole();

    public void setArcrole(String value);

    public String getHref();

    public void setHref(String value);

    public String getRole();

    public void setRole(String value);

    public String getShow();

    public void setShow(String value);

    public String getTitle();

    public void setTitle(String value);

    public String getType();

    public void setType(String value);
}
