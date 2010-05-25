package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class IconStyleDefault extends AbstractColorStyleDefault implements IconStyle {

    private double scale;
    private Angle360 heading;
    private BasicLink icon;
    private Vec2 hotSpot;
    private List<SimpleType> iconStyleSimpleExtensions;
    private List<AbstractObject> iconStyleObjectExtensions;

    public IconStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale, Angle360 heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions);
        this.scale = scale;
        this.heading = heading;
        this.icon = icon;
        this.hotSpot = hotSpot;
        this.iconStyleSimpleExtensions = iconStyleSimpleExtensions;
        this.iconStyleObjectExtensions = iconStyleObjectExtensions;
    }

    @Override
    public double getScale() {return this.scale;}

    @Override
    public Angle360 getHeading() {return this.heading;}

    @Override
    public BasicLink getIcon() {return this.icon;}

    @Override
    public Vec2 getHotSpot() {return this.hotSpot;}

    @Override
    public List<SimpleType> getIconStyleSimpleExtensions() {return this.iconStyleSimpleExtensions;}

    @Override
    public List<AbstractObject> getIconStyleObjectExtensions() {return this.iconStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tIconStyleDefault : "+
                "\n\tscale : "+this.scale+
                "\n\theading : "+this.heading+
                "\n\ticon : "+this.icon+
                "\n\thotSpot : "+this.hotSpot;
        return resultat;
    }

}
