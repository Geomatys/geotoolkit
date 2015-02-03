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

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.metadata.GeoTiffExtension;
import org.opengis.metadata.Metadata;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CBERSExtension extends GeoTiffExtension{

    private static final String BASE_NAME = "CBERS";

    private Boolean hasMetadata = null;
    private File metaFile = null;
    private Metadata cbersMeta = null;

    @Override
    public boolean isPresent(Object input) {
        try {
            input = IOUtilities.tryToFile(input);
            if(!(input instanceof File)) return false;

            final File file = (File) input;
            final String name = file.getName();

            //all cbers files start with the same prefix
            if(!name.startsWith(BASE_NAME)) return false;


        } catch (IOException ex) {
            //not a file, no cbers metadata available
        }

        return false;
    }

    @Override
    public void fillSpatialMetaData(TiffImageReader reader, SpatialMetadata metadata) throws IOException {

        //NOTE : we don't extract anything from this metadata format yet
        if(true) return;
        
        if(hasMetadata == null){
            //get the metadata file
            Object input = IOUtilities.tryToFile(reader.getInput());
            if(!(input instanceof File)){
                hasMetadata = false;
                return;
            }

            final File file = (File) input;
            final String name = file.getName();

            final int index = name.lastIndexOf('.');
            if(index<=0){
                hasMetadata = false;
                return;
            }

            metaFile = new File(file.getParent(), name.substring(0, index)+".xml");
            if(!metaFile.exists()){
                metaFile = new File(file.getParent(), name.substring(0, index)+".XML");
            }
            hasMetadata = metaFile.exists();
        }

        if(hasMetadata && cbersMeta==null){
            try {
                cbersMeta = CBERS.toMetadata(metaFile);
            } catch (ParserConfigurationException | SAXException ex) {
                throw new IOException(ex.getMessage(), ex);
            }
        }


    }

}
