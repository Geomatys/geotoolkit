package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class ChangeDefault implements Change {

    private List<AbstractObject> objects;

    /**
     *
     * @param objects
     */
    public ChangeDefault(List<AbstractObject> objects){
        this.objects = objects;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getObjects() {return this.objects;}

}
