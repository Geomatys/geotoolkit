/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.navigation;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.AbstractMap;
import org.geotoolkit.gui.javafx.render2d.AbstractNavigationHandler;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.javafx.render2d.FXPanMouseListen;
import org.geotoolkit.map.MapLayer;
import org.opengis.display.primitive.Graphic;

/**
 * Panoramic handler
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPanHandler extends AbstractNavigationHandler {

    //we could use this cursor, but java do not handle translucent cursor correctly on every platform
    private static final Cursor CUR_ZOOM_PAN = Cursor.MOVE;
    private final FXPanMouseListen mouseInputListener = new FXPanMouseListen(this) {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (infoOnRightClick && e.getButton() == MouseButton.SECONDARY) {
                final Area searchArea = new Area(new Rectangle((int)e.getX()-2, (int)e.getY()-2, 4, 4));
                final InformationVisitor visitor = new InformationVisitor();
                map.getCanvas().getGraphicsIn(searchArea, visitor, VisitFilter.INTERSECTS);

                if (!visitor.graphics.isEmpty()) {
                    for (Graphic g : visitor.graphics) {
                        print(g, visitor.ctx, visitor.area);
                    }
                }
            }
        }
    };

    private boolean infoOnRightClick;

    public FXPanHandler(boolean infoOnRightClick) {
        super();
        this.infoOnRightClick = infoOnRightClick;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final FXMap component) {
        component.addEventHandler(MouseEvent.ANY, mouseInputListener);
        component.addEventHandler(ScrollEvent.ANY, mouseInputListener);
        super.install(component);
        map.setCursor(CUR_ZOOM_PAN);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean uninstall(final FXMap component) {
        super.uninstall(component);
        component.removeEventHandler(MouseEvent.ANY, mouseInputListener);
        component.removeEventHandler(ScrollEvent.ANY, mouseInputListener);
//        map.setCursor(null);
        return true;
    }

    private void print(Graphic graphic, RenderingContext2D context, SearchAreaJ2D queryArea) {
        if (graphic instanceof ProjectedCoverage) {
            final ProjectedCoverage projectedCoverage = (ProjectedCoverage) graphic;
            final MapLayer layer = projectedCoverage.getLayer();
            final GridCoverageResource ref = (GridCoverageResource) layer.getResource();


            //create envelope around searched area
            final GeneralEnvelope dp = new GeneralEnvelope(context.getCanvasObjectiveBounds());
            final Rectangle2D bounds2D = queryArea.getObjectiveShape().getBounds2D();
            dp.setRange(0, bounds2D.getCenterX(), bounds2D.getCenterX());
            dp.setRange(1, bounds2D.getCenterY(), bounds2D.getCenterY());

            try {
                //slice grid geometry on envelope
                final org.apache.sis.storage.GridCoverageResource gr = (org.apache.sis.storage.GridCoverageResource) ref;
                final GridGeometry gridGeom = gr.getGridGeometry().derive().subgrid(dp).sliceByRatio(0.5, 0, 1).build();
                GridCoverage coverage = gr.read(gridGeom);
                //convert image to geophysic
                coverage = coverage.forConvertedValues(true);

                //pick first slice if several are available
                final GridExtent extent = coverage.getGridGeometry().getExtent();
                final long[] low = new long[extent.getDimension()];
                final long[] high = new long[extent.getDimension()];
                for (int i=0;i<low.length;i++) {
                    low[i] = extent.getLow(i);
                    high[i] = (i>1) ? low[i] : extent.getHigh(i);
                }
                final GridExtent subExt = new GridExtent(null, low, high, true);

                //read samples from image
                final RenderedImage img = coverage.render(subExt);
                final int numBands = img.getSampleModel().getNumBands();
                float[] values = new float[numBands];
                final PixelIterator ite = PixelIterator.create(img);
                if (ite.next()) {
                    ite.getPixel(values);
                } else {
                    return;
                }

                final List<Map.Entry<SampleDimension,Object>> results = new ArrayList<>();
                for (int i=0; i<values.length; i++){
                    final SampleDimension sample = coverage.getSampleDimensions().get(i);
                    results.add(new AbstractMap.SimpleImmutableEntry<SampleDimension, Object>(sample, values[i]));
                }

                for (Map.Entry<SampleDimension,Object> entry : results) {
                    System.out.println(entry.getKey());
                    System.out.println("Value = " + entry.getValue());
                }

            } catch (DisjointExtentException ex) {
                System.out.println("Out of coverage extent.");
            } catch (DataStoreException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println(graphic);
        }
    }

    private static class InformationVisitor implements GraphicVisitor {

        private final List<org.opengis.display.primitive.Graphic> graphics = new ArrayList<>();
        private RenderingContext2D ctx = null;
        private SearchAreaJ2D area = null;

        @Override
        public void startVisit() {
        }

        @Override
        public void endVisit() {
        }

        @Override
        public boolean isStopRequested() {
            return false;
        }

        @Override
        public void visit(org.opengis.display.primitive.Graphic graphic, RenderingContext context, SearchArea area) {
            this.graphics.add(graphic);
            this.ctx = (RenderingContext2D) context;
            this.area = (SearchAreaJ2D) area;
        }
    }


}
