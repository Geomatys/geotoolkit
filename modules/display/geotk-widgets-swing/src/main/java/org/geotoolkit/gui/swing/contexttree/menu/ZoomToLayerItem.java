/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.contexttree.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.geotoolkit.gui.swing.contexttree.AbstractTreePopupItem;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author eclesia
 */
public class ZoomToLayerItem extends AbstractTreePopupItem{

    private WeakReference<MapLayer> layerRef;

    /**
     * Creates a new instance of ZoomToLayerItem
     */
    public ZoomToLayerItem() {
        super(MessageBundle.getString("map_zoom_to_layer"));

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JMap2D map = getMapView();
                final MapLayer layer = layerRef.get();

                if(map == null || layer == null) return;
                try {
                    map.getCanvas().setVisibleArea(layer.getBounds());
                } catch (NoninvertibleTransformException | TransformException ex) {
                    Logging.getLogger("org.geotoolkit.gui.swing.contexttree.menu").log(Level.WARNING, null, ex);
                }
            }
        });
    }

    @Override
    public boolean isValid(final TreePath[] selection) {
        return uniqueAndType(selection,MapLayer.class);
    }

    @Override
    public Component getComponent(final TreePath[] selection) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
        layerRef = new WeakReference<MapLayer>((MapLayer) node.getUserObject());
        this.setEnabled(map != null);
        return this;
    }


}
