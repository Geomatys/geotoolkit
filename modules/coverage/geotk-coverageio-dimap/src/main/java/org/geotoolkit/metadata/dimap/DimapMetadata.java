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
import org.geotoolkit.internal.image.io.SampleMetadataFormat;
import org.w3c.dom.Node;

/**
 * Extend the Spatial metadata format with additional dimap metadata format.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimapMetadata extends SpatialMetadata{

    public static final String NATIVE_FORMAT = DimapConstants.TAG_DIMAP;

    /**
     * The format inferred from the content of the Dimap file.
     * Will be created only when first needed.
     */
    private IIOMetadataFormat nativeFormat;
    

    //todo, this can be a huge file, like 4Mb. store it as a dom tree can use plenty of memory.
    private final Node dimapTree;

    public DimapMetadata(IIOMetadataFormat format, Node dimapTree){
        super(format);
        this.dimapTree = dimapTree;
        nativeMetadataFormatName = NATIVE_FORMAT;
    }

    public DimapMetadata(IIOMetadataFormat format, ImageReader reader, IIOMetadata fallback, Node dimapTree){
        super(format,reader,fallback);
        this.dimapTree = dimapTree;
        extraMetadataFormatNames = Arrays.copyOf(extraMetadataFormatNames, extraMetadataFormatNames.length+1);
        extraMetadataFormatNames[extraMetadataFormatNames.length-1] = NATIVE_FORMAT;
    }

    @Override
    public Node getAsTree(String formatName) throws IllegalArgumentException {
        if(!formatName.equals(NATIVE_FORMAT)) return super.getAsTree(formatName);
        return dimapTree;
    }

    /**
     * If the given format name is {@code "Dimap"}, returns a "dynamic" metadata format
     * inferred from the actual content of the Dimap file. Otherwise returns the usual
     * metadata format as defined in the super-class.
     */
    @Override
    public IIOMetadataFormat getMetadataFormat(final String formatName) {
        if (NATIVE_FORMAT.equals(formatName)) {
            if (nativeFormat == null) {
                nativeFormat = new Format();
            }
            return nativeFormat;
        }
        return super.getMetadataFormat(formatName);
    }

    /**
     * The metadata format for the encloding {@link DimapMetadata} instance.
     */
    private final class Format extends SampleMetadataFormat {
        /**
         * Creates a new instance.
         */
        Format() {
            super(NATIVE_FORMAT);
        }

        /**
         * Returns the metadata to use for inferring the format.
         * This is invoked when first needed.
         */
        @Override
        protected Node getDataRootNode() {
            return getAsTree(NATIVE_FORMAT);
        }

    }

}
