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
package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.StringUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default pyramid
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultPyramid implements Pyramid{

    private final String id;
    private final PyramidSet set;
    private final CoordinateReferenceSystem crs;
    private final List<GridMosaic> mosaics = new ArrayList<GridMosaic>();

    public DefaultPyramid(PyramidSet set, CoordinateReferenceSystem crs) {
        this(null,set,crs);
    }

    public DefaultPyramid(String id, PyramidSet set, CoordinateReferenceSystem crs) {
        this.set = set;
        this.crs = crs;
        if(id == null){
            this.id = UUID.randomUUID().toString();
        }else{
            this.id = id;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Internal list of pyramids, modify with causion.
     */
    public List<GridMosaic> getMosaicsInternal() {
        return mosaics;
    }

    @Override
    public PyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public double[] getScales() {
        final SortedSet<Double> scaleSet = new TreeSet<Double>();

        for(GridMosaic m : mosaics){
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
    public Collection<GridMosaic> getMosaics(int index) {
        final List<GridMosaic> candidates = new ArrayList<GridMosaic>();
        final double[] scales = getScales();
        for(GridMosaic m : mosaics){
            if(m.getScale() == scales[index]){
                candidates.add(m);
            }
        }
        return candidates;
    }

    @Override
    public List<GridMosaic> getMosaics() {
        return Collections.unmodifiableList(mosaics);
    }

    @Override
    public String toString(){
        return StringUtilities.toStringTree(
                Classes.getShortClassName(this)
                +" "+IdentifiedObjects.getIdentifierOrName(getCoordinateReferenceSystem())
                +" "+getId(),
                getMosaicsInternal());
    }

    @Override
    public Envelope getEnvelope() {
        GeneralEnvelope env = null;
        for(GridMosaic mosaic : getMosaics()){
            if(env==null){
                env = new GeneralEnvelope(mosaic.getEnvelope());
            }else{
                env.add(mosaic.getEnvelope());
            }
        }
        return env;
    }

}
