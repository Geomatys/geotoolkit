/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.csw.xml;

import java.util.List;
import org.geotoolkit.dublincore.xml.AbstractSimpleLiteral;

/**
 *
 * @author Mehdi Sidhoum (Geomatys).
 * @author Guilhem Legal (Geomatys).
 * @module pending
 */
public interface DCMIRecord extends AbstractRecord {
    
    /**
     * Gets the value of the dcElement property.
     * (unModifiable)
     */
    List<? extends Object> getDCElement();
    
    AbstractSimpleLiteral getIdentifier();
    
    AbstractSimpleLiteral getTitle();
    
    AbstractSimpleLiteral getType();
    
    List<? extends AbstractSimpleLiteral> getSubject();
    
    List<? extends AbstractSimpleLiteral> getFormat();
    
    AbstractSimpleLiteral getModified();
    
    List< ? extends AbstractSimpleLiteral> getAbstract();
    
    List<? extends AbstractSimpleLiteral> getCreator();
    
    AbstractSimpleLiteral getLanguage();
    
    List<? extends AbstractSimpleLiteral> getRelation();
    
    List<? extends AbstractSimpleLiteral> getSource();
    
    List<? extends AbstractSimpleLiteral> getCoverage();
    
    AbstractSimpleLiteral getDate();
    
    List<? extends AbstractSimpleLiteral> getRights();
    
    AbstractSimpleLiteral getSpatial();
    
    AbstractSimpleLiteral getReferences();
    
    List<? extends AbstractSimpleLiteral> getPublisher();
    
    List<? extends AbstractSimpleLiteral> getContributor();
    
    List<? extends AbstractSimpleLiteral> getDescription();

}
