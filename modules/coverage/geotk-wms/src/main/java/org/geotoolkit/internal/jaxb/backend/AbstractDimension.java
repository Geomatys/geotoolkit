/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2008 Geomatys
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

package org.geotoolkit.internal.jaxb.backend;

import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Guilhem Legal
 */
@XmlTransient
public abstract class AbstractDimension {
    
    public abstract String getValue();
    
    public abstract void setValue(String value);
    
    public abstract String getName();
    
    public abstract String getUnits();
    
    public abstract String getUnitSymbol();
    
    public abstract String getDefault();
    
    public abstract void setDefault(String _default);

}
