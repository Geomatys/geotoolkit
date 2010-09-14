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
 * <p>This interface maps SortingCode element.</p>
 *
 * <p>Used for sorting addresses. Values may for example be CEDEX 16 (France).</p>
 *
 * <pre>
 * &lt;xs:element name="SortingCode" minOccurs="0">
 *  &lt;xs:complexType>
 *      &lt;xs:attribute name="Type">
 *      &lt;xs:xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:/xs:complexType>
 * &lt;xs:/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SortingCode {

    /**
     * <p>Specific to postal service.</p>
     *
     * @return
     */
    String getType();

    /**
     * 
     * @return
     */
    GrPostal getGrPostal();

}
