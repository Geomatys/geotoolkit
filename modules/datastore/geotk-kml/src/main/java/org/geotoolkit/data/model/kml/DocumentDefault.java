package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;

/**
 *
 * @author Samuel Andrés
 */
public class DocumentDefault extends AbstractContainerDefault implements Document{

    private List<Schema> schemas;
    private List<AbstractFeature> features;
    private List<SimpleType> documentSimpleExtensions;
    private List<AbstractObject> documentObjectExtensions;

    public DocumentDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions,
            List<Schema> schemas, List<AbstractFeature> features,
            List<SimpleType> documentSimpleExtensions,
            List<AbstractObject> documentObjectExtensions){

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open, author, link, address, addressDetails, phoneNumber,
                snippet, description, view, timePrimitive, styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions,
                abstractContainerSimpleExtensions, abstractContainerObjectExtensions);

        this.schemas = schemas;
        this.features = features;
        this.documentSimpleExtensions = documentSimpleExtensions;
        this.documentObjectExtensions = documentObjectExtensions;
    }

    @Override
    public List<Schema> getSchemas() {return this.schemas;}

    @Override
    public List<AbstractFeature> getAbstractFeatures() {return this.features;}

    @Override
    public List<AbstractObject> getDocumentObjectExtensions() {return this.documentObjectExtensions;}

    @Override
    public List<SimpleType> getDocumentSimpleExtensions() { return this.documentSimpleExtensions;}

    @Override
    public String toString(){
        String resultat = super.toString()+
                "\n\tDocumentDefault : ";
        return resultat;
    }

}
