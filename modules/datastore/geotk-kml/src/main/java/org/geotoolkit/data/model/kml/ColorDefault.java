package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class ColorDefault implements Color {

    private String color;

    public static final String BG_COLOR = "ffffffff";
    public static final String TEXT_COLOR = "ff000000";

    public ColorDefault(String color) throws KmlException{
        if(color.matches("[0-9a-fA-F]{8}")){
            this.color = color;
        } else {
            throw new KmlException("The color must be a suit of four hexabinaries");
        }
    }

    @Override
    public String getColor() {return this.color;}

    @Override
    public String toString() {
        return this.color;
    }

}
