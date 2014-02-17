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
package org.geotoolkit.data.gx.model;

import org.geotoolkit.data.kml.model.AbstractObject;

/**
 * <p>This interface maps AbstractTourPrimitive element.</p>
 *
 * <pre>
 * &lt;element name="AbstractTourPrimitiveGroup" type="gx:AbstractTourPrimitiveType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractTourPrimitiveType">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType"/>
 *  &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface AbstractTourPrimitive extends AbstractObject {

}
