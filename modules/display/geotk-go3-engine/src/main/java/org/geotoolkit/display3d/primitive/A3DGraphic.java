
package org.geotoolkit.display3d.primitive;

import com.ardor3d.scenegraph.Node;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public abstract class A3DGraphic extends Node implements Graphic{

    protected boolean visible = true;

    protected A3DGraphic(){

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
