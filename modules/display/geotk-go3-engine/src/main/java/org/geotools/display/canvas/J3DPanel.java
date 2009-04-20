/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.display.canvas;

import com.sun.opengl.util.Animator;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import org.geotools.display.container.ContextContainer3D;
import org.geotools.display.geom.FeatureGraphicJ3D;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

/**
 *
 * @author axel
 */
public class J3DPanel extends GLJPanel {

    private final Animator animator = new Animator(this);
    private final DefaultJ3DCanvas renderer = new DefaultJ3DCanvas(DefaultGeographicCRS.WGS84, null);
    private final ContextContainer3D container3d = new ContextContainer3D(renderer);


    public J3DPanel() {
        super(createGLCapabilites());
        addGLEventListener(renderer);
        renderer.setContainer(container3d);
        animator.start();
    }

    

    /**
     * Called from within initComponents().
     * hint: to customize the generated code choose 'Customize Code' in the contextmenu
     * of the selected UI Component you wish to cutomize in design mode.
     * @return Returns customized GLCapabilities.
     */
    private static GLCapabilities createGLCapabilites() {

        GLCapabilities capabilities = new GLCapabilities();
        capabilities.setHardwareAccelerated(true);

        // try to enable 2x anti aliasing - should be supported on most hardware
        capabilities.setNumSamples(2);
        capabilities.setSampleBuffers(true);

        return capabilities;
    }

    @Override
    public void setVisible(boolean show) {
        if (!show) {
            animator.stop();
        }
        super.setVisible(show);
        if (!show) {
            animator.start();
        }
    }
    /**
     * @return the renderer
     */
    public DefaultJ3DCanvas getRenderer() {
        return renderer;
    }

    /**
     * @return the container3d
     */
    public ContextContainer3D getContainer3d() {
        return container3d;
    }
}
