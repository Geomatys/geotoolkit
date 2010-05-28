package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class SimpleFieldDefault implements SimpleField {

    private String displayName;
    private String type;
    private String name;

    /**
     *
     * @param displayName
     * @param type
     * @param name
     */
    public SimpleFieldDefault(String displayName, String type, String name){
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
