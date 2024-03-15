

package org.geotoolkit.pending.demo.rendering;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.Style;


public class GlyphDemo {

    private static final MutableStyleFactory SF = DefaultStyleFactory.provider();

    public static void main(String[] args) {
        Demos.init();

        //generate a style
        final Style style = SF.style(StyleConstants.DEFAULT_LINE_SYMBOLIZER);

        //grab the best glyph size, you may provide your own size, the service will
        //do it's best to generate a nice glyph
        final Dimension preferredSize = DefaultGlyphService.glyphPreferredSize(style, null, null);

        //create the glyph image
        final BufferedImage glyph = DefaultGlyphService.create(style, preferredSize, null);

        //show the glyph
        final JFrame frm = new JFrame();
        frm.setContentPane(new JLabel(new ImageIcon(glyph)));
        frm.setSize(100, 100);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);

    }




}
