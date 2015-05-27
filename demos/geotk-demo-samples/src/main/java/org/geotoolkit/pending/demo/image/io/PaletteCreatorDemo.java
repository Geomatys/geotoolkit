
package org.geotoolkit.pending.demo.image.io;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.geotoolkit.image.palette.Palette;
import org.geotoolkit.image.palette.PaletteFactory;

/**
 * Get several colors from a specific color palette.
 *
 * @author Cédric Briançon
 */
public class PaletteCreatorDemo {

    private static final PaletteFactory PALETTE_FACTORY = PaletteFactory.getDefault();

    /**
     * Palette name. Should be chosen between values here :
     * http://www.geotoolkit.org/apidocs/org/geotoolkit/image/io/doc-files/palettes.html
     */
    private static final String PALETTE_NAME = "rainbow-c";

    /**
     * Number of colors to take from the palette.
     */
    private static final int NB_COLORS = 15;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        final Palette palette = PALETTE_FACTORY.getPalette(PALETTE_NAME, NB_COLORS);
        final IndexColorModel icm = (IndexColorModel) palette.getColorModel();

        for (int i=0; i<NB_COLORS; i++) {
            final Color color = new Color(icm.getRGB(i));
            final String hexColor = Integer.toHexString(color.getRGB()).substring(2);
            System.out.println("RGB for "+ i +" : "+ color + " | hexadecimal : #"+ hexColor);
        }

    }
}
