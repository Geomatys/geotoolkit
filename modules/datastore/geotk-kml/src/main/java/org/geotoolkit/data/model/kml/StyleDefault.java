package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class StyleDefault extends AbstractStyleSelectorDefault implements Style {

    private IconStyle iconStyle;
    private LabelStyle labelStyle;
    private LineStyle lineStyle;
    private PolyStyle polyStyle;
    private BalloonStyle balloonStyle;
    private ListStyle listStyle;
    private List<SimpleType> styleSimpleExtensions;
    private List<AbstractObject> styleObjectExtensions;

    public StyleDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractStyleSelectorSimpleExtensions,
            List<AbstractObject> abstractStyleSelectorObjectExtensions,
            IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle, PolyStyle polyStyle, BalloonStyle balloonStyle, ListStyle listStyle,
            List<SimpleType> styleSimpleExtensions,
            List<AbstractObject> styleObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
            abstractStyleSelectorSimpleExtensions,
            abstractStyleSelectorObjectExtensions);
        this.iconStyle = iconStyle;
        this.labelStyle = labelStyle;
        this.lineStyle = lineStyle;
        this.polyStyle = polyStyle;
        this.balloonStyle = balloonStyle;
        this.listStyle = listStyle;
        this.styleSimpleExtensions = styleSimpleExtensions;
        this.styleObjectExtensions = styleObjectExtensions;
    }

    @Override
    public IconStyle getIconStyle() {return this.iconStyle;}

    @Override
    public LabelStyle getLabelStyle() {return this.labelStyle;}

    @Override
    public LineStyle getLineStyle() {return this.lineStyle;}

    @Override
    public PolyStyle getPolyStyle() {return this.polyStyle;}

    @Override
    public BalloonStyle getBalloonStyle() {return this.balloonStyle;}

    @Override
    public ListStyle getListStyle() {return this.listStyle;}

    @Override
    public List<SimpleType> getStyleSimpleExtensions() {return this.styleSimpleExtensions;}

    @Override
    public List<AbstractObject> getStyleObjectExtensions() {return this.styleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tStyleDefault : "+
                "\n\ticonStyle : "+this.iconStyle+
                "\n\tlabelStyle : "+this.labelStyle+
                "\n\tlineStyle : "+this.lineStyle+
                "\n\tpolyStyle : "+this.polyStyle+
                "\n\tballoonStyle : "+this.balloonStyle+
                "\n\tlistStyle : "+this.listStyle;
        return resultat;
    }
}
