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

import java.net.URI;
import java.util.List;

/**
 * <p>This interface maps Update element.</p>
 *
 * <pre>
 * &lt;element name="Update" type="kml:UpdateType"/>
 *
 * &lt;complexType name="UpdateType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:targetHref"/>
 *      &lt;choice maxOccurs="unbounded">
 *          &lt;element ref="kml:Create"/>
 *          &lt;element ref="kml:Delete"/>
 *          &lt;element ref="kml:Change"/>
 *          &lt;element ref="kml:UpdateOpExtensionGroup"/>
 *      &lt;/choice>
 *      &lt;element ref="kml:UpdateExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 *
 * &lt;element name="UpdateOpExtensionGroup" abstract="true"/>
 * &lt;element name="UpdateExtensionGroup" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Update {

    /**
     *
     * @return
     */
    URI getTargetHref();

    /**
     *
     * @return
     */
    List<Object> getUpdates();

    /**
     *
     * @return
     */
    List<Object> getUpdateOpExtensions();

    /**
     * 
     * @return
     */
    List<Object> getUpdateExtensions();

    /**
     *
     * @param targetHref
     */
    void setTargetHref(URI targetHref);

    /**
     * 
     * @param updates
     */
    void setUpdates(List<Object> updates);

    /**
     *
     * @param updateOpEXtensions
     */
    void setUpdateOpExtensions(List<Object> updateOpEXtensions);

    /**
     * 
     * @param updateExtensions
     */
    void setUpdateExtensions(List<Object> updateExtensions);
}
