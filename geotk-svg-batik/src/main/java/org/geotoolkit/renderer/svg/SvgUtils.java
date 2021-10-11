/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Map.Entry;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.TranscodingHints.Key;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.xml.sax.XMLReader;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class SvgUtils {

    private SvgUtils(){}

    public static Image read(final Document svgdom, final Dimension dim) throws TranscoderException, IOException{
        return read(svgdom,dim,null);
    }

    public static Image read(final InputStream stream, final Dimension dim) throws TranscoderException, IOException{
       return read(stream,dim,null);
    }

    public static Image read(final Reader svgfile, final Dimension dim) throws TranscoderException, IOException{
        return read(svgfile,dim,null);
    }

    public static Image read(final String svgfile, final Dimension dim) throws TranscoderException, IOException{
        return read(svgfile,dim,null);
    }

    public static Image read(final XMLReader svgfile, final Dimension dim) throws TranscoderException, IOException{
        return read(svgfile,dim,null);
    }

    public static Image read(final Document svgdom, final Dimension dim, final RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgdom);
        return read(in, dim, hints);
    }

    public static Image read(final InputStream stream, final Dimension dim, final RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(stream);
        return read(in, dim, hints);
    }

    public static Image read(final Reader svgfile, final Dimension dim, final RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgfile);
        return read(in, dim, hints);
    }

    public static Image read(final String svgfile, final Dimension dim, final RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgfile);
        return read(in, dim, hints);
    }

    public static Image read(final XMLReader svgfile, final Dimension dim, final RenderingHints hints) throws TranscoderException, IOException{
        final TranscoderInput in = new TranscoderInput(svgfile);
        return read(in, dim, hints);
    }

    public static Image read(final TranscoderInput in, final Dimension dim, final RenderingHints hints) throws TranscoderException, IOException{

        if(dim.height <=0 || dim.width <=0){
            throw new IllegalArgumentException("Height and width must be superior to 0");
        }
        final BufferedImageTranscoder svgTranscoder = new BufferedImageTranscoder();
        TranscoderOutput out                   = new TranscoderOutput();

        if(hints != null){
            for(Entry<Object,Object> entry : hints.entrySet()){
                final Object key = entry.getKey();
                if(key instanceof TranscodingHints.Key){
                    svgTranscoder.addTranscodingHint((Key) key,entry.getValue());
                }
            }
        }

        svgTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, Float.valueOf(dim.height));
        svgTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, Float.valueOf(dim.width));
        svgTranscoder.transcode(in, out);

        return svgTranscoder.getBufferedImage();
    }

    public static void render(final URI in, final Point2D dim, final Graphics2D g, final RenderingHints hints) throws TranscoderException, IOException{
        if(dim.getY() <=0 || dim.getX() <=0){
            throw new IllegalArgumentException("Height and width must be superior to 0");
        }

        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        final SVGDocument doc = (SVGDocument)factory.createDocument(in.toString());
        final RenderableSVG svg = new RenderableSVG(doc);
        svg.paint(g, dim);
    }

    /**
     * @see http://wiki.apache.org/xmlgraphics-batik/DynamicSvgOffscreen
     */
    private static class RenderableSVG {
        private final GraphicsNode node;
        private Rectangle2D bounds;

        public RenderableSVG(final SVGDocument doc) {
            final UserAgent userAgent   = new UserAgentAdapter();
            final DocumentLoader loader = new DocumentLoader(userAgent);
            final BridgeContext ctx     = new BridgeContext(userAgent, loader);
            ctx.setDynamic(true);
            final GVTBuilder builder = new GVTBuilder();
            this.node = builder.build(ctx, doc);

            final float docWidth  = (float)ctx.getDocumentSize().getWidth();
            final float docHeight = (float)ctx.getDocumentSize().getHeight();
            bounds = new Rectangle2D.Float(0, 0, docWidth, docHeight);

            if (bounds == null){
                bounds = node.getBounds();
            }
        }

        public void paint(final Graphics2D g, final Point2D dim) {
            final double scaleX = dim.getX() / bounds.getWidth();
            final double scaleY = dim.getY() / bounds.getHeight();
            g.scale(scaleX, scaleY);

            try {
                node.paint(g);
            } finally {
                g.scale(1/scaleX, 1/scaleY);
            }
        }

    }

}
