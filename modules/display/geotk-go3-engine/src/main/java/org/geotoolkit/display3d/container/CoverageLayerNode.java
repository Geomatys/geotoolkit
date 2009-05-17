
package org.geotoolkit.display3d.container;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.image.Image;
import com.ardor3d.image.Image.Format;
import com.ardor3d.image.Texture;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.util.TextureManager;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class CoverageLayerNode extends A3DGraphic{

    private final CoverageMapLayer layer;

    public CoverageLayerNode(A3DCanvas canvas, CoverageMapLayer layer) {
        super(canvas);
        this.layer = layer;

        org.opengis.geometry.Envelope env = layer.getCoverageReader().getCoverageBounds();
        try {
            env = CRS.transform(env, canvas.getObjectiveCRS());
        } catch (TransformException ex) {
            Logger.getLogger(CoverageLayerNode.class.getName()).log(Level.SEVERE, null, ex);
        }


        final Quad back = new Quad();
        back.initialize(env.getSpan(0), env.getSpan(1));
        back.setTranslation(env.getMinimum(0) + env.getSpan(0)/2, 0, env.getMinimum(1) + env.getSpan(1)/2);
        back.setRotation(new Matrix3().fromAngleNormalAxis(Math.PI * -0.5, new Vector3(1, 0, 0)));
        back.setModelBound(new BoundingBox());
        back.updateModelBound();
        this.attachChild(back);

        try {
            GridCoverage2D coverage = layer.getCoverageReader().read(null);
            RenderedImage img = coverage.getRenderedImage();            
            Image image = AWTImageLoader.makeArdor3dImage(img, false);

            // Add a texture to the box.
            final TextureState ts = new TextureState();
            ts.setTexture(TextureManager.loadFromImage(image, Texture.MinificationFilter.Trilinear,
                    Format.Guess, true));
            back.setRenderState(ts);

//            // Add a material to the box, to show both vertex color and lighting/shading.
//            final MaterialState ms = new MaterialState();
//            ms.setColorMaterial(ColorMaterial.Diffuse);
//            box.setRenderState(ms);

        } catch (FactoryException ex) {
            Logger.getLogger(CoverageLayerNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(CoverageLayerNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CoverageLayerNode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class UpdateThread extends Thread{

        @Override
        public void run() {
            while(true){
                try {
                    sleep(200);
                } catch (InterruptedException ex) {
                }

                

            }
        }

    }

}
