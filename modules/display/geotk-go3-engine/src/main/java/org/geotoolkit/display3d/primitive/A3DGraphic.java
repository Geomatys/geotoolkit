
package org.geotoolkit.display3d.primitive;

import com.ardor3d.scenegraph.Node;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public abstract class A3DGraphic extends Node implements Graphic{

    protected final A3DCanvas canvas;
    protected boolean visible = true;

    protected A3DGraphic(A3DCanvas canvas){
        this.canvas = canvas;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void dispose() {
    }

}
