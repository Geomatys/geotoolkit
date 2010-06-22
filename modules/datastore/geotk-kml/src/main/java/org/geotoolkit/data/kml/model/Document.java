package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Document element.</p>
 *
 * <pre>
 * &lt;element name="Document" type="kml:DocumentType" substitutionGroup="kml:AbstractContainerGroup"/>
 *
 * &lt;complexType name="DocumentType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractContainerType">
 *          &lt;sequence>
 *              &lt;element ref="kml:Schema" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:DocumentSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:DocumentObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="DocumentSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="DocumentObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
