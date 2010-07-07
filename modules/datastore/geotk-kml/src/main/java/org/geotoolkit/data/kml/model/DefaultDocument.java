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
public class DefaultDocument extends DefaultAbstractContainer implements Document {

    private List<Schema> schemas;
    private List<AbstractFeature> features;

    /**
     * 
     */
    public DefaultDocument() {
        this.schemas = EMPTY_LIST;
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
     * @param schemas
     * @param features
     * @param documentSimpleExtensions
     * @param documentObjectExtensions
     */
    public DefaultDocument(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<Schema> schemas,
            List<AbstractFeature> features,
            List<SimpleType> documentSimpleExtensions,
            List<AbstractObject> documentObjectExtensions) {

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink,
                address, addressDetails,
                phoneNumber, snippet, description,
                view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions,
                abstractContainerObjectExtensions);
        this.schemas = (schemas == null) ? EMPTY_LIST : schemas;
        this.features = (features == null) ? EMPTY_LIST : features;
        if (documentSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.DOCUMENT).addAll(documentSimpleExtensions);
        }
        if (documentObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.DOCUMENT).addAll(documentObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Schema> getSchemas() {
        return this.schemas;
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
    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeatures(List<AbstractFeature> features) {
        this.features = features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tDocumentDefault : ";
        return resultat;
    }
}
