

package org.geotoolkit.pending.demo.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.xml.bind.JAXBException;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.DefaultBackgroundTemplate;
import org.geotoolkit.display2d.ext.legend.DefaultLegendService;
import org.geotoolkit.display2d.ext.legend.DefaultLegendTemplate;
import org.geotoolkit.display2d.ext.legend.LegendTemplate;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.sld.xml.Specification.SymbologyEncoding;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.util.FactoryException;


public class LegendDemo {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws PortrayalException, JAXBException, FactoryException {

        //generate a map context
        final MapContext context = createContext();

        //generate a template for the legend
        final LegendTemplate template = new DefaultLegendTemplate(
                new DefaultBackgroundTemplate( //legend background
                    new BasicStroke(2), //stroke
                    Color.BLUE, //stroke paint
                    Color.WHITE, // fill paint
                    new Insets(10, 10, 10, 10), //border margins
                    8 //round border
                    ),
                2, //gap between legend elements
                null, //glyph size, we can let it to null for the legend to use the best size
                new Font("Serial",Font.PLAIN,10), //Font used for style rules
                true, // show layer names
                new Font("Serial",Font.BOLD,12) //Font used for layer names
                );


        //grab the best legend size, you may provide your own size, the service will
        //do it's best to generate a nice legend
        final Dimension preferredSize = DefaultLegendService.legendPreferredSize(template, context);

        //create the legend image
        final BufferedImage legend = DefaultLegendService.portray(template, context, preferredSize);

        //show the legend
        final JFrame frm = new JFrame();
        frm.setContentPane(new JScrollPane(new JLabel(new ImageIcon(legend))));
        frm.setSize(300, 400);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }

    private static MapContext createContext() throws JAXBException, FactoryException{
        final MapContext context = MapBuilder.createContext();

        final XMLUtilities xmlutil = new XMLUtilities();

        final MutableStyle style2 = xmlutil.readStyle(LegendDemo.class.getResource("/data/style/legend2.xml"), SymbologyEncoding.V_1_1_0);
        final MapLayer layer2 = MapBuilder.createEmptyMapLayer();
        layer2.setStyle(style2);
        layer2.setDescription(SF.description("Highway", "Highway"));
        context.layers().add(layer2);

        return context;
    }

}
