package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class CreateDefault implements Create {

    private List<AbstractContainer> containers;

    public CreateDefault(List<AbstractContainer> containers){
        this.containers = containers;
    }

    @Override
    public List<AbstractContainer> getContainers() {return this.containers;}

}
