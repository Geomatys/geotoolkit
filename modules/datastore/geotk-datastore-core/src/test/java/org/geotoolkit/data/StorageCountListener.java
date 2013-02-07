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
package org.geotoolkit.data;

/**
 * Test storage listener, count the number of events and store the last event objects.
 * @author Johann Sorel (Puzzle-GIS)
 */
public final class StorageCountListener implements FeatureStoreListener {

    public int numManageEvent = 0;
    public int numContentEvent = 0;
    public FeatureStoreManagementEvent lastManagementEvent = null;
    public FeatureStoreContentEvent lastContentEvent = null;

    @Override
    public void structureChanged(final FeatureStoreManagementEvent event) {
        numManageEvent++;
        this.lastManagementEvent = event;
    }

    @Override
    public void contentChanged(final FeatureStoreContentEvent event) {
        numContentEvent++;
        this.lastContentEvent = event;
    }
}
