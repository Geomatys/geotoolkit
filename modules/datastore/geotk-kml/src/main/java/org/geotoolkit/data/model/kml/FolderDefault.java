package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class FolderDefault extends AbstractContainerDefault implements Folder {

    private final List<AbstractFeature> features;
    private final List<SimpleType> folderSimpleExtensions;
    private final List<AbstractObject> folderObjectExtensions;

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
    public FolderDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
    public List<SimpleType> getFolderSimpleExtensions() { return this.folderSimpleExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tFolderDefault : ";
        return resultat;
    }

}
