/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wps.adaptor;

import java.awt.image.RenderedImage;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.Format;
import org.geotoolkit.wps.xml.ReferenceProxy;
import org.geotoolkit.wps.xml.v100.InputType;

/**
 *
 * @author Johann Sorel
 */
public class ImageAdaptor extends ComplexAdaptor<RenderedImage> {

    private final String mimeType;

    public ImageAdaptor(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public Class<RenderedImage> getValueClass() {
        return RenderedImage.class;
    }

    @Override
    public InputType toWPS1Input(RenderedImage candidate) throws UnconvertibleObjectException {
        if(candidate instanceof ReferenceProxy) return super.toWPS1Input(candidate);

        return InputType.createComplex("", null, mimeType, null, candidate, null, null);
    }

    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();
            final String schema = format.getSchema();

            if (encoding!=null || schema!=null) return null;

            final String[] types = ImageIO.getReaderMIMETypes();

            if (Arrays.asList(types).contains(mimeType)) {
                return new ImageAdaptor(mimeType);
            } else {
                return null;
            }
        }

    }

}
