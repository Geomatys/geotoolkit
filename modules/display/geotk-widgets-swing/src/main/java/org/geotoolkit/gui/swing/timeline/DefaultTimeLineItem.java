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
public class DefaultTimeLineItem implements TimeLineItem{

    private final Date date;
    private final String toolTip;
    private final Color color;
    private final Image normalImage;
    private final Image selectedImage;
    
    public DefaultTimeLineItem(Date d,String tooltip,Color color,Image img,Image selected){
        date = d;
        toolTip = tooltip;
        this.color = color;
        normalImage = img;
        selectedImage = selected;
    }
    
    public Date getDate() {
        return date;
    }

    public String getToolTip() {
        return toolTip;
    }

    public Color getColor() {
        return color;
    }

    public Image getImage() {
        return normalImage;
    }

    public int compareTo(TimeLineItem o) {
        
        if(o.getImage() != null){
            if(normalImage == null){
                return -1;
            }else{
                return date.compareTo(o.getDate());
            }
            
        }else{
            if(normalImage != null){
                return 1;
            }else{
                return date.compareTo(o.getDate());
            }
            
        }
        
    }

    public Image getSelectedImage() {
        return selectedImage;
    }

}
