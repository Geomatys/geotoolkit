/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011 - 2014, Geomatys
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

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.FeatureStoreListener;
import org.geotoolkit.data.FeatureStoreManagementEvent;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public class SessionCommitAction extends AbstractAction implements FeatureStoreListener {

    private static final ImageIcon ICON_SAVE = IconBuilder.createIcon(FontAwesomeIcons.ICON_FLOPPY_O, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_WAIT = IconBuilder.createIcon(FontAwesomeIcons.ICON_SPINNER, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private final FeatureStoreListener.Weak weakListener = new Weak(this);
    private FeatureMapLayer layer;

    public SessionCommitAction() {
        this(null);
    }

    public SessionCommitAction(final FeatureMapLayer layer) {
        putValue(SMALL_ICON, ICON_SAVE);
        putValue(NAME, MessageBundle.getString("sessionCommit"));
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("sessionCommit"));
        setLayer(layer);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (layer != null)
                && (layer.getCollection().getSession().hasPendingChanges());
    }

    public FeatureMapLayer getLayer() {
        return layer;
    }

    public void setLayer(final FeatureMapLayer layer) {
        //remove previous listener
        weakListener.unregisterAll();

        final boolean newst = isEnabled();
        this.layer = layer;
        firePropertyChange("enabled", !newst, newst);

        if(this.layer != null){
            weakListener.registerSource(this.layer.getCollection().getSession());
        }
    }

    @Override
    public void actionPerformed(final ActionEvent event) {

        if (layer != null ) {
            putValue(SMALL_ICON, ICON_WAIT);
            final Thread t = new Thread(){
                @Override
                public void run() {
                    try {
                        layer.getCollection().getSession().commit();
                    } catch (DataStoreException ex) {
                        LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
                    }finally{
                        putValue(SMALL_ICON, ICON_SAVE);
                    }
                }
            };
            t.start();
        }
    }

    @Override
    public void structureChanged(final FeatureStoreManagementEvent event) {
    }

    @Override
    public void contentChanged(final FeatureStoreContentEvent event) {
        if(event.getType() == FeatureStoreContentEvent.Type.SESSION){
            //refresh enable state
            setLayer(layer);
        }
    }

}
