package org.geotoolkit.data.kml.model;

import java.net.URI;
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

    public DefaultFolder() {
        this.features = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
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
    public DefaultFolder(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<AbstractFeature> features,
            List<SimpleType> folderSimpleExtensions,
            List<AbstractObject> folderObjectExtensions) {

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink,
                address, addressDetails,
                phoneNumber, snippet, description,
                view, timePrimitive,
                styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions,
                abstractContainerObjectExtensions);
        this.features = (features == null) ? EMPTY_LIST : features;
        if (folderSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.FOLDER).addAll(folderSimpleExtensions);
        }
        if (folderObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.FOLDER).addAll(folderObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getAbstractFeatures() {
        return this.features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeatures(List<AbstractFeature> abstractFeatures) {
        this.features = abstractFeatures;
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tFolderDefault : ";
        return resultat;
    }
}
