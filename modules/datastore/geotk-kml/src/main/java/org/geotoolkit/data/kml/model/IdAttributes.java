/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

/**
 * <p>This interface maps idAttributes attributeGroup.</p>
 *
 * <pre>
 * &lt;attributeGroup name="idAttributes">
 *  &lt;attribute name="id" type="ID" use="optional"/>
 *  &lt;attribute name="targetId" type="NCName" use="optional"/>
 * &lt;/attributeGroup>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface IdAttributes {

    /**
     *
     * @return The id value.
     */
    public String getId();

    /**
     *
     * @return The targetId value.
     */
    public String getTargetId();

    /**
     * 
     * @param id
     */
    public void setId(String id);

    /**
     *
     * @param targetId
     */
    public void setTargetId(String targetId);
}
