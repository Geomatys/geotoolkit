/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.wfs;

import org.geotoolkit.data.FeatureCollection;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AbstractInsert implements Insert{

    protected FeatureCollection features;
    protected String handle = null;
    protected CoordinateReferenceSystem crs = null;
    protected String inputFormat = null;
    protected IdentifierGenerationOption idGen = null;

    @Override
    public String getHandle() {
        return handle;
    }

    @Override
    public void setHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    @Override
    public String getInputFormat() {
        return inputFormat;
    }

    @Override
    public void setInputFormat(String format) {
        this.inputFormat = format;
    }

    @Override
    public IdentifierGenerationOption getIdentifierGenerationOption() {
        return idGen;
    }

    @Override
    public void setIdentifierGenerationOption(IdentifierGenerationOption type) {
        this.idGen = type;
    }

    @Override
    public FeatureCollection getFeatures() {
        return features;
    }

    @Override
    public void setFeatures(FeatureCollection fc) {
        this.features = fc;
    }


}
