package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.xal.model.AddressDetails;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultFolder extends DefaultAbstractContainer implements Folder {

    private List<AbstractFeature> features;
    private List<SimpleType> folderSimpleExtensions;
    private List<AbstractObject> folderObjectExtensions;

    public DefaultFolder(){
        this.features = EMPTY_LIST;
        this.folderSimpleExtensions = EMPTY_LIST;
        this.folderObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param abstractContainerSimpleExtensions
     * @param abstractContainerObjectExtensions
     * @param features
     * @param folderSimpleExtensions
     * @param folderObjectExtensions
     */
    public DefaultFolder(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<AbstractFeature> features,
            List<SimpleType> folderSimpleExtensions,
            List<AbstractObject> folderObjectExtensions){

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions);

        this.features = (features == null) ? EMPTY_LIST : features;
        this.folderSimpleExtensions = (folderSimpleExtensions == null) ? EMPTY_LIST : folderSimpleExtensions;
        this.folderObjectExtensions = (folderObjectExtensions == null) ? EMPTY_LIST : folderObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getAbstractFeatures() {return this.features;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getFolderObjectExtensions() {return this.folderObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getFolderSimpleExtensions() {return this.folderSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeatures(List<AbstractFeature> abstractFeatures) {
        this.features = abstractFeatures;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFolderSimpleExtensions(List<SimpleType> folderSimpleExtensions) {
        this.folderSimpleExtensions = folderSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void getFolderObjectExtensions(List<AbstractObject> folderObjectExtensions) {
        this.folderObjectExtensions = folderObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tFolderDefault : ";
        return resultat;
    }
}
