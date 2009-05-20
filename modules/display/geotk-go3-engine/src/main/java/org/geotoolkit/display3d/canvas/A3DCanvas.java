
package org.geotoolkit.display3d.canvas;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.ardor3d.framework.Canvas;
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
import com.ardor3d.util.Timer;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;
import org.geotoolkit.display3d.controller.A3DController;
import org.geotoolkit.display3d.container.A3DContainer;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.factory.Hints;

import org.lwjgl.LWJGLException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class A3DCanvas extends ReferencedCanvas{

    public static final String CAMERA_POSITION = "camera_position";

    private final Timer timer = new Timer();

//    private final FrameHandler refresher = new FrameHandler(new Timer());
    private final LogicalLayer logicalLayer = new LogicalLayer();
    private final A3DContainer container = new A3DContainer(this);
    private final A3DController controller;
    private final JScrollPane swingPane;
    private final AwtCanvas canvas;

    public A3DCanvas(CoordinateReferenceSystem objectiveCRS, Hints hints) throws LWJGLException{
        super(objectiveCRS,hints);
        canvas = initContext();
        controller = new A3DController(this, logicalLayer);
        controller.init();

        swingPane = new JScrollPane(canvas);
        swingPane.setBorder(null);
        swingPane.setWheelScrollingEnabled(false);
        swingPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        swingPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
//        swingPane.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//
//                CanvasRenderer canvasRenderer = canvas.getCanvasRenderer();
//
//                if (canvasRenderer.getCamera() != null) {
//                    // tell our camera the correct new size
//                    canvasRenderer.getCamera().resize(canvas.getWidth(), canvas.getHeight());
//
//                    // keep our aspect ratio the same.
//                    canvasRenderer.getCamera().setFrustumPerspective(45.0,
//                            canvas.getWidth() / (float) canvas.getHeight(), 1, 3000);
//                }
//            }
//        });


        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                new Thread() {

                    private final WeakReference<AwtCanvas> ref = new WeakReference<AwtCanvas>(canvas);

                    @Override
                    public void run() {

                        while (true) {
                            final AwtCanvas canvas = ref.get();
                            if (canvas == null) {
                                break;
                            }
                            synchronized (canvas) {
                                if (canvas.isShowing()) {
                                    System.out.println("top");
//                            if(first){
//                                try {
//                                    sleep(5000);
//                                } catch (InterruptedException ex) {
//                                    Logger.getLogger(A3DCanvas.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//
//                                canvas.init();
//                                first = false;
//                            }

                                    timer.update();
                                    controller.update(timer);
                                    final CountDownLatch latch = new CountDownLatch(1);
                                    //                            canvas.repaint();
                                    canvas.draw(latch);
                                }


                            }
                        }
                    }
                }.start();


            }
        });

    }

    private boolean first= true;

    @Override
    public synchronized void setObjectiveCRS(CoordinateReferenceSystem crs) throws TransformException {
        throw new TransformException("You are not allowed to change CRS after creation on 3D canvas");
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

    private AwtCanvas initContext() throws LWJGLException{
//        refresher.addUpdater(controller);

        final AwtCanvas canvas = new AwtCanvas();
        LwjglCanvasRenderer renderer = new LwjglCanvasRenderer(container);
        canvas.setCanvasRenderer(renderer);
        canvas.setSize(new Dimension(100, 100));
        canvas.setPreferredSize(new Dimension(1,1));
        canvas.setVisible(true);

        final AwtMouseWrapper    mouseWrapper    = new AwtMouseWrapper(canvas);
        final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(canvas);
        final AwtFocusWrapper    focusWrapper    = new AwtFocusWrapper(canvas);
        final AwtMouseManager    mouseManager    = new AwtMouseManager(canvas);

        final PhysicalLayer pl = new PhysicalLayer(keyboardWrapper, mouseWrapper, focusWrapper);

        logicalLayer.registerInput(canvas, pl);

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.H),
            new TriggerAction() {
            @Override
                public void perform(Canvas source, InputState inputState, double tpf) {
                    if (source != canvas) {
                        return;
                    }
                }
            }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.J),
            new TriggerAction() {
            @Override
                public void perform(Canvas source, InputState inputState, double tpf) {
                    if (source != canvas) {
                        return;
                    }

                    mouseManager.setCursor(MouseCursor.SYSTEM_DEFAULT);
                }
            }));

//        refresher.addCanvas(canvas);

        return canvas;
    }

    @Override
    protected RenderingContext getRenderingContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
