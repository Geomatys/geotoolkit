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
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

/**
 * Utility class to build icones from TTF font.
 *
 * @author Johann Sorel
 */
public final class IconBuilder {

    /** The logger for the rendering module. */
    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.font");

    private static final Supplier<IllegalStateException> INIT_ERROR = () -> new IllegalStateException("Font awesome has not been initialized properly.");

    public static final URL FONTAWESOME = IconBuilder.class.getResource("/org/geotoolkit/font/fa-solid-900.ttf");
    public static final Optional<Font> FONT;

    static {
      Optional<Font> tmpFont;
      try (final InputStream is = FONTAWESOME.openStream()) {
        tmpFont = Optional.of(Font.createFont(Font.TRUETYPE_FONT, is));
      } catch (Exception ex) {
          LOGGER.log(Level.WARNING, ex, () -> "Cannot load FontAwesome from "+FONTAWESOME);
          tmpFont = Optional.empty();
      }
      FONT = tmpFont;
    }

    private IconBuilder(){}

    public static ImageIcon createIcon(String text, float size, Color iconColor){
        return createIcon(text,size,iconColor,null);
    }

    public static BufferedImage createImage(String text, float size, Color iconColor){
        return createImage(text, size, iconColor, null);
    }

    public static ImageIcon createIcon(String text, float size, Color iconColor, Color bgColor){
        final BufferedImage image = FONT
                .map(f -> createImage(text, null, iconColor, f.deriveFont(size), null))
                .orElseThrow(INIT_ERROR);
        return new ImageIcon(image);
    }

    public static BufferedImage createImage(String text, float size, Color iconColor, Color bgColor){
        return FONT
                .map(f -> createImage(text, null, iconColor, f.deriveFont(size), bgColor))
                .orElseThrow(INIT_ERROR);
    }

    public static BufferedImage createImage(String text, ImageIcon icon, Color textColor, Font font, Color bgColor) {
        return createImage(text, icon, textColor, font, bgColor, null, 2, true, false);
    }

    public static BufferedImage createImage(String text, ImageIcon icon, Color textColor, Font font, Color bgColor, Insets insets, int graphicGap, final boolean squareWanted, final boolean removeLeading) {
        ArgumentChecks.ensureNonEmpty("Text to draw", text);
        ArgumentChecks.ensureNonNull("Font to use", text);
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }

        final int border = 0;
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        final FontMetrics fm = g.getFontMetrics(font);
        final int textSize = fm.stringWidth(text);

        int width = textSize+border*2+insets.left+insets.right;
        int height = fm.getHeight()+border*2+insets.top+insets.bottom;
        if (removeLeading) {
            height -= fm.getLeading();
        }
        if(icon != null){
            width += icon.getIconWidth() + graphicGap;
            height = Math.max(height, icon.getIconHeight());
        }

        // We want a square. We compute additional margin to draw icon and text in center of thee square.
        final int additionalLeftInset;
        final int additionalTopInset;
        if (squareWanted) {
            final int tmpWidth = width;
            width = Math.max(width, height);
            additionalLeftInset = (width - tmpWidth) / 2;

            final int tmpHeight = height;
            height = Math.max(width, height);
            additionalTopInset = (height - tmpHeight) / 2;
        } else {
            additionalLeftInset = 0;
            additionalTopInset = 0;
        }

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

        int x = border + insets.left + additionalLeftInset;
        //draw icon
        if(icon != null){
            g.drawImage(icon.getImage(), x, (height-icon.getIconHeight())/2 + additionalTopInset, null);
            x += icon.getIconWidth()+graphicGap;
        }

        //draw text
        if (textColor != null) {
            g.setColor(textColor);
        }

        g.setFont(font);

        g.drawString(text, x, fm.getAscent()+border+insets.top + additionalTopInset);

        if(bgColor!=null){
            //draw border
            g.setColor(Color.BLACK);
            g.draw(rect);
        }

        return img;
    }
}
