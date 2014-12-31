/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Utility class to build icones from TTF font.
 * 
 * @author Johann Sorel
 */
public final class IconBuilder {
    
    public static final URL FONTAWESOME = IconBuilder.class.getResource("/org/geotoolkit/font/fontawesome-webfont.ttf");
    public static Font FONT;

    static{
      try {
        InputStream is = IconBuilder.class.getResourceAsStream("/org/geotoolkit/font/fontawesome-webfont.ttf");
        FONT = Font.createFont(Font.TRUETYPE_FONT, is);
      } catch (Exception ex) {
        ex.printStackTrace();
        System.err.println("Font not loaded.  Using serif font.");
        FONT = new Font("serif", Font.PLAIN, 24);
      }
    }
    
    private IconBuilder(){}
    
    public static ImageIcon createIcon(String text, float size, Color iconColor){
        return createIcon(text,size,iconColor,null);
    }
    
    public static BufferedImage createImage(String text, float size, Color iconColor){
        return createImage(text, null, iconColor, FONT.deriveFont(size), null);
    }
    
    public static ImageIcon createIcon(String text, float size, Color iconColor, Color bgColor){
        final BufferedImage image = createImage(text, null, iconColor, FONT.deriveFont(size), bgColor);
        return new ImageIcon(image);
    }
    
    public static BufferedImage createImage(String text, float size, Color iconColor, Color bgColor){
        return createImage(text, null, iconColor, FONT.deriveFont(size), bgColor);
    }
    
    public static BufferedImage createImage(String text, ImageIcon icon, Color textColor, Font font, Color bgColor){

        final int border = 0;
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        final FontMetrics fm = g.getFontMetrics(font);
        final int textSize = fm.stringWidth(text);
        int width = textSize+border*2;
        int height = fm.getHeight()+border*2;
        if(icon != null){
            width += icon.getIconWidth() + 2;
            height = Math.max(height, icon.getIconHeight());
        }
        //we want a square
        width = Math.max(width, height);
        height = Math.max(width, height);
        
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, width-1, img.getHeight()-1, border, border);

        if(bgColor!=null){
            final Color brighter = new Color(
                    Math.min(255, bgColor.getRed()+100), 
                    Math.min(255, bgColor.getGreen()+100), 
                    Math.min(255, bgColor.getBlue()+100));

            final LinearGradientPaint gradiant = new LinearGradientPaint(0, 0, 0, height, new float[]{0,1},new Color[]{brighter,bgColor});

            g.setPaint(gradiant);
            g.fill(rect);
        }

        int x = border;
        //draw icon
        if(icon != null){
            g.drawImage(icon.getImage(), x, (height-icon.getIconHeight())/2, null);
            x += icon.getIconWidth()+2;
        }

        //draw text
        g.setColor(textColor);
        g.setFont(font);
        g.drawString(text, (width-textSize)/2, fm.getMaxAscent()+border);
        
        if(bgColor!=null){
            //draw border
            g.setColor(Color.BLACK);
            g.draw(rect);
        }

        return img;
    }
    
    
}
