/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.gui.swing.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.xml.bind.JAXBException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.display2d.style.CachedExternal;
import org.geotoolkit.display2d.style.CachedGraphic;
import org.geotoolkit.display2d.style.CachedMark;
import org.geotoolkit.metadata.iso.citation.DefaultOnLineResource;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.xml.Specification;
import org.geotoolkit.style.xml.XMLUtilities;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.metadata.citation.OnLineResource;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.ExternalMark;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

/**
 *
 * @author sorel
 */
public class J2DStyleTest {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private static final float SIZE = 128f;

    public static void main(String[] args){

//        testMark();
//        testExternal();
//        testGraphicMark();
        testGraphicExternal();

    }

    private static void testMark(){
        Collection<Image> images = new ArrayList<Image>();
        images.add( createMarkImage("cross",SIZE) );
        images.add( createMarkImage("circle",SIZE) );
        images.add( createMarkImage("triangle",SIZE) );
        images.add( createMarkImage("X",SIZE) );
        images.add( createMarkImage("star",SIZE) );
        images.add( createMarkImage("arrow",SIZE) );
        images.add( createMarkImage("hatch",SIZE) );
        images.add( createMarkImage("square",SIZE) );

        showImage(images);
    }

    private static void testExternal(){
        Collection<Image> images = new ArrayList<Image>();
        images.add( createExternalImage(SIZE));

        showImage(images);
    }

    private static void testGraphicMark(){
        GraphicalSymbol symbol = createMark("square");
        Collection<Image> images = new ArrayList<Image>();
        images.add( createGraphicImage(symbol,SIZE,0));
        images.add( createGraphicImage(symbol,SIZE,15));
        images.add( createGraphicImage(symbol,SIZE,30));
        images.add( createGraphicImage(symbol,SIZE,45));
        images.add( createGraphicImage(symbol,SIZE,60));
        images.add( createGraphicImage(symbol,SIZE,75));
        images.add( createGraphicImage(symbol,SIZE,90));

        showImage(images);
    }

    private static void testGraphicExternal(){
        GraphicalSymbol symbol = createExternal();
        Collection<Image> images = new ArrayList<Image>();
        images.add( createGraphicImage(symbol,SIZE,0));
        images.add( createGraphicImage(symbol,SIZE,15));
        images.add( createGraphicImage(symbol,SIZE,30));
        images.add( createGraphicImage(symbol,SIZE,45));
        images.add( createGraphicImage(symbol,SIZE,60));
        images.add( createGraphicImage(symbol,SIZE,75));
        images.add( createGraphicImage(symbol,SIZE,90));

        
        List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(symbol);
        
        Graphic gra = SF.graphic(symbols,
                StyleConstants.DEFAULT_STROKE_OPACITY, 
                FF.literal(20),
                StyleConstants.DEFAULT_POINTPLACEMENT_ROTATION, 
                StyleConstants.DEFAULT_ANCHOR_POINT, 
                StyleConstants.DEFAULT_DISPLACEMENT);
        Symbolizer sym = SF.pointSymbolizer(gra, "geom");
        
        
        System.out.println(gra);
        System.out.println(gra.graphicalSymbols().get(0));
        
        FeatureTypeStyle fts = SF.featureTypeStyle(sym);
        
        XMLUtilities tool = new XMLUtilities();
        try {
            tool.writeFeatureTypeStyle(new File("temp.sld"), fts, Specification.SymbologyEncoding.V_1_1_0);
        } catch (JAXBException ex) {
            Logger.getLogger(J2DStyleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        showImage(images);
    }


    private static Mark createMark(String strWkn){

        Graphic graphic = SF.graphic();                                    //OK
        GraphicFill graphicFill = SF.graphicFill(
                graphic.graphicalSymbols(),
                graphic.getOpacity(),
                graphic.getSize(),
                graphic.getRotation(),
                graphic.getAnchorPoint(),
                graphic.getDisplacement());       

        Expression fillColor = SF.literal(Color.BLUE);                           //OK
        Expression fillOpacity = FF.literal(1d);                               //OK

        Fill fill = SF.fill(null, fillColor, fillOpacity);


        Expression color = SF.literal(Color.WHITE);                           //OK
        Expression opacity = FF.literal(1d);                               //OK
        Expression width = FF.literal(3d);                                //OK
        Expression join = FF.literal("bevel");                             //OK
        Expression cap = FF.literal("round");                              //OK
        float[] dashes = new float[]{};                                                             //OK
        Expression strokeOffset = FF.literal(0d);                          //OK

        Stroke stroke = SF.stroke(color, opacity, width, join, cap, dashes, strokeOffset);


        OnLineResource rsc = new DefaultOnLineResource(new File("/home/sorel/temp/small.png").toURI());//SAIS PAS ENCORE COMMENT LE TRAITER
        Icon icon = null;                                                                           //OK
        try {
            icon = new ImageIcon(ImageIO.read(new File("/home/sorel/temp/small.png")));
        } catch (IOException ex) {
            Logger.getLogger(J2DStyleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String format = "image/png";                                                                //SAIS PAS ENCORE COMMENT LE TRAITER
        int index = 0;                                                                              //SAIS PAS ENCORE COMMENT LE TRAITER
        ExternalMark external = SF.externalMark(rsc, format, index);


        Expression wkn = FF.literal(strWkn);                               //OK
        Mark mark = SF.mark(wkn, fill, stroke);

        return mark;

    }

    private static ExternalGraphic createExternal(){
        OnLineResource rsc = new DefaultOnLineResource(new File("/home/sorel/temp/small.png").toURI());
//        Icon icon = null;
//        try {
//            icon = new ImageIcon(ImageIO.read(new File("/home/sorel/temp/small.png")));
//        } catch (IOException ex) {
//            Logger.getLogger(J2DStyleTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
        String format = "image/png";

        ExternalGraphic external = SF.externalGraphic(rsc, format, null);
        return external;
    }

    private static Image createMarkImage(String strWkn,float size){
        CachedMark mark = new CachedMark(createMark(strWkn));
        return mark.getImage(null, size, null);
    }

    private static Image createExternalImage(float size){
        CachedExternal external = new CachedExternal(createExternal());
        return external.getImage(size,null);
    }

    private static Image createGraphicImage(GraphicalSymbol symbol, float size, float rotation){

        List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(symbol);

        Expression opacity = FF.literal(1d);     //OK
        Expression expSize = FF.literal(size);     //OK
        Expression expRotation = FF.literal(rotation);         //OK
        AnchorPoint anchor = null;                                          //NOT USE HERE, only at the last paint
        Displacement disp = null;                                           //NOT USE HERE, only at the last paint
        Graphic graphic = SF.graphic(symbols, opacity, expSize, expRotation, anchor,disp);

        CachedGraphic cache = new CachedGraphic(graphic);

        return cache.getImage(null,1,null);
    }

    private static void showImage(final Collection<Image> imgs){

        JFrame frm = new JFrame();



        JPanel panel = new JPanel(){

            int X = 0;
            int Y = 0;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                X=10;
                Y=10;

                for(Image image : imgs){
                    if(image != null){
                        g2.setColor(Color.RED);
                        g2.fillRect(X, Y, image.getWidth(this), image.getHeight(this));
                        g2.drawImage(image, X,Y, this);
                        X += image.getWidth(this) +5;
                    }
                }


            }

        };
        panel.setBackground(Color.BLACK);
        panel.setSize(800,200);

        JScrollPane jsp = new JScrollPane(panel);

        frm.setContentPane(jsp);

        frm.setSize(1200, 400);
        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);

    }


}
