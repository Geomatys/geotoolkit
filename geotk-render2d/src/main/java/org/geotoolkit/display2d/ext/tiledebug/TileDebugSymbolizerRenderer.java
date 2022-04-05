/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.ext.tiledebug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.map.ExceptionPresentation;
import org.apache.sis.internal.map.Presentation;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.ShapePresentation;
import org.geotoolkit.display2d.presentation.TextPresentation2;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;
import org.geotoolkit.storage.coverage.TileMatrixSetCoverageReader;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TiledResource;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Renderer for Tile debug symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TileDebugSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedTileDebugSymbolizer>{

    TileDebugSymbolizerRenderer(SymbolizerRendererService service, CachedTileDebugSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource resource) {

        if (!(resource instanceof TiledResource)) {
            return Stream.empty();
        }

        final TiledResource mrm = (TiledResource) resource;
        final List<Presentation> presentations = new ArrayList<>();

        final Graphics2D graphics = renderingContext.getGraphics();

        final Color textColor = Color.YELLOW;
        final Color bgColor = new Color(0, 0, 0, 150);

        final Stroke stroke = new BasicStroke(1);

        final Font font = new Font("Dialog", Font.BOLD, 12);
        final FontMetrics fontMetrics = graphics.getFontMetrics(font);

        try {
            final Map.Entry<Envelope, List<TileMatrix>> intersect = TileMatrixSetCoverageReader.intersect(mrm, getRenderingContext().getGridGeometry());
            final List<TileMatrix> mosaics = intersect.getValue();
            final Envelope wantedEnv = intersect.getKey();

            for (TileMatrix m : mosaics) {
                final CoordinateReferenceSystem crs = m.getTilingScheme().getCoordinateReferenceSystem();
                final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(crs);

                final GridExtent extent;
                try {
                    extent = TileMatrices.getTilesInEnvelope(m, wantedEnv);
                } catch (NoSuchDataException ex) {
                    continue;
                }

                try (Stream<long[]> indices = TileMatrices.pointStream(extent)) {
                    final Iterator<long[]> iterator = indices.iterator();
                    while (iterator.hasNext()) {
                        long[] indice = iterator.next();

                        final GridGeometry gridgeom = TileMatrices.getTileGridGeometry2D(m, indice, crs2d);
                        Geometry geom = GeometricUtilities.toJTSGeometry(gridgeom.getEnvelope(), GeometricUtilities.WrapResolution.NONE);
                        geom.setUserData(crs2d);
                        geom = org.apache.sis.internal.feature.jts.JTS.transform(geom, renderingContext.getDisplayCRS());

                        Shape shp = new JTSGeometryJ2D(geom);

                        final ShapePresentation border = new ShapePresentation(layer, resource, null);
                        border.stroke = stroke;
                        border.strokePaint = Color.BLACK;
                        border.shape = shp;
                        presentations.add(border);

                        final Rectangle bounds = shp.getBounds();
                        final double centerX = bounds.getCenterX();
                        final double centerY = bounds.getCenterY();

                        String mid = m.getIdentifier().toString();
                        if (mid.length() > 10) {
                            mid = mid.substring(0, 9) + "..";
                        }

                        final String mosaicId = "Z: " + mid;
                        final String mosaicScale = "S: " + new DecimalFormat("#0.00000").format(m.getTilingScheme().getResolution(true)[0]);
                        final String strX = "X: " + (indice[0]);
                        final String strY = "Y: " + (indice[1]);

                        String longest = mosaicId;
                        if (mosaicScale.length() > longest.length()) longest = mosaicScale;
                        if (strX.length() > longest.length()) longest = strX;
                        if (strY.length() > longest.length()) longest = strY;

                        Font ft = font;
                        FontMetrics fm = fontMetrics;
                        while (fm.stringWidth(longest) > bounds.width && ft.getSize() > 8) {
                            ft = new Font(ft.getFamily(), ft.getStyle(), ft.getSize()-1);
                            fm = graphics.getFontMetrics(ft);
                        }
                        graphics.setFont(ft);


                        final double fntHeight = fm.getHeight();

                        Rectangle2D txtbbox = fm.getStringBounds(mosaicId, g2d);

                        {
                            final AttributedString as = new AttributedString(mosaicId);
                            as.addAttribute(TextAttribute.FONT, ft);
                            as.addAttribute(TextAttribute.BACKGROUND, bgColor);

                            final TextPresentation2 tp = new TextPresentation2(layer, resource, null);
                            tp.forGrid(renderingContext);
                            tp.text = as;
                            tp.paint = textColor;
                            tp.x = (float)(centerX - txtbbox.getWidth()/2.0);
                            tp.y = (float)(centerY - fntHeight);
                            presentations.add(tp);
                        }

                        {
                            final AttributedString as = new AttributedString(mosaicScale);
                            as.addAttribute(TextAttribute.FONT, ft);
                            as.addAttribute(TextAttribute.BACKGROUND, bgColor);
                            txtbbox = fm.getStringBounds(mosaicScale, g2d);

                            final TextPresentation2 tp = new TextPresentation2(layer, resource, null);
                            tp.forGrid(renderingContext);
                            tp.text = as;
                            tp.paint = textColor;
                            tp.x = (float)(centerX - txtbbox.getWidth()/2.0);
                            tp.y = (float)(centerY);
                            presentations.add(tp);
                        }

                        {
                            final AttributedString as = new AttributedString(strX);
                            as.addAttribute(TextAttribute.FONT, ft);
                            as.addAttribute(TextAttribute.BACKGROUND, bgColor);
                            txtbbox = fm.getStringBounds(strX, g2d);

                            final TextPresentation2 tp = new TextPresentation2(layer, resource, null);
                            tp.forGrid(renderingContext);
                            tp.text = as;
                            tp.paint = textColor;
                            tp.x = (float)(centerX - txtbbox.getWidth()/2.0);
                            tp.y = (float)(centerY + fntHeight);
                            presentations.add(tp);
                        }

                        {
                            final AttributedString as = new AttributedString(strY);
                            as.addAttribute(TextAttribute.FONT, ft);
                            as.addAttribute(TextAttribute.BACKGROUND, bgColor);
                            txtbbox = fm.getStringBounds(strY, g2d);

                            final TextPresentation2 tp = new TextPresentation2(layer, resource, null);
                            tp.forGrid(renderingContext);
                            tp.text = as;
                            tp.paint = textColor;
                            tp.x = (float)(centerX - txtbbox.getWidth()/2.0);
                            tp.y = (float)(centerY + 2*fntHeight);
                            presentations.add(tp);
                        }

                    }
                }

            }

        } catch (DataStoreException | TransformException | FactoryException ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(resource);
            presentations.add(ep);
        }

        return presentations.stream();
    }

}
