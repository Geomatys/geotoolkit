package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class BalloonStyleDefault extends AbstractSubStyleDefault implements BalloonStyle {

    private Color bgColor;
    private Color textColor;
    private String text;
    private DisplayMode displayMode;
    private List<SimpleType> balloonStyleSimpleExtensions;
    private List<AbstractObject> balloonStyleObjectExtensions;

    public BalloonStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color bgColor, Color textColor, String text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions, List<AbstractObject> balloonStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions);
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.text = text;
        this.displayMode = displayMode;
        this.balloonStyleSimpleExtensions = balloonStyleSimpleExtensions;
        this.balloonStyleObjectExtensions = balloonStyleObjectExtensions;
    }

    @Override
    public Color getBgColor() {return this.bgColor;}

    @Override
    public Color getTextColor() {return this.textColor;}

    @Override
    public String getText() {return this.text;}

    @Override
    public DisplayMode getDisplayMode() {return this.displayMode;}

    @Override
    public List<SimpleType> getBalloonStyleSimpleExtensions() {return this.balloonStyleSimpleExtensions;}

    @Override
    public List<AbstractObject> getBalloonStyleObjectExtensions() {return this.balloonStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tBalloonStyleDefault : "+
                "\n\tbgColor : "+this.bgColor+
                "\n\ttextColor : "+this.textColor+
                "\n\ttext : "+this.text+
                "\n\tdisplayMode : "+this.displayMode;
        return resultat;
    }

}
