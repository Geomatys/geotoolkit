package org.geotoolkit.gui.javafx.render2d;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.ext.legend.DefaultLegendService;
import org.geotoolkit.display2d.ext.legend.LegendTemplate;
import org.geotoolkit.gui.javafx.render2d.navigation.AbstractMouseHandler;
import org.geotoolkit.gui.javafx.util.TaskManager;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.apache.sis.util.logging.Logging;

/**
 * A decoration which will display legend for a given map context. Legend can be
 * displayed a simple decoration on map, or in a popup.
 *
 * @author Alexis Manin (Geomatys)
 */
public class FXLegendDecoration extends StackPane implements FXMapDecoration {

    private final SimpleBooleanProperty popupMode = new SimpleBooleanProperty();

    /** A progress indicator displayed when computing a new legend. */
    private final ProgressIndicator computingIndicator = new ProgressIndicator();
    /** A trigger to notify when progress indicator must display. */
    private final SimpleBooleanProperty computingRunning = new SimpleBooleanProperty(false);

    /**
     * The {@link ImageView} which will contains the legend image.
     */
    private final ImageView legendGraphic = new ImageView();

    /**
     * A scroll pane in which we will display legend. It allows user to navigate
     * if the legend height is bigger than current map.
     */
    //private final ScrollPane scrollPane = new ScrollPane(legendGraphic);

    private final Popup p = new Popup();

    /** The template which contains rules to use to paint legend. */
    public LegendTemplate legendTemplate;

    public final SimpleObjectProperty<FXMap> map2D = new SimpleObjectProperty<>();

    /** Target map context of the legend. */
    public final SimpleObjectProperty<MapContext> mapContext = new SimpleObjectProperty<>();

    /**
     * A listener to register on current {@link MapContext}
     */
    private final ContextListener contextListener;

    public FXLegendDecoration() {
        this(null, false);
    }

    public FXLegendDecoration(final LegendTemplate template, boolean displayAsPopup) {
        legendTemplate = template;
        popupMode.set(displayAsPopup);

        popupMode.addListener(this::updatePopupMode);

        prefWidthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            refresh();
        });
        prefHeightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            refresh();
        });

        addEventHandler(MouseEvent.ANY, new DragLegend());

        contextListener = new LegendRefresh();
        mapContext.addListener(this::updateContext);

        map2D.addListener(this::updateMap2D);

        setAlignment(Pos.TOP_LEFT);

        visibleProperty().bind(mapContext.isNotNull());
        managedProperty().bind(visibleProperty());

        computingIndicator.visibleProperty().bind(computingRunning);

        getChildren().addAll(legendGraphic, computingIndicator);
        p.getContent().add(this);
        setFocusTraversable(true);
    }

    @Override
    public void refresh() {
        computingRunning.set(true);
        TaskManager.INSTANCE.submit(() -> {
            try {
                if (mapContext.get() != null && legendTemplate != null) {
                    final Dimension d = DefaultLegendService.legendPreferredSize(legendTemplate, mapContext.get());
                    WritableImage fxImage = null;
                    try {
                        final BufferedImage legend = DefaultLegendService.portray(legendTemplate, mapContext.get(), d);
                        fxImage = SwingFXUtils.toFXImage(legend, null);
                    } catch (PortrayalException ex) {
                        // TODO : make an image displaying error
                        Logging.getLogger("org.geotoolkit.gui.javafx.render2d").log(Level.WARNING, null, ex);
                    }

                    final WritableImage toShow = fxImage;
                    Platform.runLater(() -> {
                        legendGraphic.setImage(toShow);
                    });
                }
            } finally {
                Platform.runLater(() -> computingRunning.set(false));
            }
        });
    }

    @Override
    public void dispose() {
        legendGraphic.setImage(null);
        map2D.set(null);
    }

    @Override
    public void setMap2D(FXMap map) {
        map2D.set(map);
    }

    @Override
    public FXMap getMap2D() {
        return map2D.get();
    }

    @Override
    public Node getComponent() {
        return this;
    }

    /**
     * When watched {@link MapContext} changes, we update current decoration listener.
     * @param obs
     * @param oldContext If not null, we unregister our listener from it.
     * @param newContext If not null, we listen to it.
     */
    private void updateContext(final ObservableValue<? extends MapContext> obs, MapContext oldContext, MapContext newContext) {
        if (oldContext != null) {
            oldContext.removeContextListener(contextListener);
        }

        if (newContext != null) {
            newContext.addContextListener(contextListener);
        }
        refresh();
    }

    /**
     * Update decoration registration when target {@link FXMap} changes.
     * @param obs
     * @param oldMap The previous bound map. Unregister if different from null.
     * @param newMap The new Map to target. Register on it if it's not null.
     */
    private void updateMap2D(final ObservableValue<? extends FXMap> obs, FXMap oldMap, FXMap newMap) {
        p.hide();
        if (oldMap != null) {
            oldMap.removeDecoration(this);
        }
        if (newMap != null) {
            if (popupMode.get()) {
                p.show(newMap.getScene().getWindow());
            } else {
                newMap.addDecoration(this);
                setFocusTraversable(true);
            }
            mapContext.set(newMap.getContainer().getContext());
        } else {
            mapContext.set(null);
        }
    }

    /**
     * Called when popup mode is (de)activated, to update display.
     * @param obs
     * @param oldValue
     * @param newValue
     */
    private void updatePopupMode(final ObservableValue<? extends Boolean> obs, final Boolean oldValue, final Boolean newValue) {
        if (map2D.get() != null) {
            if (newValue != null && newValue) {
                map2D.get().removeDecoration(this);
                p.show(map2D.get().getScene().getWindow());
            } else {
                p.hide();
                map2D.get().addDecoration(this);
            }
        }
    }

    /**
     * A simple listener whose role is to launch legend update when watched {@link MapContext}
     * changes.
     */
    private class LegendRefresh implements ContextListener {

        @Override
        public void layerChange(CollectionChangeEvent<MapLayer> event) {
            refresh();
        }

        @Override
        public void itemChange(CollectionChangeEvent<MapItem> event) {
            refresh();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            refresh();
        }
    }

    /**
     * Move legend on drag action.
     */
    private class DragLegend extends AbstractMouseHandler {

        private Cursor defaultCursor;

        private double startX = 0;
        private double startY = 0;

        @Override
        public void mouseDragged(MouseEvent me) {
            if (MouseButton.PRIMARY.equals(me)) {
                if (popupMode.get()) {
                    p.setX(Math.max(0,
                            p.getX() + (me.getX() - startX)
                    ));
                    p.setY(Math.max(0,
                            p.getY() + (me.getY() - startY)
                    ));
                } else {
                    setTranslateX(getTranslateX() + (me.getX() - startX));
                    setTranslateY(getTranslateY() + (me.getY() - startY));
                }
                startX = me.getX();
                startY = me.getY();
            }
        }

        @Override
        public void mouseExited(MouseEvent me) {
            setCursor(defaultCursor);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            setCursor(Cursor.OPEN_HAND);
        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (MouseButton.PRIMARY.equals(me)) {
                setCursor(Cursor.MOVE);
                startX = me.getX();
                startY = me.getY();
            }
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            defaultCursor = getCursor();
            setCursor(Cursor.OPEN_HAND);
        }
    }
}
