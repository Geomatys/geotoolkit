package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class SimpleFieldDefault implements SimpleField {

    private String displayName;
    private String type;
    private String name;

    public SimpleFieldDefault(String displayName, String type, String name){
        this.displayName = displayName;
        this.type = type;
        this.name = name;
    }

    @Override
    public String getDisplayName() {return this.displayName;}

    @Override
    public String getType() {return this.type;}

    @Override
    public String getName() {return this.name;}

}
