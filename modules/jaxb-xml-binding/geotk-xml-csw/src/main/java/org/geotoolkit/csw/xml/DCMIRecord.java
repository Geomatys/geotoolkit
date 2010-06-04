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
    public List<? extends Object> getDCElement();
    
    public AbstractSimpleLiteral getIdentifier();
    
    public AbstractSimpleLiteral getTitle();
    
    public AbstractSimpleLiteral getType();
    
    public List<? extends AbstractSimpleLiteral> getSubject();
    
    public List<? extends AbstractSimpleLiteral> getFormat();
    
    public AbstractSimpleLiteral getModified();
    
    public List< ? extends AbstractSimpleLiteral> getAbstract();
    
    public List<? extends AbstractSimpleLiteral> getCreator();
    
    public AbstractSimpleLiteral getLanguage();
    
    public List<? extends AbstractSimpleLiteral> getRelation();
    
    public List<? extends AbstractSimpleLiteral> getSource();
    
    public List<? extends AbstractSimpleLiteral> getCoverage();
    
    public AbstractSimpleLiteral getDate();
    
    public List<? extends AbstractSimpleLiteral> getRights();
    
    public AbstractSimpleLiteral getSpatial();
    
    public AbstractSimpleLiteral getReferences();
    
    public List<? extends AbstractSimpleLiteral> getPublisher();
    
    public List<? extends AbstractSimpleLiteral> getContributor();
    
    public AbstractSimpleLiteral getDescription();

}
