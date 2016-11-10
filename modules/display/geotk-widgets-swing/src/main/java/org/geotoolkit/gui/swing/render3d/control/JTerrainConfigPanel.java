/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.render3d.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javax.measure.IncommensurableException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.display3d.scene.Terrain;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;
import org.geotoolkit.display3d.scene.loader.DefaultElevationLoader;
import org.geotoolkit.display3d.scene.loader.ElevationLoader;
import org.geotoolkit.display3d.scene.loader.FlatElevationLoader;
import org.geotoolkit.display3d.scene.loader.ImageLoader;
import org.geotoolkit.display3d.scene.loader.MapContextImageLoader;
import org.geotoolkit.display3d.scene.loader.PyramidImageLoader;
import org.geotoolkit.display3d.scene.loader.StackElevationLoader;
import org.geotoolkit.gui.swing.render3d.JMap3D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JTerrainConfigPanel extends javax.swing.JPanel {

    private final JMap3D map;
    private final MapContext context;

    /**
     * Creates new form JTerrainConfigPanel
     * @param map3d
     */
    public JTerrainConfigPanel(final JMap3D map3d) {
        initComponents();
        this.map = map3d;
        this.context = ((ContextContainer3D)map3d.getMap3D().getContainer()).getContext();

        guiApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                resetScene(
                        guiLayerList.getSelectedValuesList(),
                        guiMntList.getSelectedValuesList());
                resetCamera();
            }
        });


        updateLayersBox();
        //listen to any change in the map context to update combo boxes
        context.addContextListener(new ContextListener() {
            @Override
            public void layerChange(CollectionChangeEvent<MapLayer> event) {
                updateLayersBox();
            }
            @Override
            public void itemChange(CollectionChangeEvent<MapItem> event) {}
            @Override
            public void propertyChange(PropertyChangeEvent evt) {}
        });

        //listen to selection change on combo box to validate/unvalidate apply button
        final ListSelectionListener listener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                guiApply.setEnabled(guiLayerList.getSelectedValue()!= null && guiMntList.getSelectedValue() != null);
            }
        };
        guiLayerList.addListSelectionListener(listener);
        guiMntList.addListSelectionListener(listener);
        guiLayerList.setCellRenderer(new CoverageRefRenderer());
        guiMntList.setCellRenderer(new CoverageRefRenderer());

        //extract selected values from current map
