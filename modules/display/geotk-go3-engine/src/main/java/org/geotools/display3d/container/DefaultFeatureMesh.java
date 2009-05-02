
package org.geotools.display3d.container;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.geom.BufferUtils;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DefaultFeatureMesh extends Mesh {

    private final SimpleFeature feature;
    private final double z;
    private Envelope env = null;

    public DefaultFeatureMesh(SimpleFeature feature, GeometryCoordinateSequenceTransformer dataToObjectiveTransformer,double z) {
        this.feature = feature;
        this.z =z;
        
        try {
            Geometry objGeom = dataToObjectiveTransformer.transform((Geometry)feature.getDefaultGeometry());
            env = objGeom.getEnvelopeInternal();
            setData();

        } catch (Exception ex) {
            Logger.getLogger(ContextNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        Mesh.RENDER_VERTEX_ONLY= false;
        setModelBound(new BoundingSphere());
        updateModelBound();
        setLightCombineMode(Spatial.LightCombineMode.Off);
//        setDefaultColor(ColorRGBA.ORANGE);
        setRandomColors();


    }

    public Vector3[] computeVertices() {

        final Vector3 rVal[] = new Vector3[4];
        rVal[0] = new Vector3(env.getMinX(), env.getMinY(),z);
        rVal[1] = new Vector3(env.getMinX(), env.getMaxY(),z);
        rVal[2] = new Vector3(env.getMaxX(), env.getMaxY(),z);
        rVal[3] = new Vector3(env.getMaxX(), env.getMinY(),z);

//        System.out.println(rVal[0]);
//        System.out.println(rVal[1]);
//        System.out.println(rVal[2]);
//        System.out.println(rVal[3]);

        return rVal;
    }

    /**
     * Changes the data of the box so that its center is <code>center</code> and it extends in the x, y, and z
     * directions by the given extent. Note that the actual sides will be 2x the given extent values because the box
     * extends in + & - from the center for each extent.
     *
     * @param center
     *            The center of the box.
     * @param xExtent
     *            x extent of the box, in both directions.
     * @param yExtent
     *            y extent of the box, in both directions.
     * @param zExtent
     *            z extent of the box, in both directions.
     */
    public void setData() {        
        setVertexData();
        setNormalData();
        setTextureData();
        setIndexData();
    }

    /**
     * <code>setVertexData</code> sets the vertex positions that define the box. These eight points are determined from
     * the minimum and maximum point.
     */
    private void setVertexData() {
        _meshData.setVertexBuffer(BufferUtils.createVector3Buffer(_meshData.getVertexBuffer(), 4));
        final Vector3[] vert = computeVertices(); // returns 4

        // Back
        BufferUtils.setInBuffer(vert[0], _meshData.getVertexBuffer(), 0);
        BufferUtils.setInBuffer(vert[1], _meshData.getVertexBuffer(), 1);
        BufferUtils.setInBuffer(vert[2], _meshData.getVertexBuffer(), 2);
        BufferUtils.setInBuffer(vert[3], _meshData.getVertexBuffer(), 3);

    }

    /**
     * <code>setNormalData</code> sets the normals of each of the box's planes.
     */
    private void setNormalData() {
        if (_meshData.getNormalBuffer() == null) {
            _meshData.setNormalBuffer(BufferUtils.createVector3Buffer(4));

            // back
            for (int i = 0; i < 4; i++) {
                _meshData.getNormalBuffer().put(0).put(0).put(-1);
            }

        }
    }

    /**
     * <code>setTextureData</code> sets the points that define the texture of the box. It's a one-to-one ratio, where
     * each plane of the box has it's own copy of the texture. That is, the texture is repeated one time for each six
     * faces.
     */
    private void setTextureData() {
        if (_meshData.getTextureCoords(0) == null) {
            _meshData.setTextureCoords(new FloatBufferData(BufferUtils.createVector2Buffer(8), 3),0);
//            _meshData.setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(8)), 0);
            final FloatBuffer tex = _meshData.getTextureCoords(0).getBuffer();

            tex.put(1).put(0);
            tex.put(0).put(0);
            tex.put(0).put(1);
            tex.put(1).put(1);
        }
    }

    /**
     * <code>setIndexData</code> sets the indices into the list of vertices, defining all triangles that constitute the
     * box.
     */
    private void setIndexData() {
        if (_meshData.getIndexBuffer() == null) {
            final int[] indices = { 2,1,0, 3,2,0 };
            _meshData.setIndexBuffer(BufferUtils.createIntBuffer(indices));
        }
    }



}
