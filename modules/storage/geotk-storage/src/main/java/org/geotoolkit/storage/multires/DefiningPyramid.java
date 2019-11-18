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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<String,Mosaic> mosaics = new HashMap<>();

    public DefiningPyramid(CoordinateReferenceSystem crs) {
        this(null,null,crs,new ArrayList());
    }

    public DefiningPyramid(String identifier, String format, CoordinateReferenceSystem crs, List<Mosaic> mosaics) {
        this.identifier = identifier;
        this.format = format;
        this.crs = crs;

        for (Mosaic m : mosaics) {
            this.mosaics.put(m.getIdentifier(), m);
        }
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public Collection<Mosaic> getMosaics() {
        return Collections.unmodifiableCollection(mosaics.values());
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
        String uid = template.getIdentifier();
        if (mosaics.containsKey(uid)) {
            uid = UUID.randomUUID().toString();
        }

        final DefiningMosaic m2 = new DefiningMosaic(uid, template.getUpperLeftCorner(),
                template.getScale(), template.getTileSize(), template.getGridSize());
        mosaics.put(m2.getIdentifier(), m2);
        return m2;
    }

    @Override
    public void deleteMosaic(String mosaicId) throws DataStoreException {
        mosaics.remove(mosaicId);
    }

}
