

package org.geotoolkit.display3d.container;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import java.util.ArrayList;
import java.util.List;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DefaultPolygonFeatureMesh2 extends Mesh {

    private static final double EPS = 1E-3;

    private final SimpleFeature feature;

    public DefaultPolygonFeatureMesh2(SimpleFeature feature, Geometry geom) {
        this.feature = feature;

//        final double minz = 0;
//        final double maxz = ((Double)feature.getAttribute("Z_MAX"))/5 - ((Double)feature.getAttribute("Z_MIN"))/5;

        final double minz = (Double)feature.getAttribute("Z_MIN")/5;
        final double maxz = (Double)feature.getAttribute("Z_MAX")/5;

        ConformingDelaunayTriangulationBuilder builder = new ConformingDelaunayTriangulationBuilder();
        builder.setSites(geom);
        builder.setConstraints(geom);
        builder.setTolerance(EPS);
        GeometryCollection pieces = (GeometryCollection) builder.getTriangles(new GeometryFactory(geom.getPrecisionModel()));

        int num = pieces.getNumGeometries();
        
        final List<Coordinate[]> triangles = new ArrayList<Coordinate[]>();
        for(int i=0;i<num;i++){
            Polygon poly = (Polygon) pieces.getGeometryN(i);
//            triangles.add(poly.getCoordinates());
            
            if(geom.contains(poly)){
                triangles.add(poly.getCoordinates());
            }
            //sometimes polygon arn't contain because of calculation number rounding
            if(geom.overlaps(poly)){
                double a = poly.getArea();
                double b = poly.intersection(geom).getArea();
//                System.out.println(a + " <> " + b);
                if(Math.abs(a - b) < EPS * Math.max(Math.abs(a), Math.abs(b))){
//                    System.out.println("one case");
                    triangles.add(poly.getCoordinates());
                }
            }
        }

        num = triangles.size();

        final Geometry hull = geom;
        final Coordinate[] faces = hull.getCoordinates();

        num += 3*(faces.length-1)*2;


        final int[] indices = new int[3*num];
        _meshData.setVertexBuffer(BufferUtils.createVector3Buffer(3*num));
        _meshData.setNormalBuffer(BufferUtils.createVector3Buffer(3*num));

        int index = 0;
        //make the top face
        for(Coordinate[] triangle : triangles){
            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(triangle[0].x, maxz, triangle[0].y), _meshData.getVertexBuffer(), index);
            index++;
            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(triangle[1].x, maxz, triangle[1].y), _meshData.getVertexBuffer(), index);
            index++;
            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(triangle[2].x, maxz, triangle[2].y), _meshData.getVertexBuffer(), index);
            index++;
            _meshData.getNormalBuffer().put(0).put(1).put(0);
            _meshData.getNormalBuffer().put(0).put(1).put(0);
            _meshData.getNormalBuffer().put(0).put(1).put(0);
        }

        //make the facades
        for(int i=0;i<faces.length-1;i++){
            Coordinate start = faces[i];
            Coordinate end = faces[i+1];

            double a = Math.PI/2;
            double x = end.x - start.x;
            double y = end.y - start.y;
            float nx = (float) (x * Math.cos(a) - y * Math.sin(a));
            float ny = (float) (x * Math.sin(a) + y * Math.cos(a));


            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(start.x, minz, start.y), _meshData.getVertexBuffer(), index);
            index++;
            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(start.x, maxz, start.y), _meshData.getVertexBuffer(), index);
            index++;
            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(end.x, minz, end.y), _meshData.getVertexBuffer(), index);
            index++;
            _meshData.getNormalBuffer().put(nx).put(0).put(ny);
            _meshData.getNormalBuffer().put(nx).put(0).put(ny);
            _meshData.getNormalBuffer().put(nx).put(0).put(ny);

            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(end.x, minz, end.y), _meshData.getVertexBuffer(), index);
            index++;
            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(start.x, maxz, start.y), _meshData.getVertexBuffer(), index);
            index++;
            indices[index] = index;
            BufferUtils.setInBuffer(new Vector3(end.x, maxz, end.y), _meshData.getVertexBuffer(), index);
            index++;            
            _meshData.getNormalBuffer().put(nx).put(0).put(ny);
            _meshData.getNormalBuffer().put(nx).put(0).put(ny);
            _meshData.getNormalBuffer().put(nx).put(0).put(ny);

        }

        _meshData.setIndexBuffer(BufferUtils.createIntBuffer(indices));

        final MaterialState ms = new MaterialState();
        ms.setEnabled(true);
        ms.setDiffuse(new ColorRGBA(0.6f, 0.6f, 0.7f, .5f));
//        ms.setSpecular(new ColorRGBA(0.8f, 0.7f, 0.7f, .6f));
//        ms.setShininess(10);
        this.setRenderState(ms);


        setModelBound(new BoundingSphere());
        updateModelBound();
    }


}
