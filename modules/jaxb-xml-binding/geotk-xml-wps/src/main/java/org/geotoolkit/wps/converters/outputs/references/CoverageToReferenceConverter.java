/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.references;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import net.iharder.Base64;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.opengis.coverage.Coverage;

/**
 * Implementation of ObjectConverter to convert a {@link GridCoverage2D coverage} into a {@link OutputReferenceType reference}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class CoverageToReferenceConverter extends AbstractReferenceOutputConverter<GridCoverage2D> {

    private static CoverageToReferenceConverter INSTANCE;

    private CoverageToReferenceConverter() {
    }

    public static synchronized CoverageToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CoverageToReferenceConverter();
        }
        return INSTANCE;
    }
    
    @Override
    public Class<? super GridCoverage2D> getSourceClass() {
        return GridCoverage2D.class;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public ReferenceType convert(final GridCoverage2D source, final Map<String, Object> params) throws NonconvertibleObjectException {
        
        if (params.get(TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        if (source == null) {
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof GridCoverage2D) || !(source instanceof Coverage)) {
            throw new NonconvertibleObjectException("The output data is not an instance of GridCoverage2D.");
        }
        
        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        ReferenceType reference = null ;
        
        if (ioType.equals(WPSIO.IOType.INPUT)) {
            reference = new InputReferenceType();
        } else {
            reference = new OutputReferenceType();
        }

        final String encodingStr = (String) params.get(ENCODING);
        final String mimeStr = (String) params.get(MIME) != null ? (String) params.get(MIME) : WPSMimeType.IMG_GEOTIFF.val();
        final WPSMimeType mime = WPSMimeType.customValueOf(mimeStr);
        
        reference.setMimeType(mimeStr);
        reference.setEncoding(encodingStr);
        reference.setSchema((String) params.get(SCHEMA));
       
        final String formatName;
        final String[] formatNames = XImageIO.getFormatNamesByMimeType(mimeStr, true, true);
        formatName = (formatNames.length < 1)? "GEOTIFF" : formatNames[0];
        final String randomFileName = UUID.randomUUID().toString();

        try {
            
            final File imageFile = new File((String) params.get(TMP_DIR_PATH), randomFileName);
            
            if (encodingStr != null && encodingStr.equals(WPSEncoding.BASE64.getValue())) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                CoverageIO.write(source, formatName, baos);
                baos.flush();
                byte[] bytesOut = baos.toByteArray();
                FileUtilities.stringToFile(imageFile, Base64.encodeBytes(bytesOut));
                baos.close();
                
            } else {
                CoverageIO.write(source, formatName, imageFile); 
            }
            
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" +randomFileName);
            
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Error during writing the coverage in the output file.",ex);
        } catch (CoverageStoreException ex) {
            throw new NonconvertibleObjectException("Error during writing the coverage in the output file.",ex);
        }

        return reference;
    }

}
