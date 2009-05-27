/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.dublincore.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlTransient;


/**
 * 
 * 
 * @author Guilhem Legal
 */
@XmlTransient
public abstract class AbstractSimpleLiteral {


    /**
     * This is the default type for all of the DC elements. 
     * It defines a complexType SimpleLiteral which permits mixed content but disallows child elements by use of minOcccurs/maxOccurs. 
     * However, this complexType does permit the derivation of other types which would permit child elements. 
     * The scheme attribute may be used as a qualifier to reference an encoding scheme that describes the value domain for a given property.
     *
     * Gets the value of the content property.
     */
    public abstract List<String> getContent();

    /**
     * Gets the value of the scheme property.
     * 
    */
    public abstract String getScheme();
}
