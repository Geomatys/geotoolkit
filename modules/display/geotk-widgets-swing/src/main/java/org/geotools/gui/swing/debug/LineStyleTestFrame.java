/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.gui.swing.debug;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Stroke;

/**
 *
 * @author sorel
 */
public class LineStyleTestFrame extends JComponent{

    private final MutableStyleFactory STYLE_BUILDER = new DefaultStyleFactory();
    private final Shape polyline ;

    public static void main(String[] args){
        JFrame frm = new JFrame();
        frm.setSize(800, 600);

        frm.setContentPane(new JPanel(new BorderLayout()));
        frm.getContentPane().add(BorderLayout.CENTER,new LineStyleTestFrame());

        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);

    }

    public LineStyleTestFrame() {
        int x2Points[] = {0, 10, 120, 200};
        int y2Points[] = {0, 50, 10, 40};
        GeneralPath polyline =
                new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);

        polyline.moveTo (x2Points[0], y2Points[0]);

        for (int index = 1; index < x2Points.length; index++) {
                 polyline.lineTo(x2Points[index], y2Points[index]);
        }


        this.polyline = polyline;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        LineSymbolizer lineSymbol = STYLE_BUILDER.lineSymbolizer();
        final int TRANSLATE_X = 20;
        final int TRANSLATE_Y = 50;



        // static test--------------------------------------------------------
        g2.translate(TRANSLATE_X, TRANSLATE_Y);
        
//        //OK
//        lineSymbol.getStroke().setColor(STYLE_BUILDER.colorExpression(Color.BLUE));
//        //OK
//        lineSymbol.getStroke().setDashArray(new float[]{10,10});
//        //OK
//        lineSymbol.getStroke().setDashOffset(STYLE_BUILDER.literalExpression(10f));
//        
////        lineSymbol.getStroke().setGraphicFill();
////        lineSymbol.getStroke().setGraphicStroke();
//        
//        //OK
//        lineSymbol.getStroke().setLineCap(STYLE_BUILDER.literalExpression("butt"));
//        //OK
//        lineSymbol.getStroke().setLineJoin(STYLE_BUILDER.literalExpression("round"));
//        //OK
//        lineSymbol.getStroke().setOpacity(STYLE_BUILDER.literalExpression(0.7f));
//        //OK
//        lineSymbol.getStroke().setWidth(STYLE_BUILDER.literalExpression(10));

        portray(lineSymbol, g2);
        g2.draw(polyline);

    }



    private void portray(LineSymbolizer symbol, Graphics2D g2){

        Stroke seStroke = symbol.getStroke();

        Expression expColor = seStroke.getColor();
        float[] dashArray = seStroke.getDashArray();
        Expression expOffset = seStroke.getDashOffset();
//        Graphic graphicFill = seStroke.getGraphicFill();
//        Graphic graphicStroke = seStroke.getGraphicStroke();
        Expression expLineCap = seStroke.getLineCap();
        Expression expLineJoin = seStroke.getLineJoin();
        Expression expOpacity = seStroke.getOpacity();
        Expression expWidth = seStroke.getWidth();


        
//        graphicFill.getDisplacement();
//        graphicFill.getExternalGraphics();
//        graphicFill.getGeometryPropertyName();
//        graphicFill.getMarks();
//        graphicFill.getOpacity();
//        graphicFill.getRotation();
//        graphicFill.getSize();
//        graphicFill.getSymbols();
                

        Color j2dColor = expColor.evaluate(null,Color.class);
        float j2dOffset = expOffset.evaluate(null,Float.class);

        int j2dLineCap;
        String strLineCap = expLineCap.evaluate(null, String.class).toLowerCase();
        if (strLineCap.equals("butt")) {
            j2dLineCap = BasicStroke.CAP_BUTT;
        } else if (strLineCap.equals("square")) {
            j2dLineCap = BasicStroke.CAP_SQUARE;
        } else if (strLineCap.equals("round")) {
            j2dLineCap = BasicStroke.CAP_ROUND;
        } else{
            j2dLineCap = BasicStroke.CAP_BUTT;
        }

        int j2dLineJoin;
        String strLineJoin = expLineJoin.evaluate(null, String.class).toLowerCase();
        if (strLineJoin.equals("bevel")) {
            j2dLineJoin = BasicStroke.JOIN_BEVEL;
        } else if (strLineJoin.equals("mitre")) {
            j2dLineJoin = BasicStroke.JOIN_MITER;
        } else if (strLineJoin.equals("round")) {
            j2dLineJoin = BasicStroke.JOIN_ROUND;
        } else{
            j2dLineJoin = BasicStroke.JOIN_BEVEL;
        }

        float j2dOpacity = expOpacity.evaluate(null, Float.class);

        float j2dWidth = expWidth.evaluate(null, Float.class);


        // create the stroke,paint,composite ---------------------------------------------

        BasicStroke stroke;
        if(validDashes(dashArray)){
            stroke = new BasicStroke(j2dWidth, j2dLineCap, j2dLineJoin, 10f, dashArray, j2dOffset);
        }else{
            stroke = new BasicStroke(j2dWidth, j2dLineCap, j2dLineJoin, 10f);
        }
        Paint paint = j2dColor;
        Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, j2dOpacity);

        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.setComposite(composite);


    }


    private boolean validDashes(float[] dashes){
        if(dashes == null || dashes.length ==0 ){
            return false;
        }else{
            for (float f : dashes) {
                if(f == 0){
                    return false;
                }
            }
            return true;
        }
    }


}
