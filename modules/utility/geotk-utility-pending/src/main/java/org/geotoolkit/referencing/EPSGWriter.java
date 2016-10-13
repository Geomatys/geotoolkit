/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.referencing;

import javax.measure.Unit;
import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.operation.Projection;
import org.opengis.util.FactoryException;

/**
 * This class is the opposite of EpsgFactory, rather then create CRS objects
 * it allows to store user defined CRS and allocate them an EPSG code 
 * in range [32768, 60 000 000[
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface EPSGWriter {
    
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate CoordinateReferenceSystem
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreateCoordinateReferenceSystem(final CoordinateReferenceSystem candidate) throws FactoryException;
    
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate CoordinateSystem
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreateCoordinateSystem(final CoordinateSystem candidate) throws FactoryException;
    
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate Datum
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreateDatum(final Datum candidate) throws FactoryException;
    
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate Projection
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreateProjection(final Projection candidate) throws FactoryException;
    
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate Extent
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreateArea(final Extent candidate) throws FactoryException;
    
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate Unit
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreateUOM(final Unit candidate) throws FactoryException;
 
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate Ellipsoid
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreateEllipsoid(final Ellipsoid candidate) throws FactoryException;
    
    /**
     * Store given object and return it's allocated epsg code.
     * 
     * @param candidate PrimeMeridian
     * @return integer, allocated epsg code
     * @throws FactoryException 
     */
    int getOrCreatePrimeMeridian(final PrimeMeridian candidate) throws FactoryException;
    
}
