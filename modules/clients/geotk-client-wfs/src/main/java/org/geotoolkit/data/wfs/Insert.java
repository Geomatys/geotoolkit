/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

import org.apache.sis.storage.FeatureSet;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface Insert extends TransactionElement {

    String getHandle();

    void setHandle(String handle);

    CoordinateReferenceSystem getCoordinateReferenceSystem();

    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs);

    String getInputFormat();

    void setInputFormat(String format);

    IdentifierGenerationOption getIdentifierGenerationOption();

    void setIdentifierGenerationOption(IdentifierGenerationOption type);

    FeatureSet getFeatures();

    void setFeatures(FeatureSet fc);

}
