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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
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

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));

        final String randomFileName = UUID.randomUUID().toString();
        GridCoverageWriter writer = null;

        try {
            final File imageFile = new File((String) params.get(TMP_DIR_PATH), randomFileName);
            ImageWriter imgWriter = XImageIO.getWriterByMIMEType(reference.getMimeType(), imageFile, source.getRenderedImage());
            
            //CoverageIO.write(source, reference.getMimeType(), imageFile); 
            writer = CoverageIO.createSimpleWriter(imgWriter);           
            writer.write(source, null);
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" +randomFileName);
            
        } catch (CoverageStoreException ex) {
            throw new NonconvertibleObjectException("Error during writing the coverage in the output file.",ex);
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("No writer found for mime type "+reference.getMimeType(), ex);
        } finally {
            if (writer != null) {
                try {
                    writer.dispose();
                } catch (CoverageStoreException ex) {
                    throw new NonconvertibleObjectException("Error during release the coverage writer.",ex);
                }
            }
        }

        return reference;
    }

}
