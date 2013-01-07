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
