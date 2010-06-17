package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractViewGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractViewGroup" type="kml:AbstractViewType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractViewType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractViewSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractViewObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractViewSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractViewObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractView extends AbstractObject{

    /**
     *
     * @return the list of AbstractView simple extensions.
     */
    public List<SimpleType> getAbstractViewSimpleExtensions();

    /**
     *
     * @return the list of AbstractView object extensions.
     */
    public List<AbstractObject> getAbstractViewObjectExtensions();

    /**
     *
     * @param abstractViewSimpleExtensions
     */
    public void setAbstractViewSimpleExtensions(List<SimpleType> abstractViewSimpleExtensions);

    /**
     * 
     * @param abstractViewObjectExtensions
     */
    public void setAbstractViewObjectExtensions(List<AbstractObject> abstractViewObjectExtensions);

}
