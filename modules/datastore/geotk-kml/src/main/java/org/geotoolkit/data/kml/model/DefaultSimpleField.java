package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSimpleField implements SimpleField {

    private String displayName;
    private String type;
    private String name;
    private List<Object> simpleFieldExtensions;

    public DefaultSimpleField(){
        this.simpleFieldExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param displayName
     * @param type
     * @param name
     */
    public DefaultSimpleField(String displayName, String type, String name, List<Object> simpleFieldExtensions){
        this.displayName = displayName;
        this.type = type;
        this.name = name;
        this.simpleFieldExtensions = (simpleFieldExtensions == null) ? EMPTY_LIST : simpleFieldExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDisplayName() {return this.displayName;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getName() {return this.name;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getSimpleFieldExtensions() {
        return this.simpleFieldExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSimpleFieldExtensions(List<Object> simpleFieldExtensions) {
        this.simpleFieldExtensions = simpleFieldExtensions;
    }

}
