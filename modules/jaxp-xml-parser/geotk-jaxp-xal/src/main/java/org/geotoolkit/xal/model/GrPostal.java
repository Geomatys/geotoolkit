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
package org.geotoolkit.xal.model;

/**
 * <p>This interface maps grPostal attributeGroup.</p>
 *
 * <pre>
 *  &lt;xs:attributeGroup name="grPostal">
 *  &lt;xs:attribute name="Code">...
 *  &lt;/xs:attribute>
 *  &lt;/xs:attributeGroup>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface GrPostal {

    /**
     * 
     * <p>Used by postal services to encode the name of the element.</p>
     * 
     * @return
     */
    public String getCode();
}
