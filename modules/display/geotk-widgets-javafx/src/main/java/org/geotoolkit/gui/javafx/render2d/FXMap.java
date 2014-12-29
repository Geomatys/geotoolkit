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

package org.geotoolkit.gui.javafx.render2d;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javax.swing.Timer;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.canvas.control.NeverFailMonitor;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.J2DCanvasVolatile;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.javafx.util.NextPreviousList;
import org.geotoolkit.internal.Loggers;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMap extends BorderPane {

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain getHandler canvas handler} changed.
     */
    public static final String HANDLER_PROPERTY = "handler";
    private static final FXMapDecoration[] EMPTY_OVERLAYER_ARRAY = {};

    private final ObjectProperty<FXCanvasHandler> handlerProp = new SimpleObjectProperty<FXCanvasHandler>();
    private final J2DCanvasVolatile canvas;
    private boolean statefull = false;

    private WritableImage image = null;
    private final Canvas view = new ResizableCanvas();
    //used to repaint the buffer at regular interval until it is finished
    private final Timer progressTimer = new Timer(250, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            updateImage();
        }
    });
    
    
    private final List<FXMapDecoration> userDecorations = new ArrayList<>();
    private final StackPane mapDecorationPane = new StackPane();
    private final StackPane userDecorationPane = new StackPane();
    private final StackPane mainDecorationPane = new StackPane();
    private final NextPreviousList<AffineTransform> nextPreviousList = new NextPreviousList<>(10);
    private int nextMapDecorationIndex = 1;
    private FXInformationDecoration informationDecoration = new DefaultInformationDecoration();
    private FXMapDecoration backDecoration = new FXColorDecoration();
    
    public FXMap(){
        this(false,null);
    }

    public FXMap(final boolean statefull) {
        this(statefull, null);
    }
    
    public FXMap(final boolean statefull, Hints hints){
//        setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        view.heightProperty().bind(mapDecorationPane.heightProperty());
        view.widthProperty().bind(mapDecorationPane.widthProperty());
        
        mapDecorationPane.getChildren().add(0,view);
        mainDecorationPane.getChildren().add(0,backDecoration.getComponent());
        mainDecorationPane.getChildren().add(1,mapDecorationPane);
        mainDecorationPane.getChildren().add(2,userDecorationPane);
        mainDecorationPane.getChildren().add(3,informationDecoration.getComponent());
        informationDecoration.setMap2D(this);
        setCenter(mainDecorationPane);
        
        
        canvas = new J2DCanvasVolatile(CommonCRS.WGS84.normalizedGeographic(), new Dimension(100, 100), hints);
        canvas.setMonitor(new NeverFailMonitor());
        canvas.setContainer(new ContextContainer2D(canvas, statefull));
        canvas.setAutoRepaint(true);        
        canvas.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                if(AbstractCanvas.RENDERSTATE_KEY.equals(evt.getPropertyName())){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateImage();
                            
                            final Object state = evt.getNewValue();
                            if(AbstractCanvas.RENDERING.equals(state)){
                                getInformationDecoration().setPaintingIconVisible(true);
                                progressTimer.start();
                            }else{
                                getInformationDecoration().setPaintingIconVisible(false);
                                progressTimer.stop();
                                updateImage();
                            }
                        }
                    });
                    
                }else if(J2DCanvas.TRANSFORM_KEY.equals(evt.getPropertyName())){
                    nextPreviousList.put(canvas.getCenterTransform());
                }
            }
        });
        canvas.getContainer().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(ContextContainer2D.CONTEXT_PROPERTY.equals(evt.getPropertyName())){
                    canvas.repaint();
                }
            }
        });
        
    }

    private boolean first = true;

    public NextPreviousList<AffineTransform> getNextPreviousList() {
        return nextPreviousList;
    }
    
    @Override
    protected void updateBounds() {
        super.updateBounds();
        if(first){
            //zoom on map area
            first = false;
            try {
                getCanvas().setVisibleArea(getContainer().getContext().getAreaOfInterest());
            } catch (Exception ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(),ex);
            }
        }
    }
    
    private void updateImage() {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final GraphicsContext g = view.getGraphicsContext2D();
                g.clearRect(0, 0, view.getWidth(), view.getHeight());
                final BufferedImage snapshot = (BufferedImage) canvas.getSnapShot();
                if (snapshot != null && snapshot.getWidth()==view.getWidth() && snapshot.getHeight() == view.getHeight()) {
                    //JavaFX bug : argb snapshot image from volatile image creates a black background
                    final BufferedImage img = new BufferedImage((int)view.getWidth(), (int)view.getHeight(), BufferedImage.TYPE_INT_RGB);
                    img.createGraphics().drawImage(snapshot, 0, 0, null);
                    image = SwingFXUtils.toFXImage(img, image);
                    g.setGlobalBlendMode(BlendMode.SRC_OVER);
                    g.drawImage(image, 0, 0);
                }
            }
        };
        
        if(Platform.isFxApplicationThread()){
            r.run();
        }else{
            Platform.runLater(r);
        }
    }
    
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        canvas.resize(new Dimension((int)width, (int)height));
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        canvas.resize(new Dimension((int)width, (int)height));
    }

    public boolean isStatefull() {
        return statefull;
    }

    /**
     * @return the effective Go2 Canvas.
     */
    public J2DCanvas getCanvas() {
        return canvas;
    }

    public ContextContainer2D getContainer(){
        return (ContextContainer2D) canvas.getContainer();
    }

    /**
     * Must be called when the map2d is not used anymore.
     * to avoid memory leak if it uses thread or other resources
     */
    public void dispose() {
        canvas.dispose();
    }

    public ReadOnlyObjectProperty<FXCanvasHandler> getHandlerProperty(){
        return handlerProp;
    }
    
    public FXCanvasHandler getHandler(){
        return handlerProp.getValue();
    }

    public void setHandler(final FXCanvasHandler handler){

        if(getHandler() != handler) {
            //TODO : check for possible vetos

            final FXCanvasHandler old = getHandler();

            if (old != null){
                boolean veto = old.uninstall(this);
                if(!veto){
                    //handler can not be removed right now, veto
                    //could be an edition tool which is in an unfinished state
                    return;
                }
            }

            handlerProp.setValue(handler);

            if (handler != null) {
                handler.install(this);
            }

            //firePropertyChange(HANDLER_PROPERTY, old, handler);
        }

    }

    //----------------------Use as extend for subclasses------------------------
    protected void setRendering(final boolean render) {
        informationDecoration.setPaintingIconVisible(render);
    }

    //----------------------Over/Sub/information layers-------------------------
    /**
     * set the top InformationDecoration of the map2d widget
     * @param info , can't be null
     */
    public void setInformationDecoration(final FXInformationDecoration info) {
        ArgumentChecks.ensureNonNull("info decoration", info);

        mainDecorationPane.getChildren().remove(informationDecoration.getComponent());
        informationDecoration = info;
        mainDecorationPane.getChildren().add(3,informationDecoration.getComponent());
    }

    /**
     * get the top InformationDecoration of the map2d widget
     * @return InformationDecoration
     */
    public FXInformationDecoration getInformationDecoration() {
        return informationDecoration;
    }

    /**
     * set the decoration behind the map
     * @param back : MapDecoration, can't be null
     */
    public void setBackgroundDecoration(final FXMapDecoration back) {
        ArgumentChecks.ensureNonNull("background decoration", back);

        mainDecorationPane.getChildren().remove(backDecoration.getComponent());
        backDecoration = back;
        mainDecorationPane.getChildren().add(0, backDecoration.getComponent());
    }

    /**
     * get the decoration behind the map
     * @return MapDecoration : or null if no back decoration
     */
    public FXMapDecoration getBackgroundDecoration() {
        return backDecoration;
    }

    /**
     * add a Decoration between the map and the information top decoration
     * @param deco : MapDecoration to add
     */
    public void addDecoration(final FXMapDecoration deco) {

        if (deco != null && !userDecorations.contains(deco)) {
            deco.setMap2D(this);
            userDecorations.add(deco);
            userDecorationPane.getChildren().add(userDecorations.indexOf(deco), deco.getComponent());
        }
    }

    /**
     * insert a MapDecoration at a specific index
     * @param index : index where to isert the decoration
     * @param deco : MapDecoration to add
     */
    public void addDecoration(final int index, final FXMapDecoration deco) {

        if (deco != null && !userDecorations.contains(deco)) {
            deco.setMap2D(this);
            userDecorations.add(index, deco);
            userDecorationPane.getChildren().add(userDecorations.indexOf(deco), deco.getComponent());
        }
    }

    /**
     * get the index of a MapDecoration
     * @param deco : MapDecoration to find
     * @return index of the MapDecoration
     * @throw ClassCastException or NullPointerException
     */
    public int getDecorationIndex(final FXMapDecoration deco) {
        return userDecorations.indexOf(deco);
    }

    /**
     * remove a MapDecoration
     * @param deco : MapDecoration to remove
     */
    public void removeDecoration(final FXMapDecoration deco) {
        if (deco != null && userDecorations.contains(deco)) {
            deco.setMap2D(null);
            deco.dispose();
            userDecorations.remove(deco);
            userDecorationPane.getChildren().remove(deco.getComponent());
        }
    }

    /**
     * get an array of all MapDecoration
     * @return array of MapDecoration
     */
    public FXMapDecoration[] getDecorations() {
        return userDecorations.toArray(EMPTY_OVERLAYER_ARRAY);
    }

    /**
     * add a MapDecoration between the map and the user MapDecoration
     * those MapDecoration can not be removed because they are important
     * for edition/selection/navigation.
     * @param deco : MapDecoration to add
     */
    protected void addMapDecoration(final FXMapDecoration deco) {
        mapDecorationPane.getChildren().add(nextMapDecorationIndex, deco.getComponent());
        nextMapDecorationIndex++;
    }
    
    private class ResizableCanvas extends Canvas{
        
        public ResizableCanvas() {}
  
        @Override
        public boolean isResizable() {
            return true;
        }
 
        @Override
        public double prefWidth(double height) {
            return 10;
        }
 
        @Override
        public double prefHeight(double width) {
            return 10;
        }
        
    }
    
}
