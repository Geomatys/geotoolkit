package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultStyle extends DefaultAbstractStyleSelector implements Style {

    private IconStyle iconStyle;
    private LabelStyle labelStyle;
    private LineStyle lineStyle;
    private PolyStyle polyStyle;
    private BalloonStyle balloonStyle;
    private ListStyle listStyle;
    private List<SimpleType> styleSimpleExtensions;
    private List<AbstractObject> styleObjectExtensions;

    /**
     * 
     */
    public DefaultStyle(){
        this.styleSimpleExtensions = EMPTY_LIST;
        this.styleObjectExtensions = EMPTY_LIST;
    }
    
    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     * @param iconStyle
     * @param labelStyle
     * @param lineStyle
     * @param polyStyle
     * @param balloonStyle
     * @param listStyle
     * @param styleSimpleExtensions
     * @param styleObjectExtensions
     */
    public DefaultStyle(List<SimpleType> objectSimpleExtensions,
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
        this.styleSimpleExtensions = (styleSimpleExtensions == null) ? EMPTY_LIST : styleSimpleExtensions;
        this.styleObjectExtensions = (styleObjectExtensions == null) ? EMPTY_LIST : styleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IconStyle getIconStyle() {return this.iconStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LabelStyle getLabelStyle() {return this.labelStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LineStyle getLineStyle() {return this.lineStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PolyStyle getPolyStyle() {return this.polyStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public BalloonStyle getBalloonStyle() {return this.balloonStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ListStyle getListStyle() {return this.listStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getStyleSimpleExtensions() {return this.styleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getStyleObjectExtensions() {return this.styleObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIconStyle(IconStyle iconStyle) {this.iconStyle = iconStyle;};

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLabelStyle(LabelStyle labelStyle) {this.labelStyle = labelStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLineStyle(LineStyle lineStyle) {this.lineStyle = lineStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPolyStyle(PolyStyle polyStyle) {this.polyStyle = polyStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBalloonStyle(BalloonStyle baloonStyle) {this.balloonStyle = baloonStyle;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setListStyle(ListStyle listStyle) {this.listStyle = listStyle;}

    @
    /**
     *
     * @{@inheritDoc }
     */Override
    public void setStyleSimpleExtensions(List<SimpleType> styleSimpleExtensions) {
        this.styleSimpleExtensions = styleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleObjectExtensions(List<AbstractObject> styleObjectExtensions) {
        this.styleObjectExtensions = styleObjectExtensions;
    }
    
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
