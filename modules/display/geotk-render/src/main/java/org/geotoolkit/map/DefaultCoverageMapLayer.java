/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.map;


import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;

/**
 * Default implementation of the coverage MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 */
final class DefaultCoverageMapLayer extends AbstractMapLayer implements CoverageMapLayer {

    private final CoverageReader reader;
    private final Name coverageName;
    
    DefaultCoverageMapLayer(CoverageReader reader, MutableStyle style, Name name){
        super(style);
        if(reader == null || name == null || name.toString() == null || name.getLocalPart() == null){
            throw new NullPointerException("Coverage Reader and name can not be null");
        }
        this.reader = reader;
        this.coverageName = name;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Name getCoverageName() {
        return coverageName;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageReader getCoverageReader(){
        return reader;
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {        
        return reader.getCoverageBounds();
    }

}
