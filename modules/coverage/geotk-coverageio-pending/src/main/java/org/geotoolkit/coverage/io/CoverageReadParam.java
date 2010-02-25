/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.coverage.io;

import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 *
 * @deprecated Replaced by {@link GridCoverageReadParam}.
 */
@Deprecated
public class CoverageReadParam extends GridCoverageReadParam {
    
    public CoverageReadParam(Envelope env, double[] resolution){
        super.setEnvelope(env);
        super.setResolution(resolution);
    }
    
    public Envelope getEnveloppe(){
        return super.getEnvelope();
    }

    @Override
    public double[] getResolution(){
        return super.getResolution();
    }
    
}
