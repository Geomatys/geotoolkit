package org.geotoolkit.data.model.kml;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class ChangeDefault implements Change {

    private final List<AbstractObject> objects;

    /**
     *
     * @param objects
     */
    public ChangeDefault(List<AbstractObject> objects){
        this.objects = (objects == null) ? EMPTY_LIST : objects;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getObjects() {return this.objects;}

}
