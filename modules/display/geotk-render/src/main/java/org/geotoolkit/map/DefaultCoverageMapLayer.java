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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.PyramidalModel;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.ImmutableEnvelope;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.NullArgumentException;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;

/**
 * Default implementation of the coverage MapLayer.
 * Use MapBuilder to create it.
 * This class is left public only for subclass implementations.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultCoverageMapLayer extends AbstractMapLayer implements CoverageMapLayer {

    private static final ImmutableEnvelope INFINITE = new ImmutableEnvelope(DefaultGeographicCRS.WGS84, -180, 180, -90, 90);

    private final CoverageReference ref;
    private final GridCoverageReader reader;
    private final Name coverageName;
    
    protected DefaultCoverageMapLayer(final CoverageReference ref, final MutableStyle style, final Name name){
        super(style);
        if(ref == null || name == null || name.toString() == null || name.getLocalPart() == null){
            throw new NullArgumentException("Coverage Reader and name can not be null");
        }
        this.ref = ref;
        this.reader = null;
        this.coverageName = name;
    }
    
    protected DefaultCoverageMapLayer(final GridCoverageReader reader, final MutableStyle style, final Name name){
        super(style);
        if(reader == null || name == null || name.toString() == null || name.getLocalPart() == null){
            throw new NullArgumentException("Coverage Reader and name can not be null");
        }
        this.ref = null;
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
    public GridCoverageReader getCoverageReader(){
        if(ref != null){
            try {
                return ref.createReader();
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            }
        }
        return reader;
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageReference getCoverageReference() {
        return ref;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        if(ref != null && ref instanceof PyramidalModel){
            try {
                return ((PyramidalModel)ref).getPyramidSet().getEnvelope();
            } catch (DataStoreException ex) {
                Logger.getLogger(DefaultCoverageMapLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        try {
            final GeneralGridGeometry geom = getCoverageReader().getGridGeometry(0);
            if(geom == null){
                LOGGER.log(Level.WARNING, "Could not access envelope of layer {0}", getCoverageName());
                return INFINITE;
            }else{
                return geom.getEnvelope();
            }
        } catch (CoverageStoreException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return INFINITE;
        }
    }

}
