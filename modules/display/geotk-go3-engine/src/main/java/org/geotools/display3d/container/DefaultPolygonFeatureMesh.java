

package org.geotools.display3d.container;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DefaultPolygonFeatureMesh extends Mesh {

    private static final GeometryFactory GF = new GeometryFactory();

    private final SimpleFeature feature;
    private final double z;
    private Envelope env = null;

    public DefaultPolygonFeatureMesh(SimpleFeature feature, GeometryCoordinateSequenceTransformer dataToObjectiveTransformer,double z) {
        this.feature = feature;
        this.z =z;
        
        try {
            Geometry objGeom = dataToObjectiveTransformer.transform((Geometry)feature.getDefaultGeometry());
//            System.out.println(feature.getID());
            objGeom = DouglasPeuckerSimplifier.simplify(objGeom, 0.01);

            if( objGeom.getCoordinates().length > 0 && (objGeom instanceof Polygon || objGeom instanceof MultiPolygon)){
//            if( "BATIMENT_SURF.5380".equals(feature.getID()) && (objGeom instanceof Polygon || objGeom instanceof MultiPolygon)){
//                preparePolygon(objGeom);
                prepareOther(objGeom);

            }else if(objGeom instanceof LineString || objGeom instanceof MultiLineString){
                prepareOther(objGeom);
            }else{
                prepareOther(objGeom);
            }

        } catch (Exception ex) {
            Logger.getLogger(ContextNode.class.getName()).log(Level.SEVERE, null, ex);
        }

//        setModelBound(new BoundingSphere());
//        updateModelBound();
        setLightCombineMode(Spatial.LightCombineMode.Off);
//        setRandomColors();
        setDefaultColor(ColorRGBA.randomColor(null));


    }


//    private void preparePolygon(final Geometry objGeom){
//
////        final Coordinate[] coords = objGeom.getCoordinates();
////        Point3dObject3d [] pts3 = new Point3dObject3d[coords.length-1];
////        for (int i = 0; i < coords.length-1; i++) {
////            Coordinate coordinate = coords[i];
////            pts3[i] = new Point3dObject3d(new Point3d(coordinate.x, coordinate.y, 0));
////        }
////
////        System.out.println("pts " + pts3.length);
////
////        HullAlgorithm algo = new Incremental(pts3);
////
////        Object3dList list = algo.build();
////        System.out.println("lst " + list.size());
////        final List<Coordinate[]> allTriangles = new ArrayList<Coordinate[]>();
////        for(int i=0;i<list.size();i++){
////            Object3d tri = list.elementAt(i);
////            if(tri != null && tri instanceof Triangle3d){
////                Point3d[] pts = ((Triangle3d)tri).getPointss();
////                allTriangles.add(new Coordinate[]{
////                    new Coordinate(pts[0].x(), pts[0].y(), pts[0].z()),
////                    new Coordinate(pts[1].x(), pts[1].y(), pts[1].z()),
////                    new Coordinate(pts[2].x(), pts[2].y(), pts[2].z())});
////            }
////        }
//
//
//        final Delaunay delaunay = new Delaunay();
//
//        final Coordinate[] coords = objGeom.getCoordinates();
//        for (int i = 0; i < coords.length-1; i++) {
//            delaunay.insertPoint(coords[i]);
//        }
//        final List<Coordinate[]> allTriangles = delaunay.computeTriangles();
//
//
//        final List<Coordinate[]> triangles = new ArrayList<Coordinate[]>();
//
//        System.out.println(objGeom.getEnvelopeInternal());
//
//        //elimine les triangles en dehors
//        for(final Coordinate[] triangle : allTriangles){
////            Polygon p = GF.createPolygon(GF.createLinearRing(new Coordinate[]{triangle[0],triangle[1],triangle[2],triangle[0]}), new LinearRing[0]);
//
//            //that should be correct but some uncorrect triangles remains
////            if((objGeom.touches(p))) continue;
////            if(!p.within(objGeom)) continue;
////            if(!objGeom.contains(p)) continue;
////            if(!p.overlaps(objGeom)) continue;
//
//////            make and approximative test, heavy cost
////            Geometry inter = p.intersection(objGeom);
////            if(inter == null  || inter.getArea() < 0.9d*p.getArea()) continue;
//
//            triangles.add(triangle);
//        }
//        System.out.println(allTriangles.size());
//        System.out.println(triangles.size());
//
//        final int[] indices = new int[3*triangles.size()];
//        _meshData.setVertexBuffer(BufferUtils.createVector3Buffer(_meshData.getVertexBuffer(), 3*triangles.size()));
//        _meshData.setNormalBuffer(BufferUtils.createVector3Buffer(3*triangles.size()));
//
//        int index = 0;
//        for(final Coordinate[] triangle : triangles){
//            indices[index] = index;
//            BufferUtils.setInBuffer(new Vector3(triangle[0].x, triangle[0].y, z), _meshData.getVertexBuffer(), index);
//            index++;
//            indices[index] = index;
//            BufferUtils.setInBuffer(new Vector3(triangle[1].x, triangle[1].y, z), _meshData.getVertexBuffer(), index);
//            index++;
//            indices[index] = index;
//            BufferUtils.setInBuffer(new Vector3(triangle[2].x, triangle[2].y, z), _meshData.getVertexBuffer(), index);
//            index++;
//
//            _meshData.getNormalBuffer().put(0).put(0).put(0);
//        }
//
//        _meshData.setIndexBuffer(BufferUtils.createIntBuffer(indices));
//    }

    private void prepareLine(Geometry objGeom){
        
    }

    private void prepareOther(Geometry objGeom){
        env = objGeom.getEnvelopeInternal();

        _meshData.setVertexBuffer(BufferUtils.createVector3Buffer(_meshData.getVertexBuffer(), 4));
        final Vector3 vert[] = new Vector3[4];
        vert[0] = new Vector3(env.getMinX(), env.getMinY(),z);
        vert[1] = new Vector3(env.getMinX(), env.getMaxY(),z);
        vert[2] = new Vector3(env.getMaxX(), env.getMaxY(),z);
        vert[3] = new Vector3(env.getMaxX(), env.getMinY(),z);


        // Back
        BufferUtils.setInBuffer(vert[0], _meshData.getVertexBuffer(), 0);
        BufferUtils.setInBuffer(vert[1], _meshData.getVertexBuffer(), 1);
        BufferUtils.setInBuffer(vert[2], _meshData.getVertexBuffer(), 2);
        BufferUtils.setInBuffer(vert[3], _meshData.getVertexBuffer(), 3);

        if (_meshData.getNormalBuffer() == null) {
            _meshData.setNormalBuffer(BufferUtils.createVector3Buffer(4));

            // back
            for (int i = 0; i < 4; i++) {
                _meshData.getNormalBuffer().put(0).put(0).put(-1);
            }

        }
        if (_meshData.getTextureCoords(0) == null) {
            _meshData.setTextureCoords(new FloatBufferData(BufferUtils.createVector2Buffer(8), 3),0);
//            _meshData.setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(8)), 0);
            final FloatBuffer tex = _meshData.getTextureCoords(0).getBuffer();

            tex.put(1).put(0);
            tex.put(0).put(0);
            tex.put(0).put(1);
            tex.put(1).put(1);
        }
        if (_meshData.getIndexBuffer() == null) {
            final int[] indices = { 2,1,0, 3,2,0 };
            _meshData.setIndexBuffer(BufferUtils.createIntBuffer(indices));
        }

    }
    
}
