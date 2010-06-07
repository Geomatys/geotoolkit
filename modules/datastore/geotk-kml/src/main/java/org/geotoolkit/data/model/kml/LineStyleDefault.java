package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class LineStyleDefault extends AbstractColorStyleDefault implements LineStyle {

    private final double width;
    private final List<SimpleType> lineStyleSimpleExtensions;
    private final List<AbstractObject> lineStyleObjectExtentions;

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
     * @param width
     * @param lineStyleSimpleExtensions
     * @param lineStyleObjectExtensions
     */
    public LineStyleDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            Color color, ColorMode colorMode,
            List<SimpleType> colorStyleSimpleExtensions, List<AbstractObject> colorStyleObjectExtensions,
            double width,
            List<SimpleType> lineStyleSimpleExtensions, List<AbstractObject> lineStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions,
                color, colorMode, colorStyleSimpleExtensions, colorStyleObjectExtensions);
        this.width = width;
        this.lineStyleSimpleExtensions = (lineStyleSimpleExtensions == null) ? EMPTY_LIST : lineStyleSimpleExtensions;
        this.lineStyleObjectExtentions = (lineStyleObjectExtensions == null) ? EMPTY_LIST : lineStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getWidth() {return this.width;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getLineStyleSimpleExtensions() {return this.lineStyleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getLineStyleObjectExtensions() {return this.lineStyleObjectExtentions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tLineStyleDefault : "+
                "\n\twidth : "+this.width;
        return resultat;
    }

}
