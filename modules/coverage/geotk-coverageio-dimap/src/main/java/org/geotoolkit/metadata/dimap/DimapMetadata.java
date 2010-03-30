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

package org.geotoolkit.metadata.dimap;

import java.util.Arrays;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.w3c.dom.Node;

/**
 * Extend the Spatial metadata format with additional dimap metadata format.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimapMetadata extends SpatialMetadata{

    //todo, this can be a huge file, like 14Mb. store it as a dom tree can
    //use plenty of memory.
    private final Node dimapTree;

    public DimapMetadata(IIOMetadataFormat format, Node dimapTree){
        super(format);

        this.dimapTree = dimapTree;
        extraMetadataFormatNames = Arrays.copyOf(extraMetadataFormatNames, extraMetadataFormatNames.length+1);
        extraMetadataFormatNames[extraMetadataFormatNames.length-1] = "dimap";
    }

    public DimapMetadata(IIOMetadataFormat format, ImageReader reader, IIOMetadata fallback, Node dimapTree){
        super(format,reader,fallback);

        this.dimapTree = dimapTree;
        extraMetadataFormatNames = Arrays.copyOf(extraMetadataFormatNames, extraMetadataFormatNames.length+1);
        extraMetadataFormatNames[extraMetadataFormatNames.length-1] = "dimap";
    }

    @Override
    public Node getAsTree(String formatName) throws IllegalArgumentException {
        if(!formatName.equals("dimap")) return super.getAsTree(formatName);
        return dimapTree;
    }

}
