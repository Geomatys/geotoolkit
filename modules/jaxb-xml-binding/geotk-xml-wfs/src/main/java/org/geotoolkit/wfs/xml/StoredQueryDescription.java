/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wfs.xml;

import java.util.List;
import org.geotoolkit.ows.xml.AbstractMetadata;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface StoredQueryDescription {
    
    String getId();
            
    List<? extends Title> getTitle();
    
    List<? extends Abstract> getAbstract();
    
    List<? extends AbstractMetadata> getMetadata();
    
    List<? extends ParameterExpression> getParameter();
    
    List<? extends QueryExpressionText> getQueryExpressionText();
}
