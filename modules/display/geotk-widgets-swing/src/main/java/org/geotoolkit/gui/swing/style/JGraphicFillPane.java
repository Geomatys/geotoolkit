/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.style;

import org.opengis.style.GraphicFill;

/**
 *
 * @author eclesia
 */
public class JGraphicFillPane extends JGraphicPane{
    
    @Override
    public GraphicFill create() {
        return getStyleFactory().graphicFill(super.create());
    }

    
    
}
