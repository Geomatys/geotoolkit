/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.crs;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import static javax.swing.SwingConstants.EAST;
import static javax.swing.SwingConstants.NORTH;
import static javax.swing.SwingConstants.SOUTH;
import static javax.swing.SwingConstants.WEST;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.display.axis.Graduation;
import org.geotoolkit.display.axis.NumberGraduation;
import org.geotoolkit.display.axis.TickIterator;
import org.geotoolkit.math.XMath;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXAxisViewSkin extends SkinBase<FXAxisView> {

    private static final Color CBASE = Color.GRAY;
    private static final Color CTOP = Color.LIGHTGRAY;
    private static final Font FONT_MAJOR = new Font("Serif", 12);
    private static final Font FONT_MINOR = new Font("Serif", 10);
    private static final Font FONT_TIME = new Font("Monospaced", 12);
    private static final List<TimeSubdivision> SUBDIVISIONS = new ArrayList<>();
    static {
        SUBDIVISIONS.add(new TimeSubdivision.Year());
        SUBDIVISIONS.add(new TimeSubdivision.Month());
        SUBDIVISIONS.add(new TimeSubdivision.Day());
        SUBDIVISIONS.add(new TimeSubdivision.Hour());
        SUBDIVISIONS.add(new TimeSubdivision.Quarter());
        SUBDIVISIONS.add(new TimeSubdivision.Minute());
    }

    private final Group root = new Group();
    private final Rectangle background = new Rectangle();

    private double mouseCoord = 0.0;
    private double lastMouseCoord = 0.0;

    public FXAxisViewSkin(final FXAxisView control) {
        super(control);
        background.widthProperty().bind(control.widthProperty());
        background.heightProperty().bind(control.heightProperty());
        getChildren().add(background);
        getChildren().add(root);
        root.setAutoSizeChildren(false);
        root.setManaged(false);
        root.setCache(false);


        //TODO : control always grow, find a way to avoid it
        control.setMinSize(100,70);
        control.setMaxHeight(70);

        final Stop[] stops = new Stop[] { new Stop(0, CBASE), new Stop(1, CTOP)};
        final LinearGradient mask = new LinearGradient(0.0, 1.0, 0.0, 0.0, true, CycleMethod.NO_CYCLE, stops);
        background.setFill(mask);


        final ChangeListener listener = new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                updateGraphic();
            }
        };

        control.scaleProperty().addListener(listener);
        control.offsetProperty().addListener(listener);
        control.crsProperty().addListener(listener);
        control.widthProperty().addListener(listener);
        control.heightProperty().addListener(listener);
        control.rangeMinProperty().addListener(listener);
        control.rangeMaxProperty().addListener(listener);

        updateGraphic();

        control.setOnMouseMoved((MouseEvent event) -> {
            lastMouseCoord = event.getX();
        });
        control.setOnScroll((ScrollEvent event) -> {
            control.scale(1.0 + Math.toRadians(event.getDeltaY())*0.3, lastMouseCoord);
        });
        control.setOnMouseDragEntered((MouseDragEvent event) -> {
            lastMouseCoord = mouseCoord = event.getX();
        });
        control.setOnMouseDragged((MouseEvent event) -> {
            mouseCoord = event.getX();
            control.translate(mouseCoord-lastMouseCoord);
            lastMouseCoord = mouseCoord;
        });


        final MenuItem removeRange = new MenuItem("remove range");
        removeRange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                control.rangeMinProperty().set(null);
                control.rangeMaxProperty().set(null);
            }
        });
        final MenuItem markRange = new MenuItem("mark range");
        markRange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final double val = control.getAxisValueAt(lastMouseCoord);
                control.rangeMinProperty().set(val);
                control.rangeMaxProperty().set(val);
            }
        });

        final ContextMenu menu = new ContextMenu(removeRange, markRange);
        control.setContextMenu(menu);

    }

    private void updateGraphic(){

        final FXAxisView view = (FXAxisView) getNode();
        CoordinateReferenceSystem crs = view.crsProperty().get();
        if(crs==null) crs = CommonCRS.Temporal.JAVA.crs();

        final boolean temporal = Units.SECOND.isCompatible(crs.getCoordinateSystem().getAxis(0).getUnit());

        final Bounds area = view.getLayoutBounds();

        final int orientation = view.orientationProperty().get();
        final boolean horizontal = orientation == NORTH || orientation == SOUTH;
        final boolean flipText = orientation == NORTH || orientation == WEST;
        final double extent = (horizontal) ? area.getWidth() : area.getHeight();


        final Collection<Shape> ticks = new ArrayList<>();

        if(!temporal){
            //draw number graduations ------------------------------------------
            final int spacing = 200;

            final double start = view.getAxisValueAt(-spacing);
            final double end = view.getAxisValueAt(extent+spacing);

            final RenderingHints tickHint = new RenderingHints(null);
            tickHint.put(Graduation.VISUAL_AXIS_LENGTH, extent+spacing);
            tickHint.put(Graduation.VISUAL_TICK_SPACING, spacing/2);

            final NumberGraduation graduationX = new NumberGraduation(null);
            graduationX.setRange(start, end, null);
            final TickIterator tickIte = graduationX.getTickIterator(tickHint, null);

            while(!tickIte.isDone()){
                tickIte.next();
                final String label = tickIte.currentLabel();
                final double d = tickIte.currentPosition();
                final double p =view.getGraphicValueAt(d);

                final boolean majorTick = tickIte.isMajorTick();
                final double strokeWidth = (majorTick) ? 2.5 : 1.0;
                final Font font =  (majorTick) ? FONT_MAJOR : FONT_MINOR;

                switch(orientation){
                    case NORTH : break;
                    case SOUTH :
                        final Line lineSouth = new Line(p, 0, p,area.getHeight());
                        lineSouth.setStroke(CTOP);
                        lineSouth.setStrokeWidth(strokeWidth);
                        final Text textSouth = new Text(p, area.getHeight()-2, label);
                        textSouth.setFill(Color.WHITE);
                        textSouth.setFont(font);
                        ticks.add(lineSouth);
                        ticks.add(textSouth);
                        break;
                    case EAST : break;
                    case WEST :
                        final Line lineWest = new Line(0, p, area.getWidth(), p);
                        lineWest.setStroke(CTOP);
                        lineWest.setStrokeWidth(strokeWidth);
                        final Text textWest = new Text(2, p-2, label);
                        textWest.setFill(Color.WHITE);
                        textWest.setFont(font);
                        ticks.add(lineWest);
                        ticks.add(textWest);
                        break;
                }
            }

        }else{
            //draw time graduations --------------------------------------------

            Color lineColor = Color.GRAY;
            Color textColor = Color.GRAY;

            final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(FONT_TIME);
            final double compactBandHeight = fm.getAscent() +10;
            final double height = area.getHeight();
            final double width = area.getWidth();

            //draw the two background gradient
            final Rectangle mask1 = new Rectangle(0, 0, width, height-compactBandHeight);
            mask1.setFill(new LinearGradient(0.0, 0.0, 0.0, 1.0, true,
                    CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1,  Color.LIGHTGRAY)));
            ticks.add(mask1);
            final Rectangle mask2 = new Rectangle(0, height-compactBandHeight, width, compactBandHeight);
            mask2.setFill(new LinearGradient(0.0, 0.0, 0.0, 1.0, true,
                    CycleMethod.NO_CYCLE, new Stop(0, Color.GRAY), new Stop(1,  Color.LIGHTGRAY)));
            ticks.add(mask2);


            final long beginInterval = (long) view.getAxisValueAt(0);
            final long endInterval = (long) view.getAxisValueAt(width);

            final List<TimeSubdivision> compact = new ArrayList<>();


            for(int i=0;i<SUBDIVISIONS.size();i++){
                final TimeSubdivision sub = SUBDIVISIONS.get(i);
                final double textsize = sub.getTextLength(fm);
                final double scale = sub.getUnitLength();
                final double stepWidth = scale * view.scaleProperty().get();

                final boolean showLine = stepWidth > 15 ;
                final boolean showText = stepWidth > textsize ;

                if(!showLine){
                    //to narrow to show lines, skip this division and all followings
                    break;
                }

                if(stepWidth > width/3 && i<SUBDIVISIONS.size()-1){
                    //add to compact group if larg enough and not last subdivision
                    if(!sub.isIntermediate()){
                        compact.add(sub);
                    }
                    continue;
                }

                final long[] steps = sub.getSteps(beginInterval, endInterval);
                for(long step : steps){
                    final double x = view.getGraphicValueAt(step);

                    final Line line = new Line(x, 0, x, height-compactBandHeight);
                    line.setFill(lineColor);
                    ticks.add(line);

                    if(showText){
                        final Text text = new Text((float)x+3, height-compactBandHeight-fm.getMaxDescent(), sub.getText((long)step));
                        text.setFill(textColor);
                        ticks.add(text);
                    }
                }

                //we have draw one type of subdivision, skip others
                break;
            }

            lineColor = Color.BLACK;
            final Line separator = new Line(0, height-compactBandHeight, width, height-compactBandHeight);
            separator.setFill(lineColor);
            ticks.add(separator);


            //draw compacts
            textColor = Color.WHITE;
            int x = 5;
            for(int i=0;i<compact.size();i++){
                final TimeSubdivision sub = compact.get(i);

                //draw text
                final String text = sub.getText(beginInterval)+sub.getUnitText()+" ";
                final Text textShape = new Text(x, height-fm.getMaxDescent()-3,text);
                textShape.setFill(textColor);
                ticks.add(textShape);

                x += fm.computeStringWidth(text);

                //last element, we draw possible other lines
                if(i==compact.size()-1){

                    final long[] steps = sub.getSteps(beginInterval, endInterval);
                    for(long step : steps){
                        final double lx = view.getGraphicValueAt(step);
                        final String lt = sub.getText(step);

                        final Line line = new Line(lx, height-compactBandHeight, lx, height);
                        line.setFill(lineColor);
                        ticks.add(line);

                        if(lx > x){
                            final Text compactText = new Text((float)lx+3, height-fm.getMaxDescent()-3, lt);
                            compactText.setFill(textColor);
                            ticks.add(compactText);
                        }
                    }
                }

            }
        }


        //update the selection
        final Number rangeMin = view.rangeMinProperty().get();
        final Number rangeMax = view.rangeMaxProperty().get();
        if(rangeMin!=null && rangeMax!=null){
            double min = view.getGraphicValueAt(rangeMin.doubleValue());
            double max = view.getGraphicValueAt(rangeMax.doubleValue());

            if(max > 0 && min < area.getWidth()){
                //clip value in visible range
                min = XMath.clamp(min, -10, area.getWidth()+10);
                max = XMath.clamp(max, -10, area.getWidth()+10);

                final double width = max-min;

                final Rectangle rectangle = new Rectangle(min, 0, width, area.getHeight());
                rectangle.setFill(new Color(0, 0, 1, 0.3));
                ticks.add(rectangle);

                final Line border1 = new Line(min, 0, min, area.getHeight());
                border1.setStrokeWidth(4);
                border1.setStroke(Color.BLUE);
                ticks.add(border1);

                final Line border2 = new Line(max, 0, max, area.getHeight());
                border2.setStrokeWidth(4);
                border2.setStroke(Color.BLUE);
                ticks.add(border2);
            }
        }

        root.getChildren().setAll(ticks);

    }



    @Override
    public void dispose() {
        super.dispose();
        getChildren().remove(root);
    }

}
