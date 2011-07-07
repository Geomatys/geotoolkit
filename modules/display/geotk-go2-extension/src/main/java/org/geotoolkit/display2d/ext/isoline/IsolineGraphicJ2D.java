/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.media.jai.DataBufferDouble;
import javax.vecmath.Point3d;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.statefull.StatefullCoverageLayerJ2D;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.style.MutableStyle;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Sorel Johann (Geomatys)
 * @module pending
 */
public class IsolineGraphicJ2D extends StatelessFeatureLayerJ2D {

    private static final GeometryFactory GF = new GeometryFactory();

    private final ValueExtractor extractor;
    private final SimpleFeatureBuilder featureBuilder;
    private MutableStyle isoPointStyle = null;
    private MutableStyle isoLineStyle = null;
    private MutableStyle coverageStyle = null;
    private boolean interpolateCoverageColor = true;
    private int step = 10;

    public IsolineGraphicJ2D(final J2DCanvas canvas, final FeatureMapLayer layer, final ValueExtractor extractor) {
        super(canvas, layer);
        this.extractor = extractor;

        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("isoline");
        sftb.add("geometry", LineString.class, layer.getCollection().getFeatureType().getCoordinateReferenceSystem());
        sftb.add("value", Double.class);
        sftb.setDefaultGeometry("geometry");
        this.featureBuilder = new SimpleFeatureBuilder(sftb.buildSimpleFeatureType());
    }

    public void setStep(final int step) {
        this.step = step;
    }

    public double getStep() {
        return step;
    }

    public void setCoverageStyle(final MutableStyle coverageStyle) {
        this.coverageStyle = coverageStyle;
    }

    public void setIsoLineStyle(final MutableStyle isoLineStyle) {
        this.isoLineStyle = isoLineStyle;
    }
    
    public void setIsoPointStyle(final MutableStyle isoPointStyle) {
        this.isoPointStyle = isoPointStyle;
    }

    public MutableStyle getCoverageStyle() {
        return coverageStyle;
    }

    public MutableStyle getIsoLineStyle() {
        return isoLineStyle;
    }
    
    public MutableStyle getIsoPointStyle() {
        return isoPointStyle;
    }

    public void setInterpolateCoverageColor(final boolean interpolateCoverageColor) {
        this.interpolateCoverageColor = interpolateCoverageColor;
    }

    public boolean isInterpolateCoverageColor() {
        return interpolateCoverageColor;
    }

