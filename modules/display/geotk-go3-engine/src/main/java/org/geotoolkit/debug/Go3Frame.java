/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *    (C) 2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.debug;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.extension.effect.particle.ParticleFactory;
import com.ardor3d.extension.effect.particle.ParticleSystem;
import com.ardor3d.extension.model.collada.ColladaImporter;
import com.ardor3d.extension.model.collada.binding.core.Collada;
import com.ardor3d.image.Image.Format;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.ComplexSpatialController;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.TextureManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import org.apache.derby.impl.store.raw.log.ReadOnly;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.gui.swing.go3.control.JNavigationBar;
import org.geotoolkit.gui.swing.maptree.JContextTree;
import org.geotoolkit.map.MapContext;


/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class Go3Frame extends JFrame{

    private final A3DCanvas gui3DPane;
    private final JContextTree guiTree = new JContextTree();
    private final JNavigationBar guiNavBar = new JNavigationBar();

    public Go3Frame() throws Exception {
        final MapContext context = ContextBuilder.buildRealCityContext();
        gui3DPane = new A3DCanvas(context.getCoordinateReferenceSystem(), null);
        gui3DPane.getContainer2().setContext(context,true);
        guiTree.setContext(context);
        guiNavBar.setFloatable(false);
        guiNavBar.setMap(gui3DPane);

        final JSplitPane splitTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        final JPanel pan3D = new JPanel(new BorderLayout());
        pan3D.add(BorderLayout.NORTH,guiNavBar);
        pan3D.add(BorderLayout.CENTER,gui3DPane.getComponent());
        splitTree.setLeftComponent(guiTree);
        splitTree.setRightComponent(pan3D);

        final JMenuBar bar = new JMenuBar();
        final JMenu menu = new JMenu("File");
        final JMenuItem item = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        bar.add(menu);
        menu.add(item);

        gui3DPane.getController().setCameraSpeed(100);


//        gui3DPane.getContainer2().getRoot()
//        final ParticleSystem reactor = createReactor();
//        reactor.setTranslation(0, 0, 0);
        gui3DPane.getContainer2().getRoot().updateWorldTransform(true);
//        gui3DPane.getContainer2().getRoot().attachChild(reactor);

        gui3DPane.getContainer2().getScene().attachChild(createDynamicNode());


        setJMenuBar(bar);
        setContentPane(splitTree);
        setSize(1280,1024);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

    }

    public Node createDynamicNode() throws MalformedURLException{
        final Node group = new Node("planes");
        group.setTranslation(0, 200, 0);

        final Node plane1 = ColladaImporter.readColladaScene(new URL("file:///home/eclesia/Bureau/Mirage/models/Mirage.dae"));
        plane1.setRotation(new Matrix3().fromAngleNormalAxis(Math.PI * -0.5, new Vector3(1, 0, 0)));
        plane1.setScale(0.2,0.2,0.2);

        final Node plane2 = ColladaImporter.readColladaScene(new URL("file:///home/eclesia/Bureau/Mirage/models/Mirage.dae"));
        plane2.setRotation(new Matrix3().fromAngleNormalAxis(Math.PI * -0.5, new Vector3(1, 0, 0)));
        plane2.setScale(0.2,0.2,0.2);

        final Node plane3 = ColladaImporter.readColladaScene(new URL("file:///home/eclesia/Bureau/Mirage/models/Mirage.dae"));
        plane3.setRotation(new Matrix3().fromAngleNormalAxis(Math.PI * -0.5, new Vector3(1, 0, 0)));
        plane3.setScale(0.2,0.2,0.2);


        plane2.setTranslation(200, 0, 500);
        plane3.setTranslation(-200, 0, 500);


        final ParticleSystem reactor1 = createReactor();
        Node nr1 = new Node();
        nr1.setTranslation(110, 300, 400);
        nr1.attachChild(reactor1);
        nr1.updateWorldTransform(true);
        reactor1.warmUp(60);
        group.attachChild(nr1);

        final ParticleSystem reactor2 = createReactor();
        Node nr2 = new Node();
        nr2.setTranslation(310, 300, 900);
        nr2.attachChild(reactor2);
        nr2.updateWorldTransform(true);
        reactor2.warmUp(60);
        group.attachChild(nr2);

        final ParticleSystem reactor3 = createReactor();
        Node nr3 = new Node();
        nr3.setTranslation(-90, 300, 900);
        nr3.attachChild(reactor3);
        nr3.updateWorldTransform(true);
        reactor3.warmUp(60);
        group.attachChild(nr3);

        group.attachChild(plane1);
        group.attachChild(plane2);
        group.attachChild(plane3);


        group.addController(new ComplexSpatialController<Node>(){

            double rayon = 3000;
            double step = Math.PI;
            double angle = 0;

            @Override
            public void update(double time, Node caller) {
                angle += step*time/10f;
                if(angle >= Math.PI*2){
                    angle -= Math.PI*2;
                }
//                reactor.forceRespawn();
                Matrix3 rt = new Matrix3().fromAngleNormalAxis(Math.PI-angle, new Vector3(0, 1, 0));
                caller.setRotation(rt);
                caller.setTranslation(Math.cos(angle)*rayon, 500, Math.sin(angle)*rayon);
//                caller.updateWorldTransform(true);
            }
        });

        return group;
    }

    public ParticleSystem createReactor() throws MalformedURLException{
        ParticleSystem particles = ParticleFactory.buildParticles("particles", 300);
        particles.setEmissionDirection(new Vector3(0, 0, -1));
        particles.setInitialVelocity(-0.20);
        particles.setStartSize(15);
        particles.setEndSize(8);
        particles.setMinimumLifeTime(3200);
        particles.setMaximumLifeTime(10000);
        particles.setStartColor(new ColorRGBA(0.9f, 0.6f, 0.6f, 1.0f));
        particles.setEndColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 0.3f));
        particles.setMaximumAngle(1*MathUtils.DEG_TO_RAD);
        particles.setControlFlow(true);
        particles.setParticlesInWorldCoords(false);
        particles.setReleaseRate(60);
        particles.setCameraFacing(true);
//        particles.acceptVisitorarmUp(50);
//        particles.setZBufferState(true, false);
//        particles.setPlayTime( 7.220 );

        final BlendState blend = new BlendState();
        blend.setBlendEnabled(true);
        blend.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        blend.setDestinationFunction(BlendState.DestinationFunction.One);
        particles.setRenderState(blend);

        final TextureState ts = new TextureState();
        ts.setTexture(TextureManager.load(
                new URL("file:///home/eclesia/dev/ardor/trunk/ardor3d-examples/src/main/resources/com/ardor3d/example/media/images/flaresmall.jpg"),
                Texture.MinificationFilter.Trilinear, Format.Guess,
                true));
        ts.getTexture().setWrap(WrapMode.BorderClamp);
        ts.setEnabled(true);
        particles.setRenderState(ts);

        final ZBufferState zstate = new ZBufferState();
        zstate.setWritable(false);
        particles.setRenderState(zstate);

        
        particles.getParticleGeometry().setModelBound(new BoundingBox());


        return particles;
    }


    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        new Go3Frame();
    }
}
