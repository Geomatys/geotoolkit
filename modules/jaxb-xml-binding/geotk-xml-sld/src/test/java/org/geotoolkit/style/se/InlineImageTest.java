
package org.geotoolkit.style.se;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.style.StyleConstants.*;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.Symbolizer;
import org.apache.sis.measure.Units;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InlineImageTest extends org.geotoolkit.test.TestBase {

    public static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);
    public static final MutableStyleFactory SF = (MutableStyleFactory)FactoryFinder.getStyleFactory(null);

    @Test
    public void readImage() throws Exception{

        final BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_ARGB);

        final String geometry = null;
        final ExternalGraphic external = SF.externalGraphic(new ImageIcon(image), Collections.EMPTY_LIST);
        final Graphic graphic = SF.graphic(Collections.singletonList((GraphicalSymbol)external),
                LITERAL_ONE_FLOAT, LITERAL_ONE_FLOAT, LITERAL_ONE_FLOAT, DEFAULT_ANCHOR_POINT, DEFAULT_DISPLACEMENT);
        final PointSymbolizer ips = SF.pointSymbolizer("", geometry, DEFAULT_DESCRIPTION, Units.POINT, graphic);
        final MutableStyle style = SF.style(ips);

        final File f = File.createTempFile("sld", ".xml");
        f.deleteOnExit();

        final StyleXmlIO io = new StyleXmlIO();
        io.writeStyle(f, style, Specification.StyledLayerDescriptor.V_1_1_0);


        final MutableStyle result = io.readStyle(f, Specification.SymbologyEncoding.V_1_1_0);
        final Symbolizer s = result.featureTypeStyles().get(0).rules().get(0).symbolizers().get(0);
        assertTrue(s instanceof PointSymbolizer);
        final PointSymbolizer ps = (PointSymbolizer) s;
        final ExternalGraphic eg = (ExternalGraphic) ps.getGraphic().graphicalSymbols().get(0);
        assertNotNull(eg);
        final Icon ri = eg.getInlineContent();
        assertNotNull(ri);
        assertEquals(20,ri.getIconWidth());
        assertEquals(10,ri.getIconHeight());

    }

}
