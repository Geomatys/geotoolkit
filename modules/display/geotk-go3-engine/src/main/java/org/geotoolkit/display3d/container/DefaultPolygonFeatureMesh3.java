

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
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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

        final Geometry hull = geom;
        final Coordinate[] faces = hull.getCoordinates();
        final int nbTriangleVertex = 3*triangles.size();
        final int nbQuadVertex = 2*faces.length;

        final FloatBuffer vertexBuffer  = BufferUtils.createVector3Buffer(nbTriangleVertex+nbQuadVertex);
        final FloatBuffer normalBuffer  = BufferUtils.createVector3Buffer(nbTriangleVertex+nbQuadVertex);
        final IntBuffer indexBuffer     = BufferUtils.createIntBuffer(nbTriangleVertex+nbQuadVertex);

        int index = 0;
        //make the top face
        for(Coordinate[] triangle : triangles){
            vertexBuffer.put((float)triangle[0].x).put(maxz).put((float)triangle[0].y);
            vertexBuffer.put((float)triangle[1].x).put(maxz).put((float)triangle[1].y);
            vertexBuffer.put((float)triangle[2].x).put(maxz).put((float)triangle[2].y);
            normalBuffer.put(0).put(1).put(0);
            normalBuffer.put(0).put(1).put(0);
            normalBuffer.put(0).put(1).put(0);
            indexBuffer.put(index++).put(index++).put(index++);
        }

        //make the facades
        Coordinate previous = faces[faces.length-1];
        for(Coordinate coord : faces){

            double a = Math.PI/2;
            double x = previous.x - coord.x;
            double y = previous.y - coord.y;
            float nx = (float) (x * Math.cos(a) - y * Math.sin(a));
            float ny = (float) (x * Math.sin(a) + y * Math.cos(a));
            previous = coord;

            vertexBuffer.put((float)coord.x).put(maxz).put((float)coord.y);
            vertexBuffer.put((float)coord.x).put(minz).put((float)coord.y);
            normalBuffer.put(nx).put(0).put(ny);
            normalBuffer.put(nx).put(0).put(ny);
            indexBuffer.put(index++).put(index++);
        }

        _meshData.setVertexBuffer(vertexBuffer);
        _meshData.setNormalBuffer(normalBuffer);
        _meshData.setIndexBuffer(indexBuffer);
        _meshData.setIndexLengths(  new int[] {nbTriangleVertex, nbQuadVertex} );
        _meshData.setIndexModes(    new IndexMode[] { IndexMode.Triangles, IndexMode.QuadStrip } );


        final MaterialState ms = new MaterialState();
        ms.setEnabled(true);
        ms.setDiffuse(new ColorRGBA(0.6f, 0.6f, 0.7f, .5f));
        this.setRenderState(ms);

        setModelBound(new BoundingSphere());
        updateModelBound();
    }

}
