
package org.geotoolkit.pending.demo.rendering.customgraphicbuilder;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.feature.FeatureExt;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;


public class LinksGraphic extends GraphicJ2D{

    private final FeatureMapLayer layer;

    public LinksGraphic(J2DCanvas canvas, FeatureMapLayer layer){
        super(canvas);
        this.layer = layer;
    }

    @Override
    public void paint(RenderingContext2D renderingContext) {
        final CanvasMonitor monitor = renderingContext.getMonitor();
        final Graphics2D g2d = renderingContext.getGraphics();
        
        FeatureCollection collection = layer.getCollection();
        try {
            //we reproject our collection
            collection = collection.subCollection(QueryBuilder.reprojected(
                    collection.getFeatureType().getName().toString(), renderingContext.getObjectiveCRS2D()));
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        g2d.setStroke(new BasicStroke(3));
        renderingContext.switchToDisplayCRS();
        final AffineTransform2D objToDisp = renderingContext.getObjectiveToDisplay();

        final Point2D from = new Point2D.Double(0, 0);
        final Point2D to = new Point2D.Double(0, 0);

        final Color firstColor = Color.RED;

        final FeatureIterator mainIte = collection.iterator();
        try{
            while(mainIte.hasNext()){
                final Feature feature = mainIte.next();

                //draw a line from each point to all other
                final Geometry geom = (Geometry) FeatureExt.getDefaultGeometryAttributeValue(feature);
                final Point center = geom.getCentroid();
                from.setLocation(center.getX(), center.getY());
                objToDisp.transform(from, from);

                final Shape cercle = new java.awt.geom.Ellipse2D.Double(from.getX()-5, from.getY()-5, 10, 10);
                g2d.setStroke(new BasicStroke(4));
                g2d.setColor(Color.WHITE);
                g2d.draw(cercle);
                g2d.setPaint(firstColor);
                g2d.fill(cercle);
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.BLACK);
                g2d.draw(cercle);

                final FeatureIterator ite = collection.iterator();
                try{
                    while(ite.hasNext()){
                        final Feature target = ite.next();
                        if(Math.random() > 0.1d) continue;

                        final Geometry targetgeom = (Geometry) FeatureExt.getDefaultGeometryAttributeValue(target);
                        final Point targetcenter = targetgeom.getCentroid();
                        to.setLocation(targetcenter.getX(), targetcenter.getY());
                        objToDisp.transform(to, to);

                        final Shape line = new java.awt.geom.Line2D.Double(from,to);
                        g2d.setStroke(new BasicStroke( (float)Math.random()*4f));
                        g2d.setPaint(firstColor);
                        g2d.draw(line);
                    }
                }finally{
                    ite.close();
                }

            }
        }finally{
            mainIte.close();
        }

    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        return graphics;
    }

    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public Envelope getEnvelope() {
        return null;
    }

}
