/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011, Geomatys
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
package org.geotoolkit.gui.swing.go2.control.edition;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

import org.geotoolkit.data.StorageContentEvent;
import org.geotoolkit.data.StorageListener;
import org.geotoolkit.data.StorageManagementEvent;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public class SessionCommitAction extends AbstractAction implements StorageListener {

    private static final Logger LOGGER = Logging.getLogger(SessionCommitAction.class);

    private final StorageListener.Weak weakListener = new Weak(this);
    private FeatureMapLayer layer;

    public SessionCommitAction() {
        this(null);
    }

    public SessionCommitAction(final FeatureMapLayer layer) {
        putValue(SMALL_ICON, IconBundle.getIcon("16_session_commit"));
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
            try {
                layer.getCollection().getSession().commit();
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }
    }

    @Override
    public void structureChanged(final StorageManagementEvent event) {
    }

    @Override
    public void contentChanged(final StorageContentEvent event) {
        if(event.getType() == StorageContentEvent.Type.SESSION){
            //refresh enable state
            setLayer(layer);
        }
    }

}
