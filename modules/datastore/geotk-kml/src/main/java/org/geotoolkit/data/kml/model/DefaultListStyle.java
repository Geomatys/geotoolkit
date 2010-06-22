package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultListStyle extends DefaultAbstractSubStyle implements ListStyle {

    private ListItem listItem;
    private Color bgColor;
    private List<ItemIcon> itemIcons;
    private int maxSnippetLines;
    private List<SimpleType> listStyleSimpleExtensions;
    private List<AbstractObject> listStyleObjectExtensions;

    /**
     * 
     */
    public DefaultListStyle(){
        this.bgColor = DEF_BG_COLOR;
        this.maxSnippetLines = DEF_MAX_SNIPPET_LINES;
        this.listStyleSimpleExtensions = EMPTY_LIST;
        this.listStyleObjectExtensions = EMPTY_LIST;
    }

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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setListItem(ListItem listItem) {this.listItem = listItem;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBgColor(Color bgColor) {this.bgColor = bgColor;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setItemIcons(List<ItemIcon> itemIcons) {this.itemIcons = itemIcons;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxSnippetLines(int maxSnippetLines) {this.maxSnippetLines = maxSnippetLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setListStyleSimpleExtensions(List<SimpleType> listStyleSimpleExtensions) {
        this.listStyleSimpleExtensions = listStyleSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setListStyleObjectExtensions(List<AbstractObject> listStyleObjectExtensions) {
        this.listStyleObjectExtensions = listStyleObjectExtensions;
    }

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
