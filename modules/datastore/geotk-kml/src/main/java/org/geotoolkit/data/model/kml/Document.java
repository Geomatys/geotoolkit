package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Document element.</p>
 *
 * <br />&lt;element name="Document" type="kml:DocumentType" substitutionGroup="kml:AbstractContainerGroup"/>
 * <br />&lt;complexType name="DocumentType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractContainerType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:Schema" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:DocumentSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:DocumentObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="DocumentSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="DocumentObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Document extends AbstractContainer {

    /**
     *
     * @return
     */
    public List<Schema> getSchemas();

    /**
     *
     * @return
     */
    public List<AbstractFeature> getAbstractFeatures();

    /**
     *
     * @return the list of Document simple extensions.
     */
    public List<SimpleType> getDocumentSimpleExtensions();

    /**
     *
     * @return the list of Document object extensions.
     */
    public List<AbstractObject> getDocumentObjectExtensions();

}
