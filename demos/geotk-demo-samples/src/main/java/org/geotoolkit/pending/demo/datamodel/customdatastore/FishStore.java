

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

public class FishStore extends DataStore implements FeatureSet {


    private final File storage;
    private final FeatureType type;
    private final ParameterValueGroup params;

    public FishStore(URI uri) throws DataStoreException{
        this(toParameters(uri));
    }

    public FishStore(ParameterValueGroup params) throws DataStoreException{
        super();
        this.params = params;

        URI uri = (URI) params.parameter(FishProvider.PATH.getName().toString()).getValue();
        storage = new File(uri);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("length");
        ftb.addAttribute(Point.class).setName("position").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        type = ftb.build();
    }

    private static ParameterValueGroup toParameters(URI uri) {
        final Parameters params = Parameters.castOrWrap(FishProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.parameter(FishProvider.LOCATION).setValue(uri);
        return params;
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(FishProvider.NAME);
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return null;
    }

    @Override
    public ParameterValueGroup getOpenParameters() {
        return params;
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();

        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(getType().getName().toInternationalString());
        citation.setIdentifiers(Arrays.asList(new NamedIdentifier(getType().getName())));

        final DefaultDataIdentification ident = new DefaultDataIdentification();
        ident.setCitation(citation);

        metadata.setIdentificationInfo(Arrays.asList(ident));

        return metadata;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {

        FishReader reader;
        try {
            reader = new FishReader(storage, type);
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex);
        }

        final Spliterator<Feature> spliterator = Spliterators.spliterator(reader, Long.MAX_VALUE, Spliterator.ORDERED);
        final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(reader::close);
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public void close() throws DataStoreException {
    }
}
