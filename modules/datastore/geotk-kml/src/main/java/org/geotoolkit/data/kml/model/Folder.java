package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Foder element.</p>
 *
 * <pre>
 * &lt;element name="Folder" type="kml:FolderType" substitutionGroup="kml:AbstractContainerGroup"/>
 *
 * &lt;complexType name="FolderType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractContainerType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:FolderSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:FolderObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="FolderSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="FolderObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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

    /**
     *
     * @param abstractFeatures
     */
    public void setAbstractFeatures(List<AbstractFeature> abstractFeatures);

    /**
     *
     * @param folderSimpleExtensions
     */
    public void setFolderSimpleExtensions(List<SimpleType> folderSimpleExtensions);

    /**
     * 
     * @param folderObjectExtensions
     */
    public void setFolderObjectExtensions(List<AbstractObject> folderObjectExtensions);

}
