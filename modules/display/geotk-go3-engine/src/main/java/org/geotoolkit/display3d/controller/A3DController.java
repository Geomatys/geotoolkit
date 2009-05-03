
package org.geotoolkit.display3d.controller;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.awt.AwtCanvas;
import com.ardor3d.input.ButtonState;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyHeldCondition;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.util.Constants;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.stat.StatCollector;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class A3DController implements Updater,CanvasController3D {

    private static final double TURN_SPEED = 0.2;
    private static final double MOUSE_TURN_SPEED = 0.2;

    private final A3DCanvas canvas;
    private final LogicalLayer logicalLayer;

    //variable to update camera on next canvas update
    private final Vector3 updateLocation = new Vector3(0, 0, 0);
    private final Matrix3 _incr = new Matrix3();
    private double moveSpeed = 4;


    @Inject
    public A3DController(final A3DCanvas canvas, final LogicalLayer logicalLayer) {
        this.canvas = canvas;
        this.logicalLayer = logicalLayer;        
    }

    @Override
    public void setCameraPosition(double x, double y, double z) {
        synchronized(updateLocation){
            updateLocation.setX(x);
            updateLocation.setY(y);
            updateLocation.setZ(z);
        }
    }

    @Override
    public void setCameraSpeed(double speed) {
        moveSpeed = speed;
    }

    private Camera getCamera(){
        return canvas.getNativeCanvas().getCanvasRenderer().getCamera();
    }

    @MainThread
    @Override
    public void init() {
        registerInputTriggers();
    }

    private void registerInputTriggers() {
        logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.Z), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                moveForward(source, tpf);
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.S), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                moveBack(source, tpf);
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.E), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                turnLeft(source, tpf);
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.A), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                turnRight(source, tpf);
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.Q), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                moveLeft(source, tpf);
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.D), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                moveRight(source, tpf);
            }
        }));

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ZERO), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                resetCamera(source);
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.NINE), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                lookAtZero(source);
            }
        }));

        final Predicate<TwoInputStates> mouseMovedAndOneButtonPressed = Predicates.and(TriggerConditions.mouseMoved(),
                Predicates.or(TriggerConditions.leftButtonDown(), TriggerConditions.rightButtonDown()));

        logicalLayer.registerTrigger(new InputTrigger(mouseMovedAndOneButtonPressed, new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                final MouseState mouseState = inputState.getMouseState();

                turn(source, mouseState.getDx() * tpf * -MOUSE_TURN_SPEED);
                rotateUpDown(source, mouseState.getDy() * tpf * -MOUSE_TURN_SPEED);
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new MouseButtonCondition(ButtonState.DOWN, ButtonState.DOWN,
                ButtonState.UNDEFINED), new TriggerAction() {
            public void perform(final Canvas source, final InputState inputState, final double tpf) {
                moveForward(source, tpf);
            }
        }));

    }

    private void lookAtZero(final Canvas canvas) {
        if(canvas == null) return;

        canvas.getCanvasRenderer().getCamera().lookAt(Vector3.ZERO, Vector3.UNIT_Y);
    }

    private void resetCamera(final Canvas canvas) {
        if(canvas == null) return;

        final Vector3 loc = new Vector3(0.0f, 0.0f, 10.0f);
        final Vector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
        final Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
        final Vector3 dir = new Vector3(0.0f, 0f, -1.0f);

        canvas.getCanvasRenderer().getCamera().setFrame(loc, left, up, dir);
    }

    Camera lastCamera = null;

    @MainThread
    @Override
    public void update(final ReadOnlyTimer timer) {
        final double tpf = timer.getTimePerFrame();

        /** update stats, if enabled. */
        if (Constants.stats) {
            StatCollector.update();
        }

        // check and execute any input triggers, if we are concerned with input
        logicalLayer.checkTriggers(tpf);

        // Execute updateQueue item
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE).execute();

        Camera camera = getCamera();
        synchronized (updateLocation) {

            if(camera != null && camera != lastCamera){
                lastCamera = camera;
                camera.setFrustumPerspective(
                45.0,
                (float) camera.getWidth()
                        / (float) camera.getHeight(), 1, 10000.0f);
                    camera.lookAt(new Vector3(0, 0, 0), Vector3.UNIT_Y);
            }


            if ((updateLocation.getX() != 0 ||
                    updateLocation.getY() != 0 ||
                    updateLocation.getZ() != 0) &&
                    camera != null) {
                camera.setLocation(updateLocation);
                updateLocation.setX(0);
                updateLocation.setY(0);
                updateLocation.setZ(0);
            }
        }

        if(camera != null){
            canvas.getContainer2().update(camera,tpf,true);
        }


        /** Update controllers/render states/transforms/bounds for rootNode. */
        canvas.getContainer2().getRoot().updateGeometricState(timer.getTimePerFrame(), true);
    }

    private void rotateUpDown(final Canvas canvas, final double speed) {
        if(canvas == null) return;

        final Camera camera = canvas.getCanvasRenderer().getCamera();

        final Vector3 temp = Vector3.fetchTempInstance();
        _incr.fromAngleNormalAxis(speed, camera.getLeft());

        _incr.applyPost(camera.getLeft(), temp);
        camera.setLeft(temp);

        _incr.applyPost(camera.getDirection(), temp);
        camera.setDirection(temp);

        _incr.applyPost(camera.getUp(), temp);
        camera.setUp(temp);

        Vector3.releaseTempInstance(temp);

        camera.normalize();

    }

    private void turnRight(final Canvas canvas, final double tpf) {
        if(canvas == null) return;

        turn(canvas, -TURN_SPEED * tpf);
    }

    private void turn(final Canvas canvas, final double speed) {
        if(canvas == null) return;

        final Camera camera = canvas.getCanvasRenderer().getCamera();

        final Vector3 temp = Vector3.fetchTempInstance();

        //turning is autorise only along the Y axis
        //this avoid all rolling effect of the camera
        Vector3 upAxi = new Vector3(camera.getUp());
        upAxi.setX(0);
        upAxi.setZ(0);

        _incr.fromAngleNormalAxis(speed, upAxi);

        _incr.applyPost(camera.getLeft(), temp);
        camera.setLeft(temp);

        _incr.applyPost(camera.getDirection(), temp);
        camera.setDirection(temp);

        _incr.applyPost(camera.getUp(), temp);
        camera.setUp(temp);        
        Vector3.releaseTempInstance(temp);

        camera.normalize();
    }

    private void turnLeft(final Canvas canvas, final double tpf) {
        if(canvas == null) return;

        turn(canvas, TURN_SPEED * tpf);
    }

    private void moveForward(final Canvas canvas, final double tpf) {
        if(canvas == null) return;

        final Camera camera = canvas.getCanvasRenderer().getCamera();
        final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
        final Vector3 dir = Vector3.fetchTempInstance();
        if (!camera.isParallelProjection()) {
            dir.set(camera.getDirection());
        } else {
            // move up if in parallel mode
            dir.set(camera.getUp());
        }
        dir.multiplyLocal(moveSpeed * tpf);
        loc.addLocal(dir);
        checkLocation(loc);
        camera.setLocation(loc);
        Vector3.releaseTempInstance(loc);
        Vector3.releaseTempInstance(dir);
    }

    private void moveLeft(final Canvas canvas, final double tpf) {
        if(canvas == null) return;

        final Camera camera = canvas.getCanvasRenderer().getCamera();
        final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
        final Vector3 dir = Vector3.fetchTempInstance();

        dir.set(camera.getLeft());

        dir.multiplyLocal(moveSpeed * tpf);
        loc.addLocal(dir);
        checkLocation(loc);
        camera.setLocation(loc);
        Vector3.releaseTempInstance(loc);
        Vector3.releaseTempInstance(dir);
    }

    private void moveRight(final Canvas canvas, final double tpf) {
        if(canvas == null) return;

        final Camera camera = canvas.getCanvasRenderer().getCamera();
        final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
        final Vector3 dir = Vector3.fetchTempInstance();

        dir.set(camera.getLeft());

        dir.multiplyLocal(-moveSpeed * tpf);
        loc.addLocal(dir);
        checkLocation(loc);
        camera.setLocation(loc);
        Vector3.releaseTempInstance(loc);
        Vector3.releaseTempInstance(dir);
    }

    private void moveBack(final Canvas canvas, final double tpf) {
        if(canvas == null) return;

        final Camera camera = canvas.getCanvasRenderer().getCamera();
        final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
        final Vector3 dir = Vector3.fetchTempInstance();
        if (!camera.isParallelProjection()) {
            dir.set(camera.getDirection());
        } else {
            // move up if in parallel mode
            dir.set(camera.getUp());
        }
        dir.multiplyLocal(-moveSpeed * tpf);
        loc.addLocal(dir);
        checkLocation(loc);
        camera.setLocation(loc);
        Vector3.releaseTempInstance(loc);
        Vector3.releaseTempInstance(dir);
    }

    /**
     * Ensure that the camera stay in the correct area
     */
    private void checkLocation(Vector3 loc){
        //we can not go under 0Z
        if(loc.getY() < 0.1){
            loc.setY(0.1);
        }
    }

    @Override
    public void setTitle(InternationalString arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCenter(DirectPosition arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setObjectiveCRS(CoordinateReferenceSystem arg0) throws TransformException {
        canvas.setObjectiveCRS(arg0);
    }

    @Override
    public CoordinateReferenceSystem getObjectiveCRS() {
        return canvas.getObjectiveCRS();
    }

}
