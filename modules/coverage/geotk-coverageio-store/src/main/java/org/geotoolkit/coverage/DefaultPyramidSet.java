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
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;

/**
 * Default PyramidSet.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPyramidSet implements PyramidSet{
    
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
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        return env;
    }
    
}
