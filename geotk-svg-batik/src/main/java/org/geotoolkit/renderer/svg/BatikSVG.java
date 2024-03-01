/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGStyleSheetProcessingInstruction;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DefaultExternalResourceSecurity;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.XMLResourceDescriptor;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * Decorates a Batik SVG document and provide rendering utilities.
 *
 * Note on Batik :
 * - batik draw the icon in a rectangle starting at 0,0 with with and height of the SVG document.
 * - the SVG viewbox is stretched in this rectangle
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BatikSVG implements AutoCloseable {

    //configurable properties
    private final String uri;
    private float pixelToMm = Float.NaN;
    private String cssPath;
    private String cssData;
    private boolean inheritCss;

    //caches
    private SVGDocument document;
    private BridgeContext bridge;
    private GraphicsNode node;
    private FileSystem cssFileSystem;

    public BatikSVG(final String uri) {
        this.uri = uri;
    }

    /**
     * @return file URI
     */
    public String getURI() {
        return uri;
    }

    /**
     * Set pixels per millimeters factor.
     * This method must be called before getGraphicsNode or it won't have any effect.
     *
     * @param pixelToMm pixel to millimeter factor.
     */
    public void setPixelToMm(float pixelToMm) {
        this.pixelToMm = pixelToMm;
    }

    /**
     * Define a substitution CSS file.
     * This method must be called before getGraphicsNode or it won't have any effect.
     *
     * @param css file path to use
     */
    public void setStyleSheetPath(String css) {
        this.cssPath = css;
    }

    /**
     * Define a substitution CSS file content which will be place aside the SVG.
     * This method must be called before getGraphicsNode or it won't have any effect.
     *
     * @param css CSS file content
     * @param inherit true to inherit existing CSS otherwise replace it completely.
     *        An import instruction will be added at the begining of CSS content
     */
    public void setStyleSheetContent(String css, boolean inherit) {
        this.cssData = css;
        this.inheritCss = inherit;
    }

    /**
     * Parse and return SVG document.
     *
     * @return batik SVG document
     * @throws IOException if SVG parsing fails
     */
    public synchronized SVGDocument getDocument() throws IOException {
        if (document == null) {
            final String parser = XMLResourceDescriptor.getXMLParserClassName();
            final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            document = (SVGDocument)factory.createDocument(uri);
        }
        return document;
    }

    /**
     * Create GraphicsNode from SVG document.
     *
     * @return Batik GraphicsNode
     * @throws IOException if SVG parsing fails
     */
    public synchronized GraphicsNode getGraphicsNode() throws IOException {
        if (node == null) {
            final SVGDocument document = getDocument();

            if (cssPath != null) {
                final Node node = document.getFirstChild();
                if (node instanceof SVGStyleSheetProcessingInstruction cssdef) {
                    cssdef.setData("href=\"" + cssPath + "\" type=\"text/css\"");
                } else {
                    throw new IOException("SVG do not declare an external CSS file");
                }

            }

            if (cssData != null) {
                final Node node = document.getFirstChild();
                if (node instanceof SVGStyleSheetProcessingInstruction cssdef) {
                    cssFileSystem = Jimfs.newFileSystem(UUID.randomUUID().toString(), Configuration.unix());
                    final Path cssFile = cssFileSystem.getPath("replace.css");

                    if (inheritCss) {
                        //data is like : href="style.css" type="text/css"
                        String previousCssPath = cssdef.getData().split("\"")[1];
                        //convert path to absolute
                        previousCssPath = new URL(new URL(uri), previousCssPath).toString();

                        Files.writeString(cssFile, "@import url(\"" + previousCssPath + "\");\n" + cssData);
                        cssdef.setData("href=\"" + cssFile.toUri().toString() + "\" type=\"text/css\"");
                    } else {
                        Files.writeString(cssFile, cssData);
                        cssdef.setData("href=\"" + cssFile.toUri().toString() + "\" type=\"text/css\"");
                    }
                } else {
                    throw new IOException("SVG do not declare an external CSS file");
                }
            }

            final UserAgent userAgent = new UserAgentAdapter() {
                @Override
                public float getPixelUnitToMillimeter() {
                    return Float.isNaN(pixelToMm) ? super.getPixelUnitToMillimeter() : pixelToMm;
                }

                @Override
                public ExternalResourceSecurity getExternalResourceSecurity(ParsedURL resourceURL, ParsedURL docURL) {
                    if (resourceURL.getProtocol().equals("jimfs")) {
                        //allow this protocol
                        return () -> {};
                    }
                    return new DefaultExternalResourceSecurity(resourceURL, docURL);
                }
            };
            final DocumentLoader loader = new DocumentLoader(userAgent);
            bridge = new BridgeContext(userAgent, loader);
            bridge.setDynamic(true);
            final GVTBuilder builder = new GVTBuilder();
            this.node = builder.build(bridge, document);
        }
        return node;
    }

    /**
     * Get SVG document size.
     *
     * @return svg document size.
     * @throws IOException
     */
    public DoubleDimension2D getDocumentSize() throws IOException {
        //force loading
        final GraphicsNode graphicsNode = getGraphicsNode();
        final Dimension2D documentSize = bridge.getDocumentSize();
        final double docWidth = documentSize.getWidth();
        final double docHeight = documentSize.getHeight();
        return new DoubleDimension2D(docWidth, docHeight);
    }

    /**
     * Get SVG document view box, if any.
     *
     * @return svg document view box or null
     * @throws IOException
     */
    public Rectangle2D.Double getViewBox() throws IOException {
        final SVGSVGElement rootElement = getDocument().getRootElement();
        final String[] viewbox = rootElement.getAttribute("viewBox").split(" ");
        if (viewbox.length == 4) {
            final double vbx = Double.parseDouble(viewbox[0]);
            final double vby = Double.parseDouble(viewbox[1]);
            final double vbw = Double.parseDouble(viewbox[2]);
            final double vbh = Double.parseDouble(viewbox[3]);
            return new Rectangle2D.Double(vbx, vby, vbw, vbh);
        }
        return null;
    }

    /**
     * Paint SVG, the top left corner of the SVG document will be at graphics location.
     *
     * @param graphics to paint with
     * @throws IOException if SVG parsing fails
     */
    public void paint(final Graphics2D graphics) throws IOException {
        getGraphicsNode().paint(graphics);
    }

    /**
     * Paint SVG, the top left corner of the SVG document will be at graphics location.
     *
     * @param graphics to paint with
     * @param dim wanted size of SVG
     * @throws IOException if SVG parsing fails
     */
    public void paint(final Graphics2D graphics, final Point2D dim) throws IOException {
        final DoubleDimension2D documentSize = getDocumentSize();
        final GraphicsNode graphicsNode = getGraphicsNode();
        final double scaleX = dim.getX() / documentSize.getWidth();
        final double scaleY = dim.getY() / documentSize.getHeight();
        graphics.scale(scaleX, scaleY);
        try {
            graphicsNode.paint(graphics);
        } finally {
            graphics.scale(1 / scaleX, 1 / scaleY);
        }
    }

    @Override
    public void close() throws IOException {
        if (cssFileSystem != null) {
            cssFileSystem.close();
        }
    }

}
