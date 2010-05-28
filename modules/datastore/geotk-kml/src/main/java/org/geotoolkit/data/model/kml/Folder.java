package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Foder element.</p>
 *
 * <br />&lt;element name="Folder" type="kml:FolderType" substitutionGroup="kml:AbstractContainerGroup"/>
 * <br />&lt;complexType name="FolderType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractContainerType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:FolderSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:FolderObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="FolderSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="FolderObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Folder extends AbstractContainer {

    /**
     *
     * @return
     */
    public List<AbstractFeature> getAbstractFeatures();

    /**
     *
     * @return the list of Folder simple extensions.
     */
    public List<SimpleType> getFolderSimpleExtensions();

    /**
     *
     * @return the lst of Folder object extensions.
     */
    public List<AbstractObject> getFolderObjectExtensions();

}
