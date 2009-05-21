
package org.geotoolkit.display3d.container;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Tube;
import com.ardor3d.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FeatureSource;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.controller.LocationSensitiveGraphic;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.collection.Cache;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class FeatureLayerNode extends A3DGraphic implements LocationSensitiveGraphic{

    private final FeatureMapLayer layer;

    public FeatureLayerNode(A3DCanvas canvas, FeatureMapLayer layer) {
        super(canvas);
        this.layer = layer;

//        LoadingThread loader = new LoadingThread();
//        loader.setPriority(Thread.MIN_PRIORITY);
//        loader.start();

        canvas.getController().addLocationSensitiveGraphic(this, 10);
    }

    private Mesh toNodePoly(Geometry geom,SimpleFeature sf){
        return new DefaultPolygonFeatureMesh2(sf, geom);
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

    private Mesh toNodePoint(Point geom,SimpleFeature sf,ReadOnlyColorRGBA color){

        double z = ((Double)sf.getAttribute("ALTITUDE"))/5;

        Tube cy = new Tube("cy", 4, 5, 10);
        cy.setTranslation(geom.getCoordinate().x, z,geom.getCoordinate().y);
        cy.setDefaultColor(color);
        cy.setLightCombineMode(LightCombineMode.Off);

        return cy;
    }

    private Node toNodePoint(MultiPoint geom,SimpleFeature sf,ReadOnlyColorRGBA color){

        Node node = new Node();

        for(int i=0,n=geom.getNumGeometries();i<n;i++){
            Point ln = (Point) geom.getGeometryN(i);
            node.attachChild(toNodePoint(ln, sf,color));
        }

        return node;
    }

    @Override
    public void draw(Renderer r) {

        //remove old ones
        synchronized(tounload){
            if(!tounload.isEmpty()){
                for (Spatial m : tounload.values()) {
                    m.removeFromParent();
                }
                tounload.clear();
            }
        }

        //add the new ones
        synchronized(toload) {
            if(!toload.isEmpty()){
                for (Spatial m : toload.values()) {
                    this.attachChild(m);
                }
                toload.clear();
            }
        }

        super.draw(r);
    }

    private final Map<String,Spatial> loaded    = new HashMap<String,Spatial>();
    private final Map<String,Spatial> toload    = new HashMap<String,Spatial>();
    private final Map<String,Spatial> tounload  = new HashMap<String, Spatial>();
    private final Cache<String,Spatial> cache   = new Cache<String, Spatial>();
    
    @Override
    public void update(ReadOnlyVector3 cameraPosition) {

        List<String> exactList = new ArrayList<String>();

        GeometryCoordinateSequenceTransformer dataToObjectiveTransformer = new GeometryCoordinateSequenceTransformer();

        FeatureSource<SimpleFeatureType,SimpleFeature> source = layer.getFeatureSource();
        try {
            dataToObjectiveTransformer.setMathTransform(
                    CRS.findMathTransform(source.getSchema().getCoordinateReferenceSystem(),
                    canvas.getController().getObjectiveCRS(),true));

            GeometryFactory gf = new GeometryFactory();

            LinearRing ring = gf.createLinearRing(new Coordinate[]{
               new Coordinate(cameraPosition.getX()-1000,cameraPosition.getZ()-1000),
               new Coordinate(cameraPosition.getX()-1000,cameraPosition.getZ()+1000),
               new Coordinate(cameraPosition.getX()+1000,cameraPosition.getZ()+1000),
               new Coordinate(cameraPosition.getX()+1000,cameraPosition.getZ()-1000),
               new Coordinate(cameraPosition.getX()-1000,cameraPosition.getZ()-1000),
            });

            Geometry fgeom = gf.createPolygon(ring, new LinearRing[0]);

            FilterFactory2 ff = new DefaultFilterFactory2();
            Filter f = ff.intersects(
                    ff.property(source.getSchema().getGeometryDescriptor().getLocalName()),
                    ff.literal(fgeom));

//                f = ff.bbox(source.getSchema().getGeometryDescriptor().getLocalName(),
//                        cameraPosition.getX()-1000,
//                        cameraPosition.getZ()-1000,
//                        cameraPosition.getX()+1000,
//                        cameraPosition.getZ()+1000,
//                        CRS.lookupIdentifier(layer.getBounds().getCoordinateReferenceSystem(),true));


            final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(f);
            final FeatureIterator<SimpleFeature> ite = collection.features();

            ColorRGBA isolineColor = new ColorRGBA(0,0.5f,0,1);
            ReadOnlyColorRGBA pointColor = ColorRGBA.RED;

            try{
                while(ite.hasNext()){
                    final SimpleFeature sf = ite.next();
                    final String id = sf.getID();

                    exactList.add(id);

                    if(loaded.containsKey(id)){
                        //feature is already loaded
                        continue;
                    }

                    synchronized(tounload){
                        Spatial sp = tounload.get(id);
                        if(sp != null){
                            //spatial is on the unloading list
                            //avoid it to be unloaded
                            tounload.remove(id);
                            continue;
                        }
                    }

                    Spatial obj = cache.peek(id);

                    if(obj != null){
                        //feature is in the cache, load it
                        loaded.put(id, obj);
                        synchronized(toload){
                            toload.put(id, obj);
                        }
                        continue;
                    }

                    //feature is not in cache or has been removed, load it
                    final Geometry geom = dataToObjectiveTransformer.transform((Geometry) sf.getDefaultGeometry());

                    if(geom instanceof Polygon){
                        obj = toNodePoly(geom, sf);
                    }else if(geom instanceof MultiPolygon ){
                        MultiPolygon multi = (MultiPolygon) geom;
                        Node mp = new Node();
                        for(int i=0,n=multi.getNumGeometries();i<n;i++){
                            mp.attachChild(toNodePoly(multi.getGeometryN(i), sf));
                        }
                        obj = mp;
                    }else if(geom instanceof LineString ){
                        obj = toNodeLine((LineString)geom, sf,isolineColor);
                    }else if(geom instanceof MultiLineString ){
                        obj = toNodeLine((MultiLineString)geom, sf,isolineColor);
                    }else if(geom instanceof Point ){
                        obj = toNodePoint((Point)geom, sf,pointColor);
                    }else if(geom instanceof MultiPoint ){
                        obj = toNodePoint((MultiPoint)geom, sf,pointColor);
                    }

                    if(obj != null){

                        loaded.put(id, obj);
                        synchronized(toload){
                            toload.put(id, obj);
                        }
                    }

                }
            }finally{
                ite.close();
            }


        } catch (Exception ex) {
            Logger.getLogger(FeatureLayerNode.class.getName()).log(Level.SEVERE, null, ex);
        }


        //unload uncesseray features
        Collection<String> keys = new ArrayList(loaded.keySet());
        for(String key : keys){
            if(!exactList.contains(key)){
                Spatial sp = loaded.get(key);
                cache.put(key, sp);
                synchronized(tounload){
                    tounload.put(key, sp);
                }
                loaded.remove(key);
            }
        }

    }

}
