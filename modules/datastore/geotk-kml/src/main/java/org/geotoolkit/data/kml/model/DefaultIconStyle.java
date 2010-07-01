package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.KmlUtilities;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultIconStyle extends DefaultAbstractColorStyle implements IconStyle {

    private double scale;
    private double heading;
    private BasicLink icon;
    private Vec2 hotSpot;
    private List<SimpleType> iconStyleSimpleExtensions;
    private List<AbstractObject> iconStyleObjectExtensions;

    public DefaultIconStyle(){
        this.scale = DEF_SCALE;
        this.heading = DEF_HEADING;
        this.iconStyleSimpleExtensions = EMPTY_LIST;
        this.iconStyleObjectExtensions = EMPTY_LIST;
    }

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
    public DefaultIconStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double scale, double heading, BasicLink icon, Vec2 hotSpot,
            List<SimpleType> iconStyleSimpleExtensions, List<AbstractObject> iconStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions);
        this.scale = scale;
        this.heading = KmlUtilities.checkAngle360(heading);
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
    public double getHeading() {return this.heading;}

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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setScale(double scale) {this.scale = scale;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHeading(double heading) {this.heading = KmlUtilities.checkAngle360(heading);}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIcon(BasicLink icon) {this.icon = icon;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHotSpot(Vec2 hotSpot) {this.hotSpot = hotSpot;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIconStyleSimpleExtensions(List<SimpleType> iconStyleSimpleExtensions) {
        this.iconStyleSimpleExtensions = iconStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIconStyleObjectExtensions(List<AbstractObject> iconStyleObjectExtensions) {
        this.iconStyleObjectExtensions = iconStyleObjectExtensions;
    }

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
