/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.renderer.style;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;

/**
 * This factory accepts mark paths in the <code>ttf://fontName#code</code>
 * format, where fontName is the name of a TrueType font installed in the
 * system, or a URL to a TTF file, and the code is the character code, which may
 * be expressed in decimal, hexadecimal (e.g. <code>0x10</code>) octal (e.g.
 * <code>045</code>) form, as well as Unicode codes (e.g. <code>U+F054</code>
 * or <code>\uF054</code>).
 * <p>
 * When using the Windows CharMap to pick up the font codes, beware that the
 * reported code for symbol fonts are most of the time incomplete, for example,
 * the filled drop symbol in Wingdings is reported as having code
 * <code>0x53</code> whilst the real code is <code>0xF053</code> (as a rule
 * of thumb, try prefixing the reported code with <code>F0</code>).
 * 
 * @author Andrea Aime - TOPP
 * 
 * @module pending
 */
public class TTFMarkFactory implements MarkFactory {

    private static FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(
            new AffineTransform(), false, false);

    @Override
    public Shape getShape(Graphics2D graphics, Expression symbolUrl, Feature feature)
            throws Exception {
        String markUrl = symbolUrl.evaluate(feature, String.class);

        // if it does not start with the right prefix, it's not our business
        if (!markUrl.startsWith("ttf://"))
            return null;

        // if it does not match the expected format, complain before exiting
        if (!markUrl.matches("ttf://.+#.+")) {
            throw new IllegalArgumentException(
                    "Mark URL font found, but does not match the required "
                            + "structure font://<fontName>#<charNumber>, e.g., ttf://wingdigs#0x7B. You specified "
                            + markUrl);
        }
        String[] fontElements = markUrl.substring(6).split("#");

        // look up the font
        Font font = FontCache.getDefaultInsance().getFont(fontElements[0]);
        if (font == null) {
            throw new IllegalArgumentException("Unkown font " + fontElements[0]);
        }

        // get the symbol number
        String code = fontElements[1];
        char character;
        try {
            // see if a unicode escape sequence has been used
            if (code.startsWith("U+") || code.startsWith("\\u")) 
                code = "0x" + code.substring(2);
            // this will handle most numeric formats like decimal, hex and octal
            character = (char) Integer.decode(code).intValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid character specification " + fontElements[1], e);
        }

        // build the shape out of the font
        Font unitSizeFont = font.deriveFont(1.0f);
        GlyphVector textGlyphVector = unitSizeFont.createGlyphVector(FONT_RENDER_CONTEXT,
                new char[] { (char) character });
        return textGlyphVector.getOutline();
    }

    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.BLACK);

        char c = 0xF041;
        System.out.println((int) c);

        Font font = new Font("Wingdings", Font.PLAIN, 60);
        for (int i = 0; i < 65536; i++)
            if (font.canDisplay(i))
                System.out.println(((int) i) + ": " + Long.toHexString(i));
        GlyphVector textGlyphVector = font.createGlyphVector(FONT_RENDER_CONTEXT, new char[] { c });
        Shape shape = textGlyphVector.getOutline();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.translate(150, 150);
        g2d.setColor(Color.BLUE);
        g2d.fill(shape);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString(new String(new char[] { c }), 0, 50);

        g2d.dispose();
        JFrame frame = new JFrame("Test");
        frame.setContentPane(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
