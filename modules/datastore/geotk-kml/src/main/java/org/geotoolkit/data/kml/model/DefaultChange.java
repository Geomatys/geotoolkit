package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultChange implements Change {

    private List<AbstractObject> objects;

    public DefaultChange() {
        this.objects = EMPTY_LIST;
    }

    /**
     *
     * @param objects
     */
    public DefaultChange(List<AbstractObject> objects) {
        this.objects = (objects == null) ? EMPTY_LIST : objects;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getObjects() {
        return this.objects;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setObjects(List<AbstractObject> objects) {
        this.objects = objects;
    }
}
