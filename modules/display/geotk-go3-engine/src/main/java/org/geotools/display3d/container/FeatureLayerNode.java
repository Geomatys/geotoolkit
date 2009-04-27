
package org.geotools.display3d.container;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.TexCoords;
import com.ardor3d.util.geom.BufferUtils;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FeatureSource;
import org.geotools.display3d.canvas.A3DCanvas;
import org.geotools.display3d.primitive.A3DGraphic;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.map.FeatureMapLayer;
import org.geotools.map.MapContext;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

            try{
                while(ite.hasNext()){
                    DefaultPolygonFeatureMesh mesh = new DefaultPolygonFeatureMesh(ite.next(),dataToObjectiveTransformer,0f);
                    this.attachChild(mesh);
                }
            }finally{
                ite.close();
            }

        } catch (Exception ex) {
            Logger.getLogger(FeatureLayerNode.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


}
