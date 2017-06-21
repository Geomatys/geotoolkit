/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.render2d.control.edition;


import com.vividsolutions.jts.geom.Geometry;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.logging.Level;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;


/**
 * Feature attribut and geometry tool delegate.
 *
 * @author Johann Sorel
 * @module
 */
public class FeatureEditTDelegate extends AbstractFeatureEditionDelegate {

    private final FeatureMapLayer originalLayer;
    private Feature feature = null;


    public FeatureEditTDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
        this.originalLayer = candidate;
    }

    private void reset(){
        feature = null;
        decoration.setGeometries(null);
    }

    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){
            final Geometry geom = (Geometry) FeatureExt.getDefaultGeometryAttributeValue(feature);
            decoration.setGeometries(Collections.singleton(helper.toObjectiveCRS(geom)));

            final JSplitPane split = new JSplitPane();
            final JFeatureOutLine fe = new JFeatureOutLine();
            fe.setEdited(feature);
            final JPanel editPane = new JPanel(new BorderLayout());
            final JMap2D map = new JMap2D();

            final MapContext context = MapBuilder.createContext();
            final FeatureCollection col = FeatureStoreUtilities.collection(feature);
            final FeatureMapLayer layer = MapBuilder.createFeatureLayer(col, RandomStyleBuilder.createDefaultVectorStyle(col.getType()));
            context.layers().add(layer);

            //zoom on this single feature
            map.setPreferredSize(new Dimension(350, 350));
            map.getContainer().setContext(context);
            try {
                map.getCanvas().setObjectiveCRS(FeatureExt.getCRS(feature.getType()));
                map.getCanvas().setVisibleArea((Envelope)feature.getPropertyValue(AttributeConvention.ENVELOPE_PROPERTY.toString()));
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }

            //activate a node edition tool
            GeometryNodeTool t = new GeometryNodeTool();
            if(t.canHandle(layer)){
                final EditionDelegate delegate = t.createDelegate(map, layer);
                final EditionHandler handler = new EditionHandler(map,delegate);
                map.setHandler(handler);
            }

            editPane.add(BorderLayout.CENTER, map);
            split.setLeftComponent(new JScrollPane(fe));
            split.setRightComponent(editPane);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final String save = MessageBundle.format("save");
                    final String cancel = MessageBundle.format("cancel");
                    final String delete = MessageBundle.format("delete");

                    final Object res = JOptionDialog.show(null,split,new String[]{delete,cancel,save});
                    if(save == res){
                        final Feature geofeature = layer.getCollection().iterator().next();
                        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), geofeature.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString()));

                        try {
                            originalLayer.getCollection().update(feature);
                        } catch (DataStoreException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                        }
                    }else if(delete == res){
                        try {
                            originalLayer.getCollection().remove(FactoryFinder.getFilterFactory(null).id(Collections.singleton(FeatureExt.getId(feature))));
                        } catch (DataStoreException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                        }
                    }
                    reset();
                }
            });

        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            reset();
            setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
        }
    }

}
