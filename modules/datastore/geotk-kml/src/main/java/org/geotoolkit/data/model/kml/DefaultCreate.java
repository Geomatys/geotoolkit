package org.geotoolkit.data.model.kml;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultCreate implements Create {

    private final List<AbstractContainer> containers;

    /**
     *
     * @param containers
     */
    public DefaultCreate(List<AbstractContainer> containers){
        this.containers = (containers == null) ? EMPTY_LIST : containers;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractContainer> getContainers() {return this.containers;}

}
