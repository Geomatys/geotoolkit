/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.metadata.rapideye;

import java.io.IOException;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.metadata.GeoTiffExtension;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RapidEyeExtension extends GeoTiffExtension {

    @Override
    public boolean isPresent(Object input) {
        return false;
    }

    @Override
    public SpatialMetadata fillSpatialMetaData(TiffImageReader reader, SpatialMetadata metadata) throws IOException {
        return metadata;
    }

}
