/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.metadata.geotiff;

import javax.imageio.metadata.IIOMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadata;

/**
 * TODO
 * this was added just to allow the mosaicBuilder to find a writer for this given type.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GeoTiffMetaDataWriter {

    public GeoTiffMetaDataWriter(){

    }

    public IIOMetadata fillMetadata(IIOMetadata streamMD, SpatialMetadata spatialMD){
        //todo fill in metadatas
        return streamMD;
    }

}
