package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps AbstractSubStyle element.</p>
 *
 * <pre>
 * &lt;element name="AbstractSubStyleGroup" type="kml:AbstractSubStyleType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="AbstractSubStyleType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractSubStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractSubStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractSubStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractSubStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 * 
 * @author Samuel Andrés
 */
public interface AbstractSubStyle extends AbstractObject {

    /**
     *
     * @return The AbstractSubStyle simple extensions.
     */
    public List<SimpleType> getSubStyleSimpleExtensions();

    /**
     *
     * @return The AbstractSubStyle object extensions.
     */
    public List<AbstractObject> getSubStyleObjectExtensions();

    /**
     * 
     * @param subStyleSimpleExtensions
     */
    public void setSubStyleSimpleExtensions(List<SimpleType> subStyleSimpleExtensions);

    /**
     *
     * @param subStyleObjectExtensions
     */
    public void setSubStyleObjectExtensions(List<AbstractObject> subStyleObjectExtensions);
}
