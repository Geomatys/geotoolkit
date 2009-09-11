/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.isoline;

import com.vividsolutions.jts.geom.Coordinate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.SI;
import javax.media.jai.DataBufferDouble;
import javax.vecmath.Point3d;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.display2d.style.j2d.TextStroke;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.util.logging.Logging;

import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Sorel Johann (Geomatys)
 */
public class IsolineGraphicJ2D extends StatelessFeatureLayerJ2D {

    private static final Logger LOGGER = Logging.getLogger(IsolineGraphicJ2D.class);

    private static final Font FONT = new Font("Arial", Font.BOLD, 13);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color HALO_COLOR = Color.WHITE;
    private static final Stroke LINE_STROKE = new BasicStroke(1);

    private final ValueExtractor extractor;

    public IsolineGraphicJ2D(ReferencedCanvas2D canvas, FeatureMapLayer layer, ValueExtractor extractor) {
        super(canvas, layer);
        this.extractor = extractor;
    }

    @Override
    public void paint(RenderingContext2D context) {

        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) {
            return;
        }

        Date end = context.getCanvas().getController().getTemporalRange()[1];
        if (end == null) {
            end = new Date();
        }

        //search obeservation from last hour to now
        Date start = new Date(end.getTime() - 1000L*60L*60L);

        final Graphics2D g2 = context.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        final AffineTransform objToDisp = context.getObjectiveToDisplay();

        try {
            final FeatureCollection<SimpleFeatureType, SimpleFeature> collection =
                    layer.getFeatureSource().getFeatures(layer.getQuery());

            final FeatureIterator<SimpleFeature> iterator = collection.features();
            final List<Coordinate> coords = new ArrayList<Coordinate>();
            double minx = Double.POSITIVE_INFINITY;
            double miny = Double.POSITIVE_INFINITY;
            double maxx = Double.NEGATIVE_INFINITY;
            double maxy = Double.NEGATIVE_INFINITY;
            try {
                while (iterator.hasNext()) {
                    final SimpleFeature feature = iterator.next();
                    final double[] vals = extractor.getValues(context, feature);

                    if(vals == null) continue;
                    if(vals[0]<minx) minx = vals[0];
                    if(vals[0]>maxx) maxx = vals[0];
                    if(vals[1]<miny) miny = vals[1];
                    if(vals[1]>maxy) maxy = vals[1];

                    coords.add( new Coordinate(vals[0],vals[1],vals[2]) );
                }
            }catch(IOException ex){
                LOGGER.log(Level.WARNING, "Could not calculate isoline matrice",ex);
            }finally {
                collection.close(iterator);
            }

            final int s = coords.size();

            
            final Integer iStep = (Integer) ((ContextContainer2D)context.getCanvas().getContainer())
                    .getContext().getUserPropertie(IsolineGraphicBuilder.STEP_PROPERTY);
            final int step = (iStep != null) ? iStep : 5 ;

            final Rectangle2D rect = new Rectangle2D.Double(minx,miny,maxx-minx,maxy-miny);
            final Envelope refEnv = layer.getBounds();
            final double[] x = new double[s];
            final double[] y = new double[s];
            final double[] z = new double[s];

            for(int i=0;i<s;i++){
                Coordinate c = coords.get(i);
                x[i] = c.x;
                y[i] = c.y;
                z[i] = c.z;
            }

            if(coords.size() < 2) return;

            final ObjectiveAnalysis ob = new ObjectiveAnalysis(rect, new Dimension(s*s, s*s));
            
            final double[] computed;
            try{
                computed = ob.interpole(x, y, z);
            }catch(Exception ex){
                ex.printStackTrace();
                return;
            }
            final double[] cx = ob.getXs();
            final double[] cy = ob.getYs();

            double zmax = Double.NEGATIVE_INFINITY;
            double zmin = Double.POSITIVE_INFINITY;
            for(int i=0;i<s;i++){
                if(z[i] > zmax) zmax = z[i];
                if(z[i] < zmin) zmin = z[i];
            }

            GeneralEnvelope env = new GeneralEnvelope(new Rectangle2D.Double(cx[0], cy[0], cx[cx.length-1]-cx[0], cy[cy.length-1]-cy[0]));
            env.setCoordinateReferenceSystem(refEnv.getCoordinateReferenceSystem());

            GridCoverage2D coverage = toCoverage(computed, cx, cy, env, zmin, zmax);
            coverage = coverage.view(ViewType.RENDERED);

            context.switchToObjectiveCRS();
            g2.drawRenderedImage(coverage.getRenderedImage(),(AffineTransform) coverage.getGridGeometry().getGridToCRS());

            context.switchToDisplayCRS();


            final double[] palier = new double[(int)(zmax/step)];
            for(int i=0;i<palier.length;i++){
                palier[i] = i*step;
            }

            final Map<Point3d,List<Coordinate>> steps = ob.doContouring(cx, cy, computed,palier);
            for(final Point3d p : steps.keySet()){

                final List<Coordinate> cshps = steps.get(p);

                GeneralPath isoline = null;
                if(cshps.get(0).x <= cshps.get(cshps.size()-1).x){
                    //the coordinates are going right, correct
                    for(final Coordinate coord : cshps){
                        if(isoline == null){
                            isoline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                            isoline.moveTo(coord.x, coord.y);
                        }else{
                            isoline.lineTo(coord.x, coord.y);
                        }
                    }
                }else{
                    //the coordinates are going left, reverse order
                    for(int i=cshps.size()-1;i>=0;i--){
                        final Coordinate coord = cshps.get(i);
                        if(isoline == null){
                            isoline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                            isoline.moveTo(coord.x, coord.y);
                        }else{
                            isoline.lineTo(coord.x, coord.y);
                        }
                    }
                }

                Shape display = objToDisp.createTransformedShape(isoline);
                
                //line
                g2.setColor(TEXT_COLOR);
                g2.setStroke(LINE_STROKE);
                g2.draw(display);

                final TextStroke stroke = new TextStroke(String.valueOf(p.z), FONT, true, 0, 0, 600);
                final Shape shape = stroke.createStrokedShape(display);

                //paint halo
                g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND) );
                g2.setPaint(HALO_COLOR);
                g2.draw(shape);

