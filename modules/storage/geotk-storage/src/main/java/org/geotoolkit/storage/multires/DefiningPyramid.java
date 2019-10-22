/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel
 */
public class DefiningPyramid implements Pyramid {

    private final String identifier;
    private final String format;
    private final CoordinateReferenceSystem crs;
    private final List<Mosaic> mosaics;

    public DefiningPyramid(CoordinateReferenceSystem crs) {
        this(null,null,crs,new ArrayList());
    }

    public DefiningPyramid(String identifier, String format, CoordinateReferenceSystem crs, List<Mosaic> mosaics) {
        this.identifier = identifier;
        this.format = format;
        this.crs = crs;
        this.mosaics = mosaics;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public Collection<Mosaic> getMosaics() {
        return mosaics;
    }

    @Override
    public double[] getScales() {
        final SortedSet<Double> scaleSet = new TreeSet<>();
        for(Mosaic m : mosaics){
            scaleSet.add(m.getScale());
        }

        final double[] scales = new double[scaleSet.size()];
        int i=0;
        for(Double d : scaleSet){
            scales[i] = d;
            i++;
        }
        return scales;
    }

    @Override
    public Collection<? extends Mosaic> getMosaics(int index) {
        final List<Mosaic> candidates = new ArrayList<>();
        final double[] scales = getScales();
        for(Mosaic m : mosaics){
            if(m.getScale() == scales[index]){
                candidates.add(m);
            }
        }
        return candidates;
    }

    @Override
    public Envelope getEnvelope() {
        GeneralEnvelope env = null;
        for(Mosaic mosaic : getMosaics()){
            if(env==null){
                env = new GeneralEnvelope(mosaic.getEnvelope());
            }else{
                env.add(mosaic.getEnvelope());
            }
        }
        return env;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public Mosaic createMosaic(Mosaic template) throws DataStoreException {
        final DefiningMosaic m2 = new DefiningMosaic(UUID.randomUUID().toString(),
                template.getUpperLeftCorner(), template.getScale(), template.getTileSize(), template.getGridSize());
        mosaics.add(m2);
        return m2;
    }

    @Override
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
