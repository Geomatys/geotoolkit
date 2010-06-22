package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultChange implements Change {

    private final List<AbstractObject> objects;

    /**
     *
     * @param objects
     */
    public DefaultChange(List<AbstractObject> objects){
        this.objects = (objects == null) ? EMPTY_LIST : objects;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getObjects() {return this.objects;}

}
