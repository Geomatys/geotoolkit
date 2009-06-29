/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.renderer.svg;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.w3c.dom.Document;


/**
 *
 * @author  jamesm
 */
final class InternalTranscoder extends ImageTranscoder {

    private BufferedImage result;

    private Document doc;

    /**
     * Creates a new instance of InternalTranscoder.
     */
    public InternalTranscoder() {
    }

    @Override
    protected void transcode(Document document, String uri, TranscoderOutput output) throws TranscoderException {
        super.transcode(document, uri, output);
        this.doc = document;
    }

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Gets called by the end of the image transcoder with an actual image.
     */
    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output) {
        result = img;
    }

    public BufferedImage getImage(){
        return result;
    }

    public Document getDocument(){
        return doc;
    }
}
