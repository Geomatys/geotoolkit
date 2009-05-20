

package org.geotoolkit.gui.swing.go3.control;

import javax.swing.JPanel;
import org.geotoolkit.display3d.canvas.A3DCanvas;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JCoordinateBar extends JPanel{

    private A3DCanvas map;

    public JCoordinateBar() {
    }

    public void setMap(A3DCanvas map) {
        this.map = map;

    }

    public A3DCanvas getMap() {
        return map;
    }



}
