package org.geotoolkit.storage.feature;

import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;

import org.junit.Assert;
import org.junit.Test;

public class FeatureSetWrapperTest {

    @Test
    public void ensureProperClosing() {
        final Semaphore closeSignal = new Semaphore(1);
        final FeatureSetWrapper features = new FeatureSetWrapper(new SynchronizedSet(closeSignal), null);
        try (final FeatureIterator it = features.iterator()) {
            if (it.hasNext()) it.next();
        }

        Assert.assertTrue("Feature set wrapper iterator has not been properly closed", closeSignal.tryAcquire());
    }

    private static class SynchronizedSet extends AbstractFeatureSet {

        final Semaphore barrier;

        protected SynchronizedSet(final Semaphore barrier) {
            super(null);
            this.barrier = barrier;
        }

        @Override
        public FeatureType getType() throws DataStoreException {
            throw new UnsupportedOperationException("Not supported yet"); // "Alexis Manin (Geomatys)" on 12/02/2020
        }

        @Override
        public Stream<Feature> features(boolean b) throws DataStoreException {
            final Stream<Feature> test = Stream.empty();
            if (!barrier.tryAcquire()) {
                throw new AssertionError("Bad test procedure: test semaphore already busy");
            }
            return test.onClose(barrier::release);
        }
    }
}
