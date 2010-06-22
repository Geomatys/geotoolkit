package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps ItemIcon element.</p>
 *
 * <pre>
 * &lt;element name="ItemIcon" type="kml:ItemIconType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ItemIconType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:state" minOccurs="0"/>
 *              &lt;element ref="kml:href" minOccurs="0"/>
 *              &lt;element ref="kml:ItemIconSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ItemIconObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ItemIconSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ItemIconObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ItemIcon extends AbstractObject {

    /**
     *
     * @return the list of icon states.
     */
    public List<ItemIconState> getStates();

    /**
     *
     * @return the href.
     */
    public String getHref();

    /**
     *
     * @return the ItemIcon list of simple extensions.
     */
    public List<SimpleType> getItemIconSimpleExtensions();

    /**
     *
     * @return the ItemIcon list of object extensions.
     */
    public List<AbstractObject> getItemIconObjectExtensions();

    /**
     *
     * @param states
     */
    public void setStates(List<ItemIconState> states);

    /**
     *
     * @param href
     */
    public void setHref(String href);

    /**
     *
     * @param itemIconSimpleExtensions
     */
    public void setItemIconSimpleExtensions(List<SimpleType> itemIconSimpleExtensions);

    /**
     *
     * @param itemIconObjectExtensions
     */
    public void setItemIconObjectExtensions(List<AbstractObject> itemIconObjectExtensions);

}
