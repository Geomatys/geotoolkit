/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.complex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.iharder.Base64;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.v100.ComplexDataType;

/**
 * Convert an base64 encoded coverage into a GridCoverage2D.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class ComplexToCoverageConverter extends AbstractComplexInputConverter<GridCoverage2D> {

    private static ComplexToCoverageConverter INSTANCE;

    private ComplexToCoverageConverter(){
    }

    public static synchronized ComplexToCoverageConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ComplexToCoverageConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? extends GridCoverage2D> getTargetClass() {
        return GridCoverage2D.class;
    }

    @Override
    public GridCoverage2D convert(ComplexDataType source, Map<String, Object> params) throws NonconvertibleObjectException {
        
        try {
            if (params.get(ENCODING).equals(WPSEncoding.BASE64.getValue())) {
                final List<Object> data = source.getContent();
                if (data.size() != 1) {
                    throw new NonconvertibleObjectException("Only one object in Complex content.");
                }
                final String encodedImage = (String) data.get(0);
                final byte[] byteData = Base64.decode(encodedImage);
                if (byteData != null && byteData.length > 0) {
                    final InputStream is = new ByteArrayInputStream(byteData);
                    if (is != null) {
                        final ImageInputStream inStream = ImageIO.createImageInputStream(is);
                        final ImageReader reader;
                        if (source.getMimeType() != null) {
                            reader = XImageIO.getReaderByMIMEType(source.getMimeType(), inStream, null, null);
                        } else {
                            reader = XImageIO.getReader(inStream, null, Boolean.FALSE);
                        }
                        return (GridCoverage2D) CoverageIO.read(reader);
                    }
                }
                throw new NonconvertibleObjectException("Error during base64 decoding.");
            } else {
                throw new NonconvertibleObjectException("Encoding should be in \"base64\"");
            }
        } catch (CoverageStoreException ex) {
            throw new NonconvertibleObjectException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new NonconvertibleObjectException(ex.getMessage(), ex);
        }
    }
}
