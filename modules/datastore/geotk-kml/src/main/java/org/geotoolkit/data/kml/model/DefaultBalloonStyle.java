package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultBalloonStyle extends DefaultAbstractSubStyle implements BalloonStyle {

    private Color bgColor;
    private Color textColor;
    private String text;
    private DisplayMode displayMode;

    /**
     * 
     */
    public DefaultBalloonStyle() {
        this.bgColor = DEF_BG_COLOR;
        this.textColor = DEF_TEXT_COLOR;
        this.displayMode = DEF_DISPLAY_MODE;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractSubStyleSimpleExtensions
     * @param abstractSubStyleObjectExtensions
     * @param bgColor
     * @param textColor
     * @param text
     * @param displayMode
     * @param balloonStyleSimpleExtensions
     * @param balloonStyleObjectExtensions
     */
    public DefaultBalloonStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions,
            Color bgColor, Color textColor, String text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions,
            List<AbstractObject> balloonStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractSubStyleSimpleExtensions,
                abstractSubStyleObjectExtensions);
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.text = text;
        this.displayMode = displayMode;
        if (balloonStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.BALLOON_STYLE).addAll(balloonStyleSimpleExtensions);
        }
        if (balloonStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.BALLOON_STYLE).addAll(balloonStyleObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Color getBgColor() {
        return this.bgColor;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Color getTextColor() {
        return this.textColor;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DisplayMode getDisplayMode() {
        return this.displayMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tBalloonStyleDefault : "
                + "\n\tbgColor : " + this.bgColor
                + "\n\ttextColor : " + this.textColor
                + "\n\ttext : " + this.text
                + "\n\tdisplayMode : " + this.displayMode;
        return resultat;
    }
}
