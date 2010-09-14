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
 * <p>This interface maps PostBoxNumberExtension element.</p>
 *
 * <p>Some countries like USA have POBox as 12345-123.</p>
 *
 * <pre>
 * &lt;xs:element name="PostBoxNumberExtension" minOccurs="0">
 *  &lt;xs:complexType mixed="true">
 *      &lt;xs:attribute name="NumberExtensionSeparator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostBoxNumberExtension {

    /**
     *
     * @return
     */
    String getContent();

    /**
     * <p>"-" is the NumberExtensionSeparator in POBOX:12345-123.</p>
     * @return
     */
    String getNumberExtensionSeparator();
}