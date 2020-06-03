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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;
import org.geotoolkit.storage.coverage.PyramidReader;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramids;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileDebugSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedTileDebugSymbolizer>{

    public TileDebugSymbolizerRenderer(SymbolizerRendererService service, CachedTileDebugSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    @Override
    public boolean portray(Resource resource) throws PortrayalException {

        if (resource instanceof MultiResolutionResource) {
            final MultiResolutionResource mrm = (MultiResolutionResource) resource;

            renderingContext.switchToDisplayCRS();
            final Graphics2D graphics = renderingContext.getGraphics();

            final Color textColor = Color.YELLOW;
            final Color bgColor = new Color(0, 0, 0, 150);

            graphics.setStroke(new BasicStroke(1));

            Font font = new Font("Dialog", Font.BOLD, 12);
            FontMetrics fontMetrics = graphics.getFontMetrics(font);

            try {
                final Map.Entry<Envelope, List<Mosaic>> intersect = PyramidReader.intersect(mrm, getRenderingContext().getGridGeometry());
                final List<Mosaic> mosaics = intersect.getValue();
                final Envelope wantedEnv = intersect.getKey();

                for (Mosaic m : mosaics) {
                    final CoordinateReferenceSystem crs = m.getUpperLeftCorner().getCoordinateReferenceSystem();
                    final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(crs);

                    final Rectangle rectangle = Pyramids.getTilesInEnvelope(m, wantedEnv);

                    for (int x=0;x<rectangle.width;x++) {
                        for (int y=0;y<rectangle.height;y++) {
                            final GridGeometry gridgeom = Pyramids.getTileGridGeometry2D(m, new Point(rectangle.x+x, rectangle.y+y), crs2d);
                            Geometry geom = GeometricUtilities.toJTSGeometry(gridgeom.getEnvelope(), GeometricUtilities.WrapResolution.NONE);
                            geom.setUserData(crs2d);
                            geom = JTS.transform(geom, renderingContext.getDisplayCRS());

                            Shape shp = new JTSGeometryJ2D(geom);
                            graphics.draw(shp);

                            final Rectangle bounds = shp.getBounds();
                            final double centerX = bounds.getCenterX();
                            final double centerY = bounds.getCenterY();

                            String mid = m.getIdentifier();
                            if (mid.length() > 10) {
                                mid = mid.substring(0, 9) + "..";
                            }

                            final String mosaicId = "Z: " + mid;
                            final String mosaicScale = "S: " + new DecimalFormat("#0.00000").format(m.getScale());
                            final String strX = "X: "+rectangle.x+x;
                            final String strY = "Y: "+rectangle.y+y;

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
                            g2d.setColor(textColor);

                            {
                                AttributedString as1 = new AttributedString(mosaicId);
                                as1.addAttribute(TextAttribute.FONT, ft);
                                as1.addAttribute(TextAttribute.BACKGROUND, bgColor);
                                g2d.drawString(as1.getIterator(), (float)(centerX - txtbbox.getWidth()/2.0), (float)(centerY - fntHeight));
                            }

                            {
                                AttributedString as1 = new AttributedString(mosaicScale);
                                as1.addAttribute(TextAttribute.FONT, ft);
                                as1.addAttribute(TextAttribute.BACKGROUND, bgColor);
                                txtbbox = fm.getStringBounds(mosaicScale, g2d);
                                g2d.drawString(as1.getIterator(), (float)(centerX - txtbbox.getWidth()/2.0), (float)(centerY));
                            }

                            {
                                AttributedString as1 = new AttributedString(strX);
                                as1.addAttribute(TextAttribute.FONT, ft);
                                as1.addAttribute(TextAttribute.BACKGROUND, bgColor);
                                txtbbox = fm.getStringBounds(strX, g2d);
                                g2d.drawString(as1.getIterator(), (float)(centerX - txtbbox.getWidth()/2.0), (float)(centerY + fntHeight));
                            }

                            {
                                AttributedString as1 = new AttributedString(strY);
                                as1.addAttribute(TextAttribute.FONT, ft);
                                as1.addAttribute(TextAttribute.BACKGROUND, bgColor);
                                txtbbox = fm.getStringBounds(strY, g2d);
                                g2d.drawString(as1.getIterator(), (float)(centerX - txtbbox.getWidth()/2.0), (float)(centerY + 2*fntHeight));
                            }


                        }
                    }
                }

            } catch (DataStoreException | TransformException | FactoryException ex) {
                throw new PortrayalException(ex.getMessage(), ex);
            }
        }

        return false;
    }

    @Override
    public boolean portray(ProjectedCoverage graphic) throws PortrayalException {
        return portray(graphic.getLayer().getResource());
    }

}
