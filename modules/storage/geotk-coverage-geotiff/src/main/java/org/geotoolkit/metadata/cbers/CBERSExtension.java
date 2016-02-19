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
package org.geotoolkit.metadata.cbers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.metadata.geotiff.GeoTiffExtension;
import org.opengis.metadata.Metadata;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CBERSExtension extends GeoTiffExtension{

    private static final String BASE_NAME = "CBERS";

    private Boolean hasMetadata = null;
    private Path metaFile = null;
    private Metadata cbersMeta = null;

    @Override
    public boolean isPresent(Object input) {
        try {
            if (IOUtilities.canProcessAsPath(input)) {
                Path inputPath = IOUtilities.toPath(input);

                final String name = inputPath.getFileName().toString();

                //all cbers files start with the same prefix
                if (name.startsWith(BASE_NAME)) return true;
            }

        } catch (IOException ex) {
            //not a file, no cbers metadata available
        }

        return false;
    }

    @Override
    public SpatialMetadata fillSpatialMetaData(TiffImageReader reader, SpatialMetadata metadata) throws IOException {

        //NOTE : we don't extract anything from this metadata format yet
        // FIXME Why ?
        if(true) return metadata;
        
        if(hasMetadata == null){
            //get the metadata file
            final Object input = reader.getInput();
            if (IOUtilities.canProcessAsPath(input)) {
                Path inputPath = IOUtilities.toPath(input);

                final String name = inputPath.getFileName().toString();
                final int index = name.lastIndexOf('.');
                if (index <= 0) {
                    hasMetadata = false;
                    return metadata;
                }

                metaFile = IOUtilities.changeExtension(inputPath, "xml");
                if (!Files.exists(metaFile)) {
                    metaFile = IOUtilities.changeExtension(inputPath, "XML");
                }
                hasMetadata = Files.exists(metaFile);

            } else {
                hasMetadata = false;
                return metadata;
            }
        }

        if(hasMetadata && cbersMeta==null){
            try {
                cbersMeta = CBERS.toMetadata(metaFile);
            } catch (ParserConfigurationException | SAXException ex) {
                throw new IOException(ex.getMessage(), ex);
            }
        }
        return metadata;

    }

}
