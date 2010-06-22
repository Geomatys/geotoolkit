package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * <p>This interface maps AbstractContainerGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractContainerGroup" type="kml:AbstractContainerType" abstract="true" substitutionGroup="kml:AbstractFeatureGroup"/>
 *
 * &lt;complexType name="AbstractContainerType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractFeatureType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractContainerSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractContainerObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractContainerSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractContainerObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractContainer extends AbstractFeature {

    /**
     *
     * @return The AbstractContainer simple extensions.
     */
    public List<SimpleType> getAbstractContainerSimpleExtensions();

    /**
     *
     * @return The AbstractContainer object extensions.
     */
    public List<AbstractObject> getAbstractContainerObjectExtensions();

    /**
     *
     * @param abstractContainerSimpleExtensions
     */
    public void setAbstractContainerSimpleExtensions(List<SimpleType> containerSimpleExtensions);

    /**
     *
     * @param abstractContainerObjectExtensions
     */
    public void setAbstractContainerObjectExtensions(List<AbstractObject> containerObjectExtensions);

}