//        final ContextContainer3D container = (ContextContainer3D) map.getMap3D().getContainer();
//        final Terrain terrain = container.getTerrain();
//        if(terrain != null){
//            final ImageLoader imgLoader = terrain.getImageLoader();
//            final ElevationLoader eleLoader = terrain.getElevationLoader();
//
//            if(imgLoader instanceof PyramidImageLoader){
//                final PyramidalCoverageReference ref = ((PyramidImageLoader)imgLoader).getCoverageReference();
//                guiLayerList.setSelectedValue(ref, true);
//            }else if(imgLoader instanceof MapContextImageLoader){
//                final List<MapLayer> layers = ((MapContextImageLoader)imgLoader).getContext().layers();
//            }
//
//        }

    }

    private void updateLayersBox(){
        final List<MapItem> lst = new ArrayList<>(context.items());
        Collections.reverse(lst);

        final List<MapItem> validImageLayers = new ArrayList<>();
        final List<CoverageReference> validMNTLayers = new ArrayList<>();
        for(MapItem l : lst){
            validImageLayers.add(l);
            if(l instanceof CoverageMapLayer){
                final CoverageReference ref = ((CoverageMapLayer)l).getCoverageReference();
                validMNTLayers.add(ref);
            }
        }

        guiLayerList.setModel(new ListComboBoxModel(validImageLayers));
        guiLayerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if(validImageLayers.size()==1){
            guiLayerList.setSelectedIndex(0);
        }

        guiMntList.setModel(new ListComboBoxModel(validMNTLayers));
        guiMntList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if(validMNTLayers.size()==1){
            guiMntList.setSelectedIndex(0);
        }

        guiApply.setEnabled(guiLayerList.getSelectedValue()!= null && guiMntList.getSelectedValue() != null);

    }

    private void resetScene(final List<MapItem> contextLayers, final List<CoverageReference> mntList){
        try{

            GeneralEnvelope envelope = null;
            int numMosaic = 1;

            final ElevationLoader elevationLoader;
            if(mntList.size()==1){
                //single elevation
                elevationLoader = new DefaultElevationLoader(mntList.get(0));
            }else if( mntList.size()>1){
                //multiple elevation
                final List<ElevationLoader> mnts = new ArrayList<>();
                for(CoverageReference ref : mntList){
                    mnts.add(new DefaultElevationLoader(ref));
                }
                elevationLoader = new StackElevationLoader(mnts);
            }else{
                //no elevation
                elevationLoader = new FlatElevationLoader(0);
            }


            //prepare the terrain image loader
            ImageLoader imageLoader = null;
            imagepyramid:
            if(contextLayers.size() == 1){
                final MapItem layer = contextLayers.get(0);
                if(layer instanceof CoverageMapLayer){
                    //we can use the coverage reference directly
                    final CoverageReference modelImg = ((CoverageMapLayer)layer).getCoverageReference();
                    if(modelImg instanceof PyramidalCoverageReference){
                        final PyramidalCoverageReference pcf = (PyramidalCoverageReference) modelImg;
                        final List<Pyramid> pyramidsImg = (List<Pyramid>) pcf.getPyramidSet().getPyramids();
                        Pyramid pyramidImg = null;
                        if (pyramidsImg.size() > 0){
                            pyramidImg = pyramidsImg.get(0);
                        }
                        imageLoader = new PyramidImageLoader(pcf, pyramidImg);
                    }
                }
            }

            if(imageLoader==null){
                //use a map context image loader
                final MapContext context = MapBuilder.createContext();
                context.items().addAll(contextLayers);
                Collections.reverse(context.items());
                imageLoader = new MapContextImageLoader(context);
            }

            if(envelope == null) {
                GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
                env.setRange(0, -180.0, 180.0);
                env.setRange(1, -90.0, 90.0);

                envelope = env;
                numMosaic = 21;
            }

            final Terrain terrain = ((ContextContainer3D)map.getMap3D().getContainer()).createTerrain(envelope, numMosaic);
            terrain.setElevationLoader(elevationLoader);
            terrain.setImageLoader(imageLoader);

        } catch (PortrayalException | TransformException | FactoryException | DataStoreException | IncommensurableException ex) {
            Map3D.LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    private void resetCamera(){
        final Map3D map3d = map.getMap3D();
        final Terrain terrain = ((ContextContainer3D)map3d.getContainer()).getTerrain();

        if (terrain != null){
            final TrackBallCamera camera = map3d.getCamera();
            final float x = (float) terrain.getEnvelope().getMedian(0);
            final float y = (float) terrain.getEnvelope().getMedian(1);
            final double maxScale = terrain.getEnvelope().getSpan(0) / camera.getWidth();
            final float maxLength = (float)map3d.getDistForScale(maxScale);

            camera.setCenter(x, y, 0.0f);
            camera.zoomTo(maxLength);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane();
        guiMntList = new JList();
        jPanel2 = new JPanel();
        jLabel2 = new JLabel();
        jScrollPane2 = new JScrollPane();
        guiLayerList = new JList();
        guiApply = new JButton();

        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText(MessageBundle.format("org_geotoolkit_gui_swing_render3d_mnt")); // NOI18N

        jScrollPane1.setViewportView(guiMntList);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText(MessageBundle.format("org_geotoolkit_gui_swing_render3d_layer")); // NOI18N

        jScrollPane2.setViewportView(guiLayerList);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        guiApply.setText(MessageBundle.format("apply")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiApply)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(guiApply)
                .addGap(33, 33, 33))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton guiApply;
    private JList guiLayerList;
    private JList guiMntList;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    private static class CoverageRefRenderer extends DefaultListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if(value instanceof CoverageReference){
                final CoverageReference ref = (CoverageReference) value;
                lbl.setText(ref.getName().tip().toString());
            }else if(value instanceof MapItem){
                final MapItem mapitem = (MapItem) value;
                String txt = mapitem.getName();
                if( (txt==null || txt.isEmpty()) && mapitem.getDescription() != null){
                    txt = String.valueOf(mapitem.getDescription().getTitle());
                }
                if( (txt==null || txt.isEmpty())){
                    txt = "";
                }
                lbl.setText(txt);
            }

            return lbl;
        }

    }


}
