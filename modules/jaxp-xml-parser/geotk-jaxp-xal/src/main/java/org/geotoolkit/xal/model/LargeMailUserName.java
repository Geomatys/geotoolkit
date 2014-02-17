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
 * <p>This interface maps LargeMailUserName element.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Type" type="xs:string">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attribute name="Code" type="xs:string"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface LargeMailUserName {

    /**
     *
     * @return
     */
    String getContent();

    /**
     * <p>Airport, Hospital, etc.</p>
     *
     * @return
     */
    String getType();

    /**
     * 
     * @return
     */
    String getCode();

    /**
     *
     * @param content
     */
    void setContent(String content);

    /**
     *
     * @param type
     */
    void setType(String type);

    /**
     * 
     * @param code
     */
    void setCode(String code);
}
