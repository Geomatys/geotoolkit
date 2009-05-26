/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.go.control.creation;


import java.awt.Component;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.gui.swing.go.CanvasHandler;
import org.geotoolkit.gui.swing.go.GoMap2D;

/**
 *
 * @author eclesia
 */
public class DefaultEditionHandler2 implements CanvasHandler {
    
    private final DefaultEditionDecoration deco = new DefaultEditionDecoration();
    private GoMap2D map;

    public DefaultEditionHandler2(GoMap2D map) {
        this.map = map;
    }

    public void setMap(GoMap2D map){
        this.map = map;
    }

    public GoMap2D getMap() {
        return map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(Component component) {
        deco.reset();
        deco.getMouseListener().setMap(map);
        deco.getMouseListener().install(component);
        map.addDecoration(0,deco);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(Component component) {
        deco.getMouseListener().uninstall(component);
        map.removeDecoration(deco);
    }

    @Override
    public ReferencedCanvas2D getCanvas() {
        return map.getCanvas();
    }

}
