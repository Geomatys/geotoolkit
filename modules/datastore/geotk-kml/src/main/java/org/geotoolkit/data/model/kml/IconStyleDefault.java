package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class IconStyleDefault extends AbstractColorStyleDefault implements IconStyle {

    private final double scale;
    private final Angle360 heading;
    private final BasicLink icon;
    private final Vec2 hotSpot;
    private final List<SimpleType> iconStyleSimpleExtensions;
    private final List<AbstractObject> iconStyleObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param color
     * @param colorMode
     * @param colorStyleSimpleExtensions
     * @param colorStyleObjectExtensions
     * @param scale
     * @param heading
     * @param icon
     * @param hotSpot
     * @param iconStyleSimpleExtensions
     * @param iconStyleObjectExtensions
     */
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
        this.iconStyleSimpleExtensions = (iconStyleSimpleExtensions == null) ? EMPTY_LIST : iconStyleSimpleExtensions;
        this.iconStyleObjectExtensions = (iconStyleObjectExtensions == null) ? EMPTY_LIST : iconStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getScale() {return this.scale;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Angle360 getHeading() {return this.heading;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BasicLink getIcon() {return this.icon;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Vec2 getHotSpot() {return this.hotSpot;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getIconStyleSimpleExtensions() {return this.iconStyleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
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
