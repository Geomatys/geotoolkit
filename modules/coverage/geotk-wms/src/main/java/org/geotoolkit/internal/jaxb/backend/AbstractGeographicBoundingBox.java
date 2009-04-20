/*
 * Sicade - SystÃ¨mes intÃ©grÃ©s de connaissances pour l'aide Ã  la dÃ©cision en environnement
 * (C) 2005, Institut de Recherche pour le DÃ©veloppement
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
public abstract class AbstractGeographicBoundingBox {
    
    public abstract double getWestBoundLongitude();
    
    public abstract double getEastBoundLongitude();
    
    public abstract double getSouthBoundLatitude();
            
    public abstract double getNorthBoundLatitude();

}
