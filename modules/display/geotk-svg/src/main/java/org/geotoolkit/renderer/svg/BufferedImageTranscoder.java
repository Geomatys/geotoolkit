/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * Basic BufferedImage Transcoder. Inspired by :
 * http://bbgen.net/blog/2011/06/java-svg-to-bufferedimage
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class BufferedImageTranscoder extends ImageTranscoder {

    private BufferedImage img;
    
    @Override
    public BufferedImage createImage(final int w, final int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void writeImage(final BufferedImage img, final TranscoderOutput output) {
        this.img = img;
    }

    public BufferedImage getBufferedImage() {
        return img;
    }
    
}
