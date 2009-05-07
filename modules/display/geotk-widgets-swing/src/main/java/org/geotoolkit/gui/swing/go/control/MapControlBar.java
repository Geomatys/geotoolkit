/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.go.control;

import java.awt.Component;
import org.geotoolkit.gui.swing.go.GoMap2D;

/**
 *
 * @author eclesia
 */
public interface MapControlBar {

    void setMap(GoMap2D map);

    GoMap2D getMap();

    Component getComponent();

}
