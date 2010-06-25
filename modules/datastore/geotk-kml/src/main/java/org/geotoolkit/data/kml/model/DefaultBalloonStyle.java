package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
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
    private List<SimpleType> balloonStyleSimpleExtensions;
    private List<AbstractObject> balloonStyleObjectExtensions;

    /**
     * 
     */
    public DefaultBalloonStyle() {
        this.bgColor = DEF_BG_COLOR;
        this.textColor = DEF_TEXT_COLOR;
        this.displayMode = DEF_DISPLAY_MODE;
        this.balloonStyleSimpleExtensions = EMPTY_LIST;
        this.balloonStyleObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param bgColor
     * @param textColor
     * @param text
     * @param displayMode
     * @param balloonStyleSimpleExtensions
     * @param balloonStyleObjectExtensions
     */
    public DefaultBalloonStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color bgColor, Color textColor, String text, DisplayMode displayMode,
            List<SimpleType> balloonStyleSimpleExtensions, List<AbstractObject> balloonStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions);
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.text = text;
        this.displayMode = displayMode;
        this.balloonStyleSimpleExtensions = (balloonStyleSimpleExtensions == null) ? EMPTY_LIST : balloonStyleSimpleExtensions;
        this.balloonStyleObjectExtensions = (balloonStyleObjectExtensions == null) ? EMPTY_LIST : balloonStyleObjectExtensions;
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
    public List<SimpleType> getBalloonStyleSimpleExtensions() {
        return this.balloonStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getBalloonStyleObjectExtensions() {
        return this.balloonStyleObjectExtensions;
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBalloonStyleSimpleExtensions(List<SimpleType> balloonStyleSimpleExtensions) {
        this.balloonStyleSimpleExtensions = balloonStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBalloonStyleObjectExtensions(List<AbstractObject> balloonStyleObjectExtensions) {
        this.balloonStyleObjectExtensions = balloonStyleObjectExtensions;
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
