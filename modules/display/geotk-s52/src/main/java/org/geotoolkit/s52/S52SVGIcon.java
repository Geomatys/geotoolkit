/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMLinearGradientElement;
import org.apache.batik.dom.svg.SVGOMRectElement;
import org.apache.batik.dom.svg.SVGOMStopElement;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

/**
 * Parse SVG, adds derivate capabilities for S-52 palette.
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52SVGIcon {

    private float canvasWidth;
    private float canvasHeight;
    private float pivotX;
    private float pivotY;
    private SVGDocument doc;
    private final GraphicsNode node;

    public S52SVGIcon(final String uri) throws IOException {

        //parse svg
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        doc = (SVGDocument)factory.createSVGDocument(uri);

        //create the rendering node
        final UserAgent userAgent   = new UserAgentAdapter();
        final DocumentLoader loader = new DocumentLoader(userAgent);
        final BridgeContext ctx     = new BridgeContext(userAgent, loader);
        ctx.setDynamic(true);
        final GVTBuilder builder = new GVTBuilder();
        this.node = builder.build(ctx, doc);

        //parse
        canvasWidth  = doc.getRootElement().getWidth().getBaseVal().getValue();
        canvasHeight = doc.getRootElement().getHeight().getBaseVal().getValue();
        final SVGOMRectElement element = (SVGOMRectElement)doc.getElementById("pivot");
        pivotX = element.getX().getBaseVal().getValue();
        pivotY = element.getY().getBaseVal().getValue();
    }

    private S52SVGIcon(S52SVGIcon icon){
        this.canvasWidth = icon.canvasWidth;
        this.canvasHeight = icon.canvasHeight;
        this.pivotX = icon.pivotX;
        this.pivotY = icon.pivotY;
        this.doc = (SVGDocument)icon.doc.cloneNode(true);

        //create the rendering node
        final UserAgent userAgent   = new UserAgentAdapter();
        final DocumentLoader loader = new DocumentLoader(userAgent);
        final BridgeContext ctx     = new BridgeContext(userAgent, loader);
        ctx.setDynamic(true);
        final GVTBuilder builder = new GVTBuilder();
        this.node = builder.build(ctx, this.doc);
    }

    /**
     * Create derivate icon replacing colors by those in given palette.
     * @param palette
     * @return
     */
    public S52SVGIcon derivate(final S52Palette palette){
        final S52SVGIcon derivate = new S52SVGIcon(this);
        derivate.update(palette);
        return derivate;
    }

    /**
     * Update SVG with palette colors.
     * @param palette not null
     */
    private void update(final S52Palette palette){
        final NodeList gradients = doc.getElementsByTagName("linearGradient");
        for(int k=0,n=gradients.getLength();k<n;k++){
            final SVGOMLinearGradientElement lg = (SVGOMLinearGradientElement) gradients.item(k);
            final String colorId = lg.getId();
            final String colorValue = palette.getColorHexa(colorId);
            if(colorValue==null){
                //stay with the default color
                continue;
            }

            //change gradient stop colors.
            final NodeList stops = lg.getElementsByTagName("stop");
            for(int i=0,j=stops.getLength();i<j;i++){
                final SVGOMStopElement stop = (SVGOMStopElement) stops.item(i);
                stop.getStyle().setProperty("stop-color", colorValue, "");
            }
        }
    }

    public float getCanvasHeight() {
        return canvasHeight;
    }

    public float getCanvasWidth() {
        return canvasWidth;
    }

    public float getPivotX() {
        return pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public void paint(final Graphics2D g, Point2D position, float rotation, float scale) {
        final AffineTransform old = g.getTransform();

        position.setLocation(position.getX()+pivotX, position.getY()+pivotY);
        final AffineTransform trs = new AffineTransform();
        trs.translate(position.getX(), position.getY());
        trs.scale(scale, scale);
        trs.rotate(rotation);
        trs.translate(-position.getX(), -position.getY());
        node.paint(g);
        g.setTransform(old);
    }

}
