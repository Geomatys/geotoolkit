/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.timeline;

import java.awt.Color;
import java.awt.Image;
import java.util.Date;

/**
 *
 * @author johann sorel
 */
public interface TimeLineItem extends Comparable<TimeLineItem>{

    Date getDate();
    
    String getToolTip();
    
    Color getColor();
    
    Image getImage();
    
    Image getSelectedImage();
    
}
