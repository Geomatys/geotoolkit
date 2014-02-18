/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.tree.Trees;
import org.apache.sis.util.Classes;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * Default PyramidSet.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPyramidSet extends AbstractPyramidSet{
    
    private final String id = UUID.randomUUID().toString();
    private final List<Pyramid> pyramids = new ArrayList<Pyramid>();
    private final List<String> formats = new ArrayList<String>();

    @Override
    public Collection<Pyramid> getPyramids() {
        return pyramids;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> getFormats() {
        return formats;
    }

    @Override
    public Envelope getEnvelope() {
        for(Pyramid pyramid : getPyramids()){
            for(GridMosaic mosaic : pyramid.getMosaics()){
                final DirectPosition position = mosaic.getUpperLeftCorner();
                final double minX = position.getOrdinate(0);
                final double maxY = position.getOrdinate(1);
                final double spanX = mosaic.getTileSize().width * mosaic.getGridSize().width * mosaic.getScale();
                final double spanY = mosaic.getTileSize().height* mosaic.getGridSize().height* mosaic.getScale();
                final GeneralEnvelope envelope = new GeneralEnvelope(
                        pyramid.getCoordinateReferenceSystem());
                envelope.setRange(0, minX, minX + spanX);
                envelope.setRange(1, maxY - spanY, maxY );
                return envelope;
            }
        }
        return null;
    }
    
    @Override
    public String toString(){
        return Trees.toString(Classes.getShortClassName(this)+" "+getId(), getPyramids());
    }
    
}
