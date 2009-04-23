/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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

package org.geotoolkit.csw.xml;

import java.util.List;
import javax.xml.bind.JAXBElement;
import org.geotoolkit.dublincore.xml.AbstractSimpleLiteral;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;

/**
 *
 * @author Mehdi Sidhoum
 */
public interface SummaryRecord extends AbstractRecord {
    /**
     * Gets the value of the identifier property.
     * (unmodifiable)
     */
    public List<? extends AbstractSimpleLiteral> getIdentifier();

    /**
     * Gets the value of the title property.
     * (unmodifiable)
     */
    public List<? extends AbstractSimpleLiteral> getTitle();

    /**
     * Gets the value of the type property.
     */
    public AbstractSimpleLiteral getType();

    /**
     * Gets the value of the subject property.
     * (unmodifiable) 
     */
    public List<? extends AbstractSimpleLiteral> getSubject();

    /**
     * Gets the value of the format property.
     * (unmodifiable)
     */
    public List<? extends AbstractSimpleLiteral> getFormat();

    /**
     * Gets the value of the relation property.
     * (unmodifiable)
     */
    public List<? extends AbstractSimpleLiteral> getRelation();

    /**
     * Gets the value of the modified property.
     * (unmodifiable)
     */
    public List<? extends AbstractSimpleLiteral> getModified();

    /**
     * Gets the value of the abstract property.
     * (unmodifiable)
     */
    public List<? extends AbstractSimpleLiteral> getAbstract();

    /**
     * Gets the value of the spatial property.
     * (unmodifiable)
     */
    public List<? extends AbstractSimpleLiteral> getSpatial();

    /**
     * Gets the value of the boundingBox property.
     * (unmodifiable)
     */
    public List<? extends JAXBElement<? extends BoundingBoxType>> getBoundingBox();
}
