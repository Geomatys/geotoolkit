package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps AbstractContainerGroup element.</p>
 *
 * <br />&lt;element name="AbstractContainerGroup" type="kml:AbstractContainerType" abstract="true" substitutionGroup="kml:AbstractFeatureGroup"/>
 * <br />&lt;complexType name="AbstractContainerType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractFeatureType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractContainerSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractContainerObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractContainerSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AbstractContainerObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>

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

}
