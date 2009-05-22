

package org.geotoolkit.display3d.container;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.NormalsMode;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DefaultPolygonFeatureMesh3 extends Mesh {

    private static final double EPS = 1E-3;

    private final SimpleFeature feature;

    public DefaultPolygonFeatureMesh3(SimpleFeature feature, Geometry geom) {
        this.feature = feature;


        final float minz = ((Number)feature.getAttribute("Z_MIN")).floatValue()/5;
        final float maxz = ((Number)feature.getAttribute("Z_MAX")).floatValue()/5;

        final ConformingDelaunayTriangulationBuilder builder = new ConformingDelaunayTriangulationBuilder();
        builder.setSites(geom);
        builder.setConstraints(geom);
        builder.setTolerance(EPS);
        GeometryCollection pieces = (GeometryCollection) builder.getTriangles(new GeometryFactory(geom.getPrecisionModel()));
        
        final List<Coordinate[]> triangles = new ArrayList<Coordinate[]>();
        for(int i=0,n=pieces.getNumGeometries();i<n;i++){
            final Polygon poly = (Polygon) pieces.getGeometryN(i);
            
            if(geom.contains(poly)){
                triangles.add(poly.getCoordinates());
            }

            //sometimes polygon arn't contain because of calculation number rounding
            if(geom.overlaps(poly)){
                double a = poly.getArea();
                double b = poly.intersection(geom).getArea();
                if(Math.abs(a - b) < EPS * Math.max(Math.abs(a), Math.abs(b))){
                    triangles.add(poly.getCoordinates());
                }
            }
        }

        //compress triangulation
        final List<Integer> indexes = new ArrayList<Integer>();
        final List<Coordinate> vertexes = new ArrayList<Coordinate>();
        compress(triangles, vertexes, indexes);
        final int nbTriangleVertex = vertexes.size();

        //find the facades
        final Polygon hull = (Polygon) geom;
        final List<Coordinate[]> rings = new ArrayList<Coordinate[]>();
        final Coordinate[] exteriorRing = hull.getExteriorRing().getCoordinates();
        rings.add(exteriorRing);
        int nbQuadVertex = 4*(exteriorRing.length-1) ;

        for(int i=0,n=hull.getNumInteriorRing();i<n;i++){
            final Coordinate[] hole = hull.getInteriorRingN(i).getCoordinates();
            nbQuadVertex += 4*(hole.length-1);
            rings.add(hole);
        }


        final FloatBuffer vertexBuffer  = BufferUtils.createVector3Buffer(nbTriangleVertex+nbQuadVertex);
        final FloatBuffer normalBuffer  = BufferUtils.createVector3Buffer(nbTriangleVertex+nbQuadVertex);
        final IntBuffer indexBuffer     = BufferUtils.createIntBuffer(indexes.size()+nbQuadVertex);

        //make the facades
        int index = 0;
        for(Coordinate[] faces : rings){
            for(int i=0,n=faces.length-1;i<n;i++){
                Coordinate previous = faces[i];
                Coordinate coord = faces[i+1];

                double a = Math.PI/2;
                double x = previous.x - coord.x;
                double y = previous.y - coord.y;
                float nx = (float) (x * Math.cos(a) - y * Math.sin(a));
                float ny = (float) (x * Math.sin(a) + y * Math.cos(a));

                vertexBuffer.put((float)previous.x).put(maxz).put((float)previous.y);
                vertexBuffer.put((float)previous.x).put(minz).put((float)previous.y);
                vertexBuffer.put((float)coord.x).put(minz).put((float)coord.y);
                vertexBuffer.put((float)coord.x).put(maxz).put((float)coord.y);
                normalBuffer.put(nx).put(0).put(ny);
                normalBuffer.put(nx).put(0).put(ny);
                normalBuffer.put(nx).put(0).put(ny);
                normalBuffer.put(nx).put(0).put(ny);
                indexBuffer.put(index++).put(index++);
                indexBuffer.put(index++).put(index++);
            }
        }

        //make the top face
        for(Coordinate c : vertexes){
            vertexBuffer.put((float)c.x).put(maxz).put((float)c.y);
            normalBuffer.put(0).put(1).put(0);
        }
        for(Integer i : indexes){
            indexBuffer.put(index+i);
        }


        _meshData.setVertexBuffer(vertexBuffer);
        _meshData.setNormalBuffer(normalBuffer);
        _meshData.setIndexBuffer(indexBuffer);
        _meshData.setIndexLengths(  new int[] {nbQuadVertex, indexes.size() } );
        _meshData.setIndexModes(    new IndexMode[] {IndexMode.Quads, IndexMode.Triangles } );

        final MaterialState ms = new MaterialState();
        ms.setEnabled(true);
        ms.setDiffuse(new ColorRGBA(0.6f, 0.6f, 0.7f, .5f));
        this.setRenderState(ms);

        setModelBound(new BoundingSphere());
        updateModelBound();
    }

    private static void compress(List<Coordinate[]> coords, List<Coordinate> vertexes, List<Integer> indexes){
        Map<Coordinate,Integer> lst = new HashMap<Coordinate,Integer>();
        
        int inc = -1;

        for(Coordinate[] coord : coords){
            for(int i=0;i<3;i++){
                //use only 3 first coords, the 4th one is the same as the first one
                Coordinate c = coord[i];
                Integer index = lst.get(c);
                if(index != null){
                    indexes.add(index);
                }else{
                    inc++;
                    vertexes.add(c);
                    indexes.add(inc);
                    lst.put(c, inc);
                }
            }
        }

    }

}
