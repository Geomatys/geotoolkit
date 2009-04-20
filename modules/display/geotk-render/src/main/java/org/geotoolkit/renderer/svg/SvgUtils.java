/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SvgUtils {

    private static SvgUtils INSTANCE = null;

    private SvgUtils(){}

    public Image read(URL svgfile, final Dimension dim) throws TranscoderException, IOException{
    
        if(dim.height <=0 || dim.width <=0){
            throw new IllegalArgumentException("Height and width must be superior to 0");
        }
        final InternalTranscoder svgTranscoder = new InternalTranscoder();
        final TranscoderInput in               = new TranscoderInput(svgfile.openStream());
        TranscoderOutput out                   = new TranscoderOutput();
        
        svgTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, new Float(dim.height));
        svgTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, new Float(dim.width));
        svgTranscoder.transcode(in, out);

        return svgTranscoder.getImage();
    }

    public static SvgUtils getInstance(){

        if(INSTANCE == null){
            INSTANCE = new SvgUtils();
        }

        return INSTANCE;
    }


}