                //paint text
                g2.setStroke(new BasicStroke(0));
                g2.setPaint(TEXT_COLOR);
                g2.fill(shape);
            }

        } catch (IOException ex) {
            Logger.getLogger(IsolineGraphicJ2D.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static GridCoverage2D toCoverage(final double[] computed, final double[] xs, final double[] ys,
            final Envelope env, double zmin, double zmax){

        final float[] zs = new float[xs.length*ys.length];

//        flip the matrix on y axi
        for(int i=0;i<xs.length;i++){
            for(int j=0;j<ys.length;j++){
                zs[i + (ys.length-1-j) * xs.length] = (float) computed[i + j * xs.length];
            }
        }

//        flip the matrix order on x axi
//        for(int i=0;i<xs.length;i++){
//            for(int j=0;j<ys.length;j++){
//                zs[j + (xs.length-1-i) * xs.length] = (float) computed[i + j * xs.length];
//            }
//        }

        final DataBufferFloat buffer = new DataBufferFloat(zs, zs.length);
        final SampleModel model = new BandedSampleModel(DataBufferDouble.TYPE_FLOAT, xs.length, ys.length, 1);

        final WritableRaster raster = Raster.createWritableRaster(model, buffer, new Point(0,0));

//        final Category cat = new Category("elevation",
//                new Color[]{
//                    new Color(0f,0f,1f,0.8f),
//                    new Color(0f,1f,1f,0.5f),
//                    new Color(1f,1f,1f,0f),
//                    new Color(1f,1f,0f,0.5f),
//                    new Color(1f,0f,0f,0.8f)},
//                NumberRange.create(0, 255),
//                NumberRange.create(zmin, zmax));

        if(zmax <= zmin) zmax = zmin+1;

        final Category cat = new Category("elevation",
                new Color[]{
                    new Color(1f,1f,1f,0.01f),
                    new Color(0f,1f,0f,0.2f),
                    new Color(1f,1f,0f,0.4f),
                    new Color(1f,0.5f,0f,0.6f),
                    new Color(1f,0f,0f,0.7f)},
                NumberRange.create(0, 255),
                NumberRange.create(0, 30));

        final GridSampleDimension dim = new GridSampleDimension("elevation", new Category[]{cat},SI.METER).geophysics(true);

        final GridCoverageFactory gcf = new GridCoverageFactory();
        return gcf.create("elevationgrid", raster, env, new GridSampleDimension[]{dim});
    }

}
