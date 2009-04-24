/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.go.control;

import javax.swing.JButton;
import org.geotoolkit.gui.swing.go.GoMap2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JCRSButton extends JButton{

    private final CRSAction ACTION_CRS = new CRSAction();
    private GoMap2D map = null;

    public JCRSButton(){
        super();
        setAction(ACTION_CRS);
        setText("CRS");
    }

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    public void setMap(GoMap2D map2d) {
        map = map2d;
        ACTION_CRS.setMap(map);
    }

}
