
package org.geotools.display3d.container;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FeatureSource;
import org.geotools.display3d.canvas.A3DCanvas;
import org.geotools.display3d.primitive.A3DGraphic;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class FeatureLayerNode extends A3DGraphic{

    private final FeatureMapLayer layer;

    public FeatureLayerNode(A3DCanvas canvas, FeatureMapLayer layer) {
        this.layer = layer;

        GeometryCoordinateSequenceTransformer dataToObjectiveTransformer = new GeometryCoordinateSequenceTransformer();

        FeatureSource<SimpleFeatureType,SimpleFeature> source = layer.getFeatureSource();
        try {
            dataToObjectiveTransformer.setMathTransform(
                    CRS.findMathTransform(source.getSchema().getCoordinateReferenceSystem(),
                    canvas.getController().getObjectiveCRS(),true));

            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures();

            final FeatureIterator<SimpleFeature> ite = collection.features();

            ColorRGBA isolineColor = new ColorRGBA(0,0.5f,0,1);

            try{
                while(ite.hasNext()){
                    SimpleFeature sf = ite.next();
                    Geometry geom = dataToObjectiveTransformer.transform((Geometry) sf.getDefaultGeometry());

                    if(geom instanceof Polygon || geom instanceof MultiPolygon){
                        this.attachChild(toNodePoly(geom, sf));
                    }else if(geom instanceof LineString ){
                        this.attachChild(toNodeLine((LineString)geom, sf,isolineColor));
                    }else if(geom instanceof MultiLineString ){
                        this.attachChild(toNodeLine((MultiLineString)geom, sf,isolineColor));
                    }

                }
            }finally{
                ite.close();
            }

        } catch (Exception ex) {
            Logger.getLogger(FeatureLayerNode.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private Mesh toNodePoly(Geometry geom,SimpleFeature sf){
        double minz = ((Double)sf.getAttribute("Z_MIN"))/5;
        double maxz = ((Double)sf.getAttribute("Z_MAX"))/5;
        Envelope env = geom.getEnvelopeInternal();
        Box box = new Box(sf.getID(), new Vector3(env.getMinX(), minz, env.getMinY()), new Vector3(env.getMaxX(), maxz, env.getMaxY()));
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();
        return box;
    }

    private Mesh toNodeLine(LineString geom,SimpleFeature sf,ColorRGBA color){
        double z = ((Double)sf.getAttribute("ALTITUDE"))/5;
        
        Node n = new Node();
        
        final Coordinate[] coords = geom.getCoordinates();
        final FloatBuffer verts = BufferUtils.createVector3Buffer((coords.length));
        
        for(Coordinate c : coords){
            verts.put((float)c.x).put((float)z).put((float)c.y);
        }
        
        Line line = new Line("Lines", verts, null, null, null);
        line.getMeshData().setIndexMode(IndexMode.LineStrip);
        line.setLineWidth(0.5f);
        line.setDefaultColor(color);
//        line.setAntialiased(true);
        line.setLightCombineMode(LightCombineMode.Off);
        line.setModelBound(new BoundingSphere());
        line.updateModelBound();

        return line;
    }

    private Node toNodeLine(MultiLineString geom,SimpleFeature sf,ColorRGBA color){

        Node node = new Node();

        for(int i=0,n=geom.getNumGeometries();i<n;i++){
            LineString ln = (LineString) geom.getGeometryN(i);
            node.attachChild(toNodeLine(ln, sf,color));
        }

        return node;
    }


}
