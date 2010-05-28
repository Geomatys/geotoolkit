package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class CreateDefault implements Create {

    private List<AbstractContainer> containers;

    /**
     *
     * @param containers
     */
    public CreateDefault(List<AbstractContainer> containers){
        this.containers = containers;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractContainer> getContainers() {return this.containers;}

}
