/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
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

package org.constellation.sml;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface AbstractInputs {

    public AbstractInputList getInputList();

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema();

    /**
     * Sets the value of the remoteSchema property.
     */
    public void setRemoteSchema(String value);

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate();
    /**
     * Sets the value of the actuate property.
     */
    public void setActuate(String value);

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole();

    /**
     * Sets the value of the arcrole property.
     */
    public void setArcrole(String value);

    /**
     * Gets the value of the href property.
     */
    public String getHref();

    /**
     * Sets the value of the href property.
     */
    public void setHref(String value);

    /**
     * Gets the value of the role property.
     */
    public String getRole();

    /**
     * Sets the value of the role property.
     */
    public void setRole(String value);

    /**
     * Gets the value of the show property.
     */
    public String getShow();

    /**
     * Sets the value of the show property.
     */
    public void setShow(String value);

    /**
     * Gets the value of the title property.
     */
    public String getTitle();

    /**
     * Sets the value of the title property.
     */
    public void setTitle(String value);

    /**
     * Gets the value of the type property.
     */
    public String getType();

    /**
     * Sets the value of the type property.
     */
    public void setType(String value);
}
