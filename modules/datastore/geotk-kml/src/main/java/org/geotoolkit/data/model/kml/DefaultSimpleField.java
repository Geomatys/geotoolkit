package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSimpleField implements SimpleField {

    private final String displayName;
    private final String type;
    private final String name;

    /**
     *
     * @param displayName
     * @param type
     * @param name
     */
    public DefaultSimpleField(String displayName, String type, String name){
        this.displayName = displayName;
        this.type = type;
        this.name = name;
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

}
