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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataOutput;
import org.geotoolkit.wps.xml.v200.Format;

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
    public RenderedImage fromWPS2Input(DataOutput candidate) throws UnconvertibleObjectException {
        if (candidate.getReference() != null) {
            return super.fromWPS2Input(candidate);
        }

        final Data data = candidate.getData();
        if (data == null) throw new UnconvertibleObjectException();
        final String mimeType = data.getMimeType();
        if (mimeType == null) throw new UnconvertibleObjectException();
        if (data.getContent().size() != 1) throw new UnconvertibleObjectException();
        Object cdt = data.getContent().get(0);
        if (cdt instanceof String) {
            //base64 encoded image
            byte[] rawData = Base64.getDecoder().decode(cdt.toString());
            try {
                return ImageIO.read(new ByteArrayInputStream(rawData));
            } catch (IOException ex) {
                throw new UnconvertibleObjectException(ex.getMessage(), ex);
            }
        } else {
            throw new UnconvertibleObjectException();
        }
    }



    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();
            final String schema = format.getSchema();

            if (encoding != null || schema != null) {
                return null;
            }

            final List<String> types = getCleanMimeTypeList();
            if (types.contains(mimeType)) {
                return new ImageAdaptor(mimeType);
            } else {
                return null;
            }
        }

        /**
         * Return a list of spported MIME type starting with the prefix "image"
         * to avoid annoying match like text/plain.
         *
         * @return
         */
        private List<String> getCleanMimeTypeList() {
            List<String> results = new ArrayList<>();
            final String[] types = ImageIO.getReaderMIMETypes();
            for (String type : types) {
                if (type.startsWith("image")) {
                    results.add(type);
                }
            }
            return results;
        }

    }

}
