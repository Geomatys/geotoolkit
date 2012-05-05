/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Tube;
import com.ardor3d.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.*;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.logging.Level;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.controller.LocationSensitiveGraphic;
import org.geotoolkit.display3d.geom.DefaultPolygonMesh;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class FeatureLayerNode extends A3DGraphic implements LocationSensitiveGraphic{

    private static final FilterFactory2 FF = new DefaultFilterFactory2();
    private static final int SEARCH_EXTENT = 2000;

    private final FeatureMapLayer layer;

    public FeatureLayerNode(final A3DCanvas canvas, final FeatureMapLayer layer) {
        this(canvas,layer,false);
    }

    public FeatureLayerNode(final A3DCanvas canvas, final FeatureMapLayer layer, final boolean loadAll) {
        super(canvas);
        this.layer = layer;

        if(loadAll){
            loadArea(null);
        }else{
            canvas.getController().addLocationSensitiveGraphic(this, 10);
        }

        //speed up a bit the performances
//        getSceneHints().setDataMode(DataMode.VBOInterleaved);
        
    }

    private Mesh toNodeLine(final LineString geom, final float z ,final ColorRGBA color){
                
        final Coordinate[] coords = geom.getCoordinates();
        final FloatBuffer verts = BufferUtils.createVector3Buffer(coords.length);
        
        for(Coordinate c : coords){
            verts.put((float)c.x).put((float)z).put((float)c.y);
        }
        
        final Line line = new Line("Lines", verts, null, null, null);
        line.getMeshData().setIndexMode(IndexMode.LineStrip);
        line.setLineWidth(0.5f);
        line.setDefaultColor(color);
//        line.setAntialiased(true);
        line.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        line.setModelBound(new BoundingSphere());
        line.updateModelBound();

        return line;
    }

    private Node toNodeLine(final MultiLineString geom, final float z ,final ColorRGBA color){

        final Node node = new Node();

        for(int i=0,n=geom.getNumGeometries();i<n;i++){
            final LineString ln = (LineString) geom.getGeometryN(i);
            node.attachChild(toNodeLine(ln, z,color));
        }

        return node;
    }

    private Mesh toNodePoint(final Point geom,final float z,final ReadOnlyColorRGBA color){
        final Tube cy = new Tube("cy", 4, 5, 10);
        cy.setTranslation(geom.getCoordinate().x, z,geom.getCoordinate().y);
        cy.setDefaultColor(color);
        cy.getSceneHints().setLightCombineMode(LightCombineMode.Off);

        return cy;
    }

    private Node toNodePoint(final MultiPoint geom,final float z,final ReadOnlyColorRGBA color){

        final Node node = new Node();

        for(int i=0,n=geom.getNumGeometries();i<n;i++){
            final Point ln = (Point) geom.getGeometryN(i);
            node.attachChild(toNodePoint(ln, z,color));
        }

        return node;
    }

    @Override
    public void draw(final Renderer r) {

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
    public void update(final ReadOnlyVector3 cameraPosition) {
        final FeatureCollection source = layer.getCollection();

        final DefaultBoundingBox bb = new DefaultBoundingBox(layer.getBounds());
        bb.setRange(0, cameraPosition.getX()-SEARCH_EXTENT, cameraPosition.getX()+SEARCH_EXTENT);
        bb.setRange(1, cameraPosition.getZ()-SEARCH_EXTENT, cameraPosition.getZ()+SEARCH_EXTENT);

        final Filter f = FF.bbox(FF.property(source.getFeatureType().getGeometryDescriptor().getLocalName()),bb);
//            Filter f = FF.dwithin(FF.property(source.getSchema().getGeometryDescriptor().getLocalName()),
//                    FF.literal(new Coordinate(cameraPosition.getX(), cameraPosition.getZ())), searchExtent, "");

        loadArea(f);

    }

    private void loadArea(final Filter filter){
        final List<String> exactList = new ArrayList<String>();

        //HACK TO ENABLE 3D
        final Class geoClass = layer.getCollection().getFeatureType().getGeometryDescriptor().getType().getBinding();

        if(geoClass.equals(Point.class) || geoClass.equals(MultiPoint.class)){
            System.out.println("3d point");
            layer.setElevationModel(MapBuilder.createElevationModel(null, FF.property("ALTITUDE"), FF.literal(0)));
        }else if(geoClass.equals(LineString.class) || geoClass.equals(MultiLineString.class)){
            System.out.println("3d line");
            layer.setElevationModel(MapBuilder.createElevationModel(null, FF.property("ALTITUDE"), FF.literal(0)));
        }else if(geoClass.equals(Polygon.class) || geoClass.equals(MultiPolygon.class)){
            System.out.println("3d polygon");
            layer.setElevationModel(MapBuilder.createElevationModel(null, FF.property("Z_MIN"), FF.literal(0)));
        }else{
            System.out.println("3d else ?");
        }



        FeatureCollection collection = layer.getCollection();
        
        try {            
            collection = collection.subCollection(QueryBuilder.filtered(collection.getFeatureType().getName(), filter));
            collection = GenericReprojectFeatureIterator.wrap(collection, canvas.getObjectiveCRS());

            final FeatureIterator ite = collection.iterator();

            final ColorRGBA isolineColor = new ColorRGBA(0,0.5f,0,1);
            final ReadOnlyColorRGBA pointColor = ColorRGBA.RED;

            final ElevationModel model = layer.getElevationModel();

            try{
                while(ite.hasNext()){
                    final Feature sf = ite.next();
                    final String id = sf.getIdentifier().getID();

                    exactList.add(id);

                    if(loaded.containsKey(id)){
                        //feature is already loaded
                        continue;
                    }

                    synchronized(tounload){
                        final Spatial sp = tounload.get(id);
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


                    final float minz;
                    float maxz;

                    if(model == null){
                        minz = 0;
                        maxz = 1;
                    }else{
                        minz = model.getBaseOffset().evaluate(sf, Float.class);
                        maxz = minz +1;
                        if(sf.getProperty("Z_MAX") != null){
                            maxz = Float.valueOf(sf.getProperty("Z_MAX").toString());
                        }
                    }

                    //feature is not in cache or has been removed, load it
                    final Geometry geom = (Geometry) ((SimpleFeature)sf).getDefaultGeometry();

                    if(geom instanceof Polygon){
                        obj = new DefaultPolygonMesh((Polygon)geom,0,1);
                    }else if(geom instanceof MultiPolygon ){
                        final MultiPolygon multi = (MultiPolygon) geom;
                        final Node mp = new Node();
                        for(int i=0,n=multi.getNumGeometries();i<n;i++){
                            mp.attachChild(new DefaultPolygonMesh((Polygon) multi.getGeometryN(i),minz,maxz));
                        }
                        obj = mp;
                    }else if(geom instanceof LineString ){
                        obj = toNodeLine((LineString)geom, minz,isolineColor);
                    }else if(geom instanceof MultiLineString ){
                        obj = toNodeLine((MultiLineString)geom, minz,isolineColor);
                    }else if(geom instanceof Point ){
                        obj = toNodePoint((Point)geom, minz,pointColor);
                    }else if(geom instanceof MultiPoint ){
                        obj = toNodePoint((MultiPoint)geom, minz,pointColor);
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
            Logging.getLogger(FeatureLayerNode.class).log(Level.WARNING, null, ex);
        }


        //unload uncesseray features
        final Collection<String> keys = new ArrayList(loaded.keySet());
        for(String key : keys){
            if(!exactList.contains(key)){
                final Spatial sp = loaded.get(key);
                cache.put(key, sp);
                synchronized(tounload){
                    tounload.put(key, sp);
                }
                loaded.remove(key);
            }
        }
    }


}
