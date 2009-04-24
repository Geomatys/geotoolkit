/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.debug.jfreechart;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotools.data.FeatureSource;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;
import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author sorel
 */
public class ChartFeatureLayerGraphic extends GraphicJ2D{

    protected static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
            new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private final JFreeChart chart;

    private final FeatureMapLayer layer;

    ChartFeatureLayerGraphic(ReferencedCanvas2D canvas, FeatureMapLayer layer){
        super(canvas,layer.getBounds().getCoordinateReferenceSystem());
        this.layer = layer;

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("A", 500d);
        dataset.setValue("B", 1000d);
        dataset.setValue("C", 1000d);

        chart = ChartFactory.createPieChart(null, dataset, false,false, Locale.FRENCH);

        PiePlot plot = ( PiePlot )chart.getPlot();

        //erase the margin, shadow and outline
        plot.setForegroundAlpha(1f);
        plot.setBackgroundAlpha(0f);
        plot.setBaseSectionPaint(new Color(0f,0f,0f,0f));
        plot.setInteriorGap(0);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(new Color(0f,0f,0f,0f));
//        plot.setInsets(new RectangleInsets(0, 0, 0, 0));

        //erase the labels
//        plot.setSimpleLabels(true);
//        plot.setLabelBackgroundPaint(null);
//        plot.setLabelPaint(new Color(0f,0f,0f,0f));
//        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);

        //erase margin, border, legend and title
        chart.setBorderVisible(false);
        chart.setPadding(new RectangleInsets(4, 4, 4, 4));
        chart.setBackgroundPaint(new Color(0f,0f,0f,0f));
        chart.setBorderStroke(new BasicStroke());
        chart.setBackgroundImage(null);
        chart.removeLegend();

    }

    @Override
    public void paint(RenderingContext2D renderingContext) {

        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) return;


        final FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer.getFeatureSource();
        final FeatureType schema                                 = fs.getSchema();
        final String geomAttName                                 = schema.getGeometryDescriptor().getLocalName();
        BoundingBox bbox                                         = renderingContext.getPaintingObjectiveBounds();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final CanvasMonitor monitor                              = renderingContext.getMonitor();
        final ReferencedEnvelope layerBounds                     = layer.getBounds();

        if( !CRS.equalsIgnoreMetadata(layerBounds.getCoordinateReferenceSystem(),bboxCRS)){
            //BBox and layer bounds have different CRS. reproject bbox bounds
            Envelope env;

            try{
                env = CRS.transform(bbox, layerBounds.getCoordinateReferenceSystem());
            }catch(TransformException ex){
                renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
                env = new Envelope2D();
            }

            //TODO looks like the envelope after transform operation doesnt have always exactly the same CRS.
            //fix CRS classes method and remove the two next lines.
            env = new GeneralEnvelope(env);
            ((GeneralEnvelope)env).setCoordinateReferenceSystem(layerBounds.getCoordinateReferenceSystem());

            bbox = new ReferencedEnvelope(env);
        }

        Filter filter;
        if( ((BoundingBox)bbox).contains(layerBounds)){
            //the layer bounds overlaps the bbox, no need for a spatial filter
            filter = Filter.INCLUDE;
        }else{
            //make a bbox filter
            filter = FF.bbox(FF.property(geomAttName),bbox);
        }

        //concatenate geographique filter with data filter if there is one
        if(layer.getQuery() != null && layer.getQuery().getFilter() != null){
            filter = FF.and(filter,layer.getQuery().getFilter());
        }

        if(monitor.stopRequested()) return;

        final FeatureCollection<SimpleFeatureType,SimpleFeature> features;
        try{
            features = fs.getFeatures();
        }catch(IOException ex){
            renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
            //can not continue this layer with this error
            return;
        }

        //we check that we have features left after the user Query
        //if empty we stop this layer rendering
        if(features == null || features.isEmpty()) return;

        renderingContext.switchToDisplayCRS();

        final CoordinateReferenceSystem dataCRS      = features.getSchema().getCoordinateReferenceSystem();
        final CoordinateReferenceSystem displayCRS   = renderingContext.getDisplayCRS();

        final MathTransform dataToDisp;
        try {
            dataToDisp = CRS.findMathTransform(dataCRS, displayCRS,true);
        } catch (FactoryException ex) {
            ex.printStackTrace();
            return;
        }


        // read & paint in the same thread
        final FeatureIterator<SimpleFeature> ite = features.features();
        try{
            while(ite.hasNext()){
                SimpleFeature sf = ite.next();
                Geometry geom = (Geometry) sf.getDefaultGeometry();
                geom = geom.getCentroid();
                Coordinate coord = geom.getCoordinate();
                
                DirectPosition pt2d = new DirectPosition2D(coord.x, coord.y);

                try{ 
                    pt2d = dataToDisp.transform(pt2d,pt2d);
                    double[] coords =  pt2d.getCoordinate();

                    drawChart(renderingContext.getGraphics(),coords[0],coords[1]);
                   

                } catch(TransformException ex){
                    ex.printStackTrace();
                    return;
                }
                
                
            }

        }finally{
            ite.close();
        }


    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        return graphics;
    }


    private void drawChart(Graphics2D g2d, double x, double y){

        chart.draw(g2d, new Rectangle2D.Double(x-40, y-40, 80, 80));
    }

}
