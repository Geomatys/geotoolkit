package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps ListStyle.</p>
 *
 * <pre>
 * &lt;element name="ListStyle" type="kml:ListStyleType" substitutionGroup="kml:AbstractSubStyleGroup"/>
 *
 * &lt;complexType name="ListStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractSubStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:listItemType" minOccurs="0"/>
 *              &lt;element ref="kml:bgColor" minOccurs="0"/>
 *              &lt;element ref="kml:ItemIcon" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:maxSnippetLines" minOccurs="0"/>
 *              &lt;element ref="kml:ListStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ListStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ListStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ListStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ListStyle extends AbstractSubStyle {

    /**
     *
     * @return
     */
    public ListItem getListItem();

    /**
     *
     * @return
     */
    public Color getBgColor();

    /**
     *
     * @return
     */
    public List<ItemIcon> getItemIcons();

    /**
     *
     * @return
     */
    public int getMaxSnippetLines();

    /**
     *
     * @return the list of ListStyle simple extensions.
     */
    public List<SimpleType> getListStyleSimpleExtensions();

    /**
     *
     * @return the list of ListStyle object extensions.
     */
    public List<AbstractObject> getListStyleObjectExtensions();

}
