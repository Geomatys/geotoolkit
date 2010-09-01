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
package org.geotoolkit.atom.model;

import java.util.List;

/**
 * <p>This interface maps Atom personConstruct type.</p>
 *
 * <pre>
 * &lt;complexType name="atomPersonConstruct">
 *  &lt;choice minOccurs="0" maxOccurs="unbounded">
 *      &lt;element ref="atom:name"/>
 *      &lt;element ref="atom:uri"/>
 *      &lt;element ref="atom:email"/>
 *  &lt;/choice>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AtomPersonConstruct {

    /**
     *
     * @return
     */
    List<Object> getParams();

    /**
     * 
     * @param params
     */
    void setParams(final List<Object> params);
}
