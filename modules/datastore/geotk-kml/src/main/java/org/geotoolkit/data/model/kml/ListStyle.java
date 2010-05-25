package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface ListStyle extends AbstractSubStyle {

    public ListItem getListItem();
    public Color getBgColor();
    public List<ItemIcon> getItemIcons();
    public int getMaxSnippetLines();
    public List<SimpleType> getListStyleSimpleExtensions();
    public List<AbstractObject> getListStyleObjectExtensions();

}
