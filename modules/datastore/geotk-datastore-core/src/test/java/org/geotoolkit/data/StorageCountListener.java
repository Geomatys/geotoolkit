package org.geotoolkit.data;

/**
 * Test storage listener, count the number of events and store the last event objects.
 * @author Johann Sorel (Puzzle-GIS)
 */
public final class StorageCountListener implements StorageListener {

    public int numManageEvent = 0;
    public int numContentEvent = 0;
    public StorageManagementEvent lastManagementEvent = null;
    public StorageContentEvent lastContentEvent = null;

    @Override
    public void structureChanged(StorageManagementEvent event) {
        numManageEvent++;
        this.lastManagementEvent = event;
    }

    @Override
    public void contentChanged(StorageContentEvent event) {
        numContentEvent++;
        this.lastContentEvent = event;
    }
}