    @Override
    public void paintLayer(final RenderingContext2D context) {

        //we abort painting if the layer is not visible.
        if (!item.isVisible()) {
            return;
        }
        
        final CanvasMonitor monitor = context.getMonitor();
        
        final Graphics2D g2 = context.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        FeatureCollection<SimpleFeature> collection =
                    (FeatureCollection<SimpleFeature>) item.getCollection();
        try {
            collection = collection.subCollection(item.getQuery());
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }
        

        try {
            final FeatureIterator<SimpleFeature> iterator = collection.iterator();
            final Set<Coordinate> coords = new HashSet<Coordinate>();
            double minx = Double.POSITIVE_INFINITY;
            double miny = Double.POSITIVE_INFINITY;
            double maxx = Double.NEGATIVE_INFINITY;
            double maxy = Double.NEGATIVE_INFINITY;
            double minz = Double.POSITIVE_INFINITY;
            double maxz = Double.NEGATIVE_INFINITY;
            try {
                while (iterator.hasNext()) {
                    final SimpleFeature feature = iterator.next();
                    final Coordinate coord = extractor.getValues(context, feature);

                    if(coord == null) continue;
                    if(coord.x<minx) minx = coord.x;
                    if(coord.x>maxx) maxx = coord.x;
                    if(coord.y<miny) miny = coord.y;
                    if(coord.y>maxy) maxy = coord.y;
                    if(coord.z<minz) minz = coord.z;
                    if(coord.z>maxz) maxz = coord.z;
                    
                    coords.add(coord);
                    System.out.println(coord);
                }
            }catch(Exception ex){
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }finally {
                iterator.close();
            }

            final int s = coords.size();
            final Rectangle2D rect = new Rectangle2D.Double(minx,miny,maxx-minx,maxy-miny);
            final Envelope refEnv = item.getBounds();
            final double[] x = new double[s];
            final double[] y = new double[s];
            final double[] z = new double[s];

            int index=0;
            for(Coordinate c : coords){
                x[index] = c.x;
                y[index] = c.y;
                z[index] = c.z;
                index++;
            }

            if(coords.size() < 2) return;

            final ObjectiveAnalysis ob = new ObjectiveAnalysis(rect, new Dimension(s*s, s*s));
            
            final double[] computed;
            try{
                computed = ob.interpole(x, y, z);
            }catch(Exception ex){
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }
            final double[] cx = ob.getXs();
            final double[] cy = ob.getYs();

            //calculate the coverage -------------------------------------------
            if(coverageStyle != null || interpolateCoverageColor){
                final GeneralEnvelope env = new GeneralEnvelope(
                        new Rectangle2D.Double(cx[0], cy[0], cx[cx.length-1]-cx[0], cy[cy.length-1]-cy[0]));
                env.setCoordinateReferenceSystem(refEnv.getCoordinateReferenceSystem());

                GridCoverage2D coverage = toCoverage(computed, cx, cy, env, minz, maxz);

                if(interpolateCoverageColor){
                    coverage = coverage.view(ViewType.RENDERED);

                    final CoverageMapLayer covlayer = MapBuilder.createCoverageLayer(
                            coverage, GO2Utilities.STYLE_FACTORY.style(GO2Utilities.STYLE_FACTORY.rasterSymbolizer()), "test");
                    final StatefullCoverageLayerJ2D graphic = new StatefullCoverageLayerJ2D(getCanvas(), covlayer);
                    graphic.paint(context);
                }else{
                    final CoverageMapLayer covlayer = MapBuilder.createCoverageLayer(coverage, coverageStyle, "test");
                    final StatefullCoverageLayerJ2D graphic = new StatefullCoverageLayerJ2D(getCanvas(), covlayer);
                    graphic.paint(context);
                }
               
            }

            if(isoLineStyle != null){
                //calculate the isolines -------------------------------------------
                final double[] palier = new double[(int)(maxz/step)];
                for(int i=0;i<palier.length;i++){
                    palier[i] = i*step;
                }
                final Map<Point3d,List<Coordinate>> steps = ob.doContouring(cx, cy, computed, palier);

                //render the isolines ----------------------------------------------
                final FeatureCollection col = DataUtilities.collection(
                        "id", featureBuilder.getFeatureType());
                int inc = 0;

                context.switchToDisplayCRS();
                for(final Point3d p : steps.keySet()){
                    final List<Coordinate> cshps = steps.get(p);

                    if(cshps.get(0).x > cshps.get(cshps.size()-1).x){
                        //the coordinates are going left, reverse order
                        Collections.reverse(cshps);
                    }

                    final LineString geometry = GF.createLineString(cshps.toArray(new Coordinate[cshps.size()]));
                    final double value = p.z;

                    featureBuilder.set("geometry", geometry);
                    featureBuilder.set("value", value);
                    col.add(featureBuilder.buildFeature(String.valueOf(inc++)));
                }

                final FeatureMapLayer flayer = MapBuilder.createFeatureLayer(col, isoLineStyle);
                final StatelessFeatureLayerJ2D graphic = new StatelessFeatureLayerJ2D(getCanvas(), flayer);
                graphic.paint(context);
            }
            
        } catch (DataStoreRuntimeException ex) {
            Logger.getLogger(IsolineGraphicJ2D.class.getName()).log(Level.WARNING, null, ex);
        }

    }

    private static GridCoverage2D toCoverage(final double[] computed, final double[] xs, final double[] ys,
            final Envelope env, final double zmin, double zmax){

//        final int lower = 1;
//        final int upper = 255;
//        double scale = (zmax - zmin) / upper;
//        if(scale == 0) scale = 1;
//        final double offset = zmin - scale*lower;

        final byte[] zs = new byte[xs.length*ys.length];

//        flip the matrix on y axi
        for(int i=0;i<xs.length;i++){
            for(int j=0;j<ys.length;j++){
                double value = computed[i + j * xs.length];
                zs[i + (ys.length-1-j) * xs.length] = (byte) Math.round(value);
            }
        }

//        flip the matrix order on x axi
//        for(int i=0;i<xs.length;i++){
//            for(int j=0;j<ys.length;j++){
//                zs[j + (xs.length-1-i) * xs.length] = (float) computed[i + j * xs.length];
//            }
//        }

        final DataBufferByte buffer = new DataBufferByte(zs, zs.length);
        final SampleModel model = new BandedSampleModel(DataBufferDouble.TYPE_BYTE, xs.length, ys.length, 1);
        final WritableRaster raster = Raster.createWritableRaster(model, buffer, new Point(0,0));

        if(zmax <= zmin) zmax = zmin+1;

        final Category cat = new Category("cat",
                new Color[]{
                    new Color(0f,0f,1f,0.8f),
                    new Color(0f,1f,1f,0.5f),
                    new Color(1f,1f,1f,0f),
                    new Color(1f,1f,0f,0.5f),
                    new Color(1f,0f,0f,0.8f)},
                NumberRange.create(zmin, zmax+1),
                1, 0);

        final GridSampleDimension dim = new GridSampleDimension("cat", 
                new Category[]{cat},SI.METRE);
        final GridCoverageFactory gcf = new GridCoverageFactory();
        return gcf.create("catgrid", raster, env, new GridSampleDimension[]{dim});
    }

}
