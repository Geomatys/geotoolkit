
package org.geotoolkit.display3d.canvas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

import com.ardor3d.framework.ArdorModule;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.awt.AwtCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import javax.swing.JComponent;
import org.geotoolkit.display3d.controller.A3DController;
import org.geotoolkit.display3d.container.A3DContainer;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.factory.Hints;

import org.lwjgl.LWJGLException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class A3DCanvas extends ReferencedCanvas{

    public static final ArdorModule  ARDOR_3D = new ArdorModule();
    public static final Injector     INJECTOR = Guice.createInjector(Stage.PRODUCTION, ARDOR_3D);
    public static final FrameHandler FRAMEWORK = INJECTOR.getInstance(FrameHandler.class);
    public static final LogicalLayer LOGICAL_LAYER = INJECTOR.getInstance(LogicalLayer.class);

    private final A3DContainer container = new A3DContainer(this);
    private final A3DController controller = new A3DController(this, LOGICAL_LAYER);
    private final JPanel swingPane = new JPanel(new BorderLayout());
    private AwtCanvas canvas = null;

    public A3DCanvas(CoordinateReferenceSystem objectiveCRS, Hints hints) {
        super(objectiveCRS,hints);
        try {
            initContext();
        } catch (LWJGLException ex) {
            Logger.getLogger(A3DCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }

        swingPane.add(BorderLayout.CENTER,canvas);
        swingPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                swingPane.remove(canvas);
                try {
                    updateCanvas(swingPane.getSize());
                } catch (LWJGLException ex) {
                    ex.printStackTrace();
                }
                swingPane.add(BorderLayout.CENTER,canvas);
                swingPane.revalidate();
            }
        });

    }

    @Override
    public A3DController getController() {
        return controller;
    }

    public A3DContainer getContainer2() {
        return container;
    }

    @Override
    public AbstractContainer getContainer() {
        return null;
    }

    public JComponent getComponent(){
        return swingPane;
    }

    public AwtCanvas getNativeCanvas(){
        return canvas;
    }

    private void initContext() throws LWJGLException{
        FRAMEWORK.addUpdater(controller);
        controller.init();
        updateCanvas(new Dimension(200,200));
    }

    private AwtCanvas updateCanvas(Dimension dim) throws LWJGLException{

        //unregister the previous canvas
        if(canvas != null){
            FRAMEWORK.removeCanvas(canvas);
        }

        canvas = new AwtCanvas();
        LwjglCanvasRenderer renderer = new LwjglCanvasRenderer(container);
        canvas.setCanvasRenderer(renderer);
        canvas.setSize(dim);
        canvas.setPreferredSize(new Dimension(100,100));
        canvas.setVisible(true);

        final AwtMouseWrapper    mouseWrapper    = new AwtMouseWrapper(canvas);
        final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(canvas);
        final AwtFocusWrapper    focusWrapper    = new AwtFocusWrapper(canvas);
        final AwtMouseManager    mouseManager    = new AwtMouseManager(canvas);

        final PhysicalLayer pl = new PhysicalLayer(keyboardWrapper, mouseWrapper, focusWrapper);

        LOGICAL_LAYER.registerInput(canvas, pl);

        LOGICAL_LAYER.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.H),
            new TriggerAction() {
            @Override
                public void perform(Canvas source, InputState inputState, double tpf) {
                    if (source != canvas) {
                        return;
                    }
                }
            }));
        LOGICAL_LAYER.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.J),
            new TriggerAction() {
            @Override
                public void perform(Canvas source, InputState inputState, double tpf) {
                    if (source != canvas) {
                        return;
                    }

                    mouseManager.setCursor(MouseCursor.SYSTEM_DEFAULT);
                }
            }));

        FRAMEWORK.addCanvas(canvas);

        return canvas;
    }

    @Override
    protected RenderingContext getRenderingContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
