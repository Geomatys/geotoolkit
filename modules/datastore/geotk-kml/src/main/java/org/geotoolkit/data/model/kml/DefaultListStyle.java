package org.geotoolkit.data.model.kml;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultListStyle extends DefaultAbstractSubStyle implements ListStyle {

    private final ListItem listItem;
    private final Color bgColor;
    private final List<ItemIcon> itemIcons;
    private final int maxSnippetLines;
    private final List<SimpleType> listStyleSimpleExtensions;
    private final List<AbstractObject> listStyleObjectExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param subStyleSimpleExtensions
     * @param subStyleObjectExtensions
     * @param listItem
     * @param bgColor
     * @param itemIcons
     * @param maxSnippetLines
     * @param listStyleSimpleExtensions
     * @param listStyleObjectExtensions
     */
    public DefaultListStyle(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> subStyleSimpleExtensions, List<AbstractObject> subStyleObjectExtensions,
            ListItem listItem, Color bgColor, List<ItemIcon> itemIcons, int maxSnippetLines,
            List<SimpleType> listStyleSimpleExtensions, List<AbstractObject> listStyleObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                subStyleSimpleExtensions, subStyleObjectExtensions);
        this.listItem = listItem;
        this.bgColor = bgColor;
        this.itemIcons = itemIcons;
        this.maxSnippetLines = maxSnippetLines;
        this.listStyleSimpleExtensions = (listStyleSimpleExtensions == null) ? EMPTY_LIST : listStyleSimpleExtensions;
        this.listStyleObjectExtensions = (listStyleObjectExtensions == null) ? EMPTY_LIST : listStyleObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ListItem getListItem() {return this.listItem;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Color getBgColor() {return this.bgColor;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<ItemIcon> getItemIcons() {return this.itemIcons;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxSnippetLines() {return this.maxSnippetLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getListStyleSimpleExtensions() {return this.listStyleSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getListStyleObjectExtensions() {return this.listStyleObjectExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tListStyleDefault : "+
                "\n\tlistItem : "+this.listItem+
                "\n\tbgColor : "+this.bgColor+
                "\n\titemIcons : "+this.itemIcons+
                "\n\tmaxSnippetLines : "+this.maxSnippetLines;
        return resultat;
    }
}
