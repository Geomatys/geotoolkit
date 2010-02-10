/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002 - 2008, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.TranscodingHints.Key;
import org.geotoolkit.factory.Hints;

import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SvgUtils {

    private SvgUtils(){}

    public static Image read(Document svgdom, final Dimension dim) throws TranscoderException, IOException{
        return read(svgdom,dim,null);
    }

    public static Image read(InputStream stream, final Dimension dim) throws TranscoderException, IOException{
       return read(stream,dim,null);
    }

    public static Image read(Reader svgfile, final Dimension dim) throws TranscoderException, IOException{
        return read(svgfile,dim,null);
    }

    public static Image read(String svgfile, final Dimension dim) throws TranscoderException, IOException{
        return read(svgfile,dim,null);
    }

    public static Image read(XMLReader svgfile, final Dimension dim) throws TranscoderException, IOException{
        return read(svgfile,dim,null);
    }

    public static Image read(Document svgdom, final Dimension dim, RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgdom);
        return read(in, dim, hints);
    }

    public static Image read(InputStream stream, final Dimension dim, RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(stream);
        return read(in, dim, hints);
    }

    public static Image read(Reader svgfile, final Dimension dim, RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgfile);
        return read(in, dim, hints);
    }
    
    public static Image read(String svgfile, final Dimension dim, RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgfile);
        return read(in, dim, hints);
    }

    public static Image read(XMLReader svgfile, final Dimension dim, RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgfile);
        return read(in, dim, hints);
    }

    public static Image read(TranscoderInput in, Dimension dim, RenderingHints hints) throws TranscoderException, IOException{

        if(dim.height <=0 || dim.width <=0){
            throw new IllegalArgumentException("Height and width must be superior to 0");
        }
        final InternalTranscoder svgTranscoder = new InternalTranscoder();
        TranscoderOutput out                   = new TranscoderOutput();

        if(hints != null){
            for(Object key : hints.keySet()){
                if(key instanceof TranscodingHints.Key){
                    svgTranscoder.addTranscodingHint((Key) key,hints.get(key));
                }
            }
        }

        svgTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, new Float(dim.height));
        svgTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, new Float(dim.width));
        svgTranscoder.transcode(in, out);

        return svgTranscoder.getImage();
    }

}
