/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 Geomatys
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
package org.geotoolkit.gui.swing.style;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.sld.MutableLayer;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.logging.Logging;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.sld.LayerStyle;
import org.opengis.sld.NamedLayer;
import org.opengis.sld.UserLayer;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;

/**
 * StyleBank
 *
 * @author Fabien Rétif (Geomatys)
 */
public final class StyleBank {

    public static Comparator ALL = new Comparator() {
        public int compare(Object t, Object t1) {
            return 0;
        }
    };

    public static class ByClassComparator implements Comparator {

        private final Class[] clazzes;

        public ByClassComparator(Class... clazzes) {
            this.clazzes = clazzes;
        }

        public int compare(Object t, Object t1) {
            for (Class c : clazzes) {
                if (c.isInstance(t)) {
                    return 0;
                }
            }
            return -1;
        }
    }
    private static StyleBank INSTANCE = null;
    private final File folder;
    /**
     * List of objects in the bank.
     */
    private final Set banks = new HashSet();
    
    private StyleBank(File storageFolder) {
        this.folder = storageFolder;

        loadDefaultStyle();
        explore(folder);

    }

    
    /**
     * Returns a list which contains instances of the class given in parameter
     *
     * @param clazz : Class
     * @return ArrayList
     */
    public List getCandidates(Comparator c) {

        if (c == null) {
            c = ALL;
        }

        final List list = new ArrayList();

        for (Object o : banks) {
            if (c.compare(o, o) == 0) {
                list.add(o);
            }
        }

        return list;
    }
    
    public void addFromFile(File source){       
        loadFile(source);
    }
    
    private void explore(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                explore(c);
            }
        } else if (f.exists()) {
            loadFile(f);
        }
    }

    private void loadDefaultStyle() {
        //Load default marks                    
        final Literal[] marks = new Literal[]{
            StyleConstants.MARK_CIRCLE,
            StyleConstants.MARK_CROSS,
            StyleConstants.MARK_SQUARE,
            StyleConstants.MARK_STAR,
            StyleConstants.MARK_TRIANGLE,
            StyleConstants.MARK_X
        };

        for (Literal m : marks) {

            // Creates mark from litteral
            final String name = "mySymbol";
            final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
            final String geometry = null; //use the default geometry of the feature
            final Unit unit = NonSI.PIXEL;
            final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;

            //the visual element
            final Expression size = GO2Utilities.FILTER_FACTORY.literal(12);
            final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
            final Expression rotation = StyleConstants.LITERAL_ONE_FLOAT;
            final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
            final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;

            final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
            final Stroke stroke = GO2Utilities.STYLE_FACTORY.stroke(Color.BLACK, 2);
            final Fill fill = GO2Utilities.STYLE_FACTORY.fill(Color.BLACK);

            final Mark currentMark = GO2Utilities.STYLE_FACTORY.mark(
                    m,
                    fill,
                    stroke);

            parse(currentMark);
        }
        
        //load default symbolizers
        parse(StyleConstants.DEFAULT_POINT_SYMBOLIZER);
        parse(StyleConstants.DEFAULT_LINE_SYMBOLIZER);
        parse(StyleConstants.DEFAULT_POLYGON_SYMBOLIZER);
        parse(StyleConstants.DEFAULT_TEXT_SYMBOLIZER);
        parse(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
        
    }

    /**
     * Load bank
     *
     * @param sources
     */
    private void loadFile(File file) {

        MutableStyle currentStyle = null;

        final Specification.StyledLayerDescriptor version = Specification.StyledLayerDescriptor.V_1_1_0;

        if (file.exists()) {

            final StyleXmlIO tool = new StyleXmlIO();
            try {
                final MutableStyledLayerDescriptor sld = tool.readSLD(file, version);

                if (sld != null) {
                    for (MutableLayer sldLayer : sld.layers()) {
                        if (sldLayer instanceof NamedLayer) {
                            final NamedLayer nl = (NamedLayer) sldLayer;
                            for (LayerStyle ls : nl.styles()) {
                                if (ls instanceof MutableStyle) {
                                    currentStyle = (MutableStyle) ls;
                                }
                            }
                        } else if (sldLayer instanceof UserLayer) {
                            final UserLayer ul = (UserLayer) sldLayer;
                            for (Style ls : ul.styles()) {
                                if (ls instanceof MutableStyle) {
                                    currentStyle = (MutableStyle) ls;
                                }
                            }
                        }
                    }
                } else {
                    currentStyle = tool.readStyle(file,
                            (version == Specification.StyledLayerDescriptor.V_1_0_0)
                            ? Specification.SymbologyEncoding.SLD_1_0_0
                            : Specification.SymbologyEncoding.V_1_1_0);

                }

                if (currentStyle != null) {

                    Iterator<MutableFeatureTypeStyle> iterStyles = currentStyle.featureTypeStyles().iterator();

                    while (iterStyles.hasNext()) {

                        Iterator<MutableRule> iterRules = iterStyles.next().rules().iterator();

                        while (iterRules.hasNext()) {

                            Iterator<Symbolizer> iterSymbol = iterRules.next().symbolizers().iterator();

                            while (iterSymbol.hasNext()) {

                                Symbolizer symbol = iterSymbol.next();

                                parse(symbol);
                            }
                        }
                    }
                }

            } catch (JAXBException ex) {
                Logging.getLogger(StyleBank.class).log(Level.FINEST, ex.getMessage(), ex);
            } catch (FactoryException ex) {
                Logging.getLogger(StyleBank.class).log(Level.FINEST, ex.getMessage(), ex);
            }
        }

    }

    /**
     * Parse object, add it to the bank and parse his sub-type
     *
     * @param style
     */
    private void parse(final Object style) {

        if (style != null) {

            //We add the style 
            banks.add(style);

            //We parse under-style
            if (style instanceof PointSymbolizer) {

                PointSymbolizer currentPoint = (PointSymbolizer) style;

                if (currentPoint.getGraphic() != null && currentPoint.getGraphic().graphicalSymbols() != null) {

                    //Parses his graphical symbol            
                    Iterator<GraphicalSymbol> iterGraphicSymbol = ((PointSymbolizer) style).getGraphic().graphicalSymbols().iterator();

                    while (iterGraphicSymbol.hasNext()) {
                        parse(iterGraphicSymbol.next());
                    }
                }

            } else if (style instanceof LineSymbolizer) {

                //Parses his stroke
                parse(((LineSymbolizer) style).getStroke());

            } else if (style instanceof PolygonSymbolizer) {

                //Parse his style
                parse(((PolygonSymbolizer) style).getFill());
                parse(((PolygonSymbolizer) style).getStroke());

            } else if (style instanceof Fill) {

                Fill currentFill = ((Fill) style);

                if (currentFill.getGraphicFill() != null && currentFill.getGraphicFill().graphicalSymbols() != null) {

                    Iterator<GraphicalSymbol> iterGraphicSymbol = ((Fill) style).getGraphicFill().graphicalSymbols().iterator();

                    while (iterGraphicSymbol.hasNext()) {
                        parse(iterGraphicSymbol.next());
                    }
                }
            }
        }

    }

    /**
     * Static for load object from file bank
     *
     * @return JBankManager
     */
    public static synchronized StyleBank getInstance() {

        if (StyleBank.INSTANCE == null) {
            String dir = System.getProperty("user.home") + File.separator + ".geotoolkit" + File.separator + "bankSLD";

            File file = new File(dir);
            StyleBank.INSTANCE = new StyleBank(file);
        }

        return StyleBank.INSTANCE;
    }
}
