package org.geotoolkit.test;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.logging.Logging;

import static org.geotoolkit.test.VerifiableStorageConnector.Integrity.KO;
import static org.geotoolkit.test.VerifiableStorageConnector.Integrity.OK;
import static org.geotoolkit.test.VerifiableStorageConnector.Integrity.UNDEFINED;
import static org.geotoolkit.test.VerifiableStorageConnector.Integrity.UNSUPPORTED;
import static org.junit.Assert.fail;

public class VerifiableStorageConnector extends StorageConnector {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.test");
    
    private final CharSequence description;
    private final Map<Object, IntegrityKey> checkers;

    /**
     * Creates a new data store connection wrapping the given input/output object.
     * The object can be of any type, but the class javadoc lists the most typical ones.
     *
     * @param description Use in case of assertion error to give description of the component in fault.
     * @param storage the input/output object as a URL, file, image input stream, <i>etc.</i>.
     */
    public VerifiableStorageConnector(final CharSequence description, Object storage) {
        super(storage);
        this.description = description;
        checkers = new IdentityHashMap<>();
    }

    @Override
    public <T> T getStorageAs(Class<T> type) throws IllegalArgumentException, DataStoreException {
        final T storage = super.getStorageAs(type);
        if (storage == null) return storage;
        try {
            checkers.computeIfAbsent(storage, view -> createKey(view));
        } catch (UncheckedIOException e) {
            throw new DataStoreException("Error while accessing storage", e.getCause());
        } catch (BackingStoreException e) {
            throw e.unwrapOrRethrow(DataStoreException.class);
        }
        return storage;
    }

    @Override
    public void closeAllExcept(Object view) throws DataStoreException {
        verifyAllExcept(view);
        super.closeAllExcept(view);
    }

    public void verifyAllExcept(Object view) throws DataStoreException {
        final IntegrityKey key = view == null ? null : checkers.remove(view);
        verifyAll();
        if (view != null) checkers.put(view, key);
    }

    public void verifyAll() throws DataStoreException {
        try {
            for (Map.Entry<Object, IntegrityKey> entry : checkers.entrySet()) {
                final Object view = entry.getKey();
                final IntegrityResult result = entry.getValue().verify(view);
                switch (result.state) {
                    case KO:
                        fail(formatError(result, view));
                        break;
                    case UNSUPPORTED:
                        LOGGER.warning(() -> this.formatUnsupported(view));
                        break;
                    case UNDEFINED:
                        LOGGER.warning(() -> this.formatUndefined(result, view));
                        break;
                    case OK:
                        LOGGER.finest("Storage integrity check success");
                }
            }
        } catch (IOException e) {
            throw new DataStoreException("Error while accessing underlying storage", e);
        }
    }

    private String formatError(IntegrityResult result, Object viewInError) {
        return String.format(
                "Storage view has not been correctly rewind.%nView: %s%nError description: %s%nSource description: %s",
                viewInError.getClass().getCanonicalName(),
                result.errorDescription.orElse("No description available"),
                description
        );
    }

    private String formatUndefined(IntegrityResult result, Object view) {
        return String.format(
                "Storage verification cannot be done on view: %s%nReason: %s",
                view.getClass().getCanonicalName(), result.errorDescription.orElse("No reason available")
        );
    }

    private String formatUnsupported(Object view) {
        return String.format(
                "Deactivate storage verification for unsupported view: %s",
                view.getClass().getCanonicalName()
        );
    }

    protected IntegrityKey createKey(final Object view) {
        try {
            if (view instanceof ByteBuffer) {
                return forByteBuffer((ByteBuffer) view);
            } else if (view instanceof InputStream) {
                return forInputStream((InputStream) view);
            } else if (view instanceof ImageInputStream) {
                return forImageInputStream((ImageInputStream)view);
            } else {
                return in -> new IntegrityResult(UNSUPPORTED);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static IntegrityKey forByteStream(ByteStream view, Function<Object, ByteStream> converter) throws IOException {
        byte[] ctrlValue = new byte[64];
        view.mark(ctrlValue.length);
        try (final Closeable resetOnceDone = view::reset) {
            int readBytes = readFully(view, ctrlValue);

            if (readBytes <= 0)
                return in -> new IntegrityResult(UNDEFINED, "Cannot enable verification, because no bytes have been read from the stream");
            if (readBytes < ctrlValue.length) {
                ctrlValue = Arrays.copyOf(ctrlValue, readBytes);
            }

            final byte[] ctrl = ctrlValue;
            return in -> verify(converter.apply(in), ctrl);
        }
    }

    private static IntegrityKey forInputStream(InputStream view) throws IOException {
        if (!view.markSupported()) {
            return in -> new IntegrityResult(UNSUPPORTED, "Cannot verify integrity on a stream that does not support mark/reset");
        }

        return forByteStream(toByteStream(view), in -> toByteStream((InputStream)in));
    }

    /**
     * Commodity for {@link #readFully(ByteStream, byte[], int) @code readFully(source, target, 5)}.
     */
    private static int readFully(final ByteStream source, byte[] target) throws IOException {
        return readFully(source, target, 5);
    }

    /**
     * Try to extract as many bytes as possible from a given source that can be put in a target array.
     *
     * @param source The IO object to read data from.
     * @param target The array to put read data into. Defines maximum number of bytes to read by its length.
     * @param maxTrys If we cannot completely fill the array in a single pass, how many times to try again at maximum.
     * @return Total number of read bytes. Always >= 0.
     */
    private static int readFully(final ByteStream source, byte[] target, int maxTrys) throws IOException {
        int nbRead = 0;
        for (int i = 0; i < maxTrys && nbRead < target.length; i++) {
            final int read = source.read(target, nbRead, target.length - nbRead);
            if (read < 0) break; // end-of-stream
            nbRead += read;
        }
        return nbRead;
    }

    private static IntegrityResult verify(ByteStream view, byte[] ctrl) throws IOException {
        byte[] currentValue = new byte[ctrl.length];
        int nbRead;
        view.mark(ctrl.length);
        try (Closeable resetOnceRead = view::reset) {
            nbRead = readFully(view, currentValue);
        }
        if (nbRead < ctrl.length) return new IntegrityResult(UNDEFINED, "Cannot verify, because we cannot read enough bytes");
        if (Arrays.equals(ctrl, currentValue)) return new IntegrityResult(OK);
        else return new IntegrityResult(KO, String.format("First %d bytes does not match", ctrl.length));
    }

    private static IntegrityKey forByteBuffer(ByteBuffer view) {
        final int remain = view.remaining();
        if (remain <= 0) {
            return in -> new IntegrityResult(UNDEFINED, "Verification deactivated because of an empty ByteBuffer as storage view");
        }
        final byte[] ctrlValue = new byte[Math.min(64, remain)];
        view.get(ctrlValue).rewind();
        return in -> {
            final ByteBuffer storage = (ByteBuffer) in;
            if (storage.remaining() != remain) return new IntegrityResult(KO, "Byte buffer remaining bytes does not match");

            final byte[] currentValue = new byte[ctrlValue.length];
            storage.get(currentValue).rewind();
            if (Arrays.equals(ctrlValue, currentValue)) return new IntegrityResult(OK);
            else return new IntegrityResult(KO, String.format("First %d bytes does not match", ctrlValue.length));
        };
    }

    private static IntegrityKey forImageInputStream(ImageInputStream view) throws IOException {
        return forByteStream(toByteStream(view), in -> toByteStream((ImageInputStream)in));
    }

    @FunctionalInterface
    public interface IntegrityKey {

        /**
         * Ensures that given view integrity.
         *
         * @param associatedView The view which served at the creation of this key
         * @return True if the view has been properly rewinded after use, and match integrity check defined by this
         * component.
         * @throws IOException If an error occurs while accessing view storage.
         */
        IntegrityResult verify(Object associatedView) throws IOException;
    }

    public enum Integrity {
        /**
         * Verification passed successfully, meaning that view if finely rewind.
         */
        OK,
        /**
         * Tested view is corrupted.
         */
        KO,
        /**
         * Verification procedure is not yet implemented (or cannot be implemented at all) for given storage view.
         */
        UNSUPPORTED,
        /**
         * Different from unsupported: a verification should be possible for the view, but cannot be done currently
         * because of a unexpected state or problem.
         */
        UNDEFINED
    }

    /**
     * Note: with sealed class, we could merge current class with above enum to provide a message only when needed (KO).
     * And in Kotlin, we could use singletons for subtypes without any state.
     */
    public static final class IntegrityResult {
        public final Integrity state;
        public final Optional<CharSequence> errorDescription;

        public IntegrityResult(Integrity state) {
            this(state, null);
        }

        public IntegrityResult(Integrity state, CharSequence errorDescription) {
            this.state = state;
            this.errorDescription = Optional.ofNullable(errorDescription);
        }
    }

    /**
     * A dummy abstraction to unify {@link InputStream} and {@link ImageInputStream} APIs needed for validation.
     */
    private interface ByteStream {
        /**
         * A bridge for both {@link ImageInputStream#read(byte[], int, int)} and {@link InputStream#read(byte[], int, int)}.
         *
         * @param target Destination buffer
         * @param offsetInTarget From which index to fill
         * @param maxToRead Maximum number of bytes to read.
         * @return -1 if reading is note possible anymore (end of stream), 0 for "not now", else number of bytes transferred.
         */
        int read(byte[] target, int offsetInTarget, int maxToRead) throws IOException;

        /**
         * Equivalent of {@link InputStream#reset()} and {@link ImageInputStream#reset()}.
         */
        void reset() throws IOException;

        /**
         * Equivalent of {@link InputStream#mark(int)} or {@link ImageInputStream#mark()}
         */
        void mark(int readLimit);
    }

    private static ByteStream toByteStream(final InputStream in) {
        return new ByteStream() {
            @Override
            public int read(byte[] target, int offsetInTarget, int maxToRead) throws IOException {
                return in.read(target, offsetInTarget, maxToRead);
            }

            @Override
            public void reset() throws IOException {
                in.reset();
            }

            @Override
            public void mark(int readLimit) {
                in.mark(readLimit);
            }
        };
    }

    private static ByteStream toByteStream(final ImageInputStream in) {
        return new ByteStream() {
            @Override
            public int read(byte[] target, int offsetInTarget, int maxToRead) throws IOException {
                return in.read(target, offsetInTarget, maxToRead);
            }

            @Override
            public void reset() throws IOException {
                in.reset();
            }

            @Override
            public void mark(int readLimit) {
                in.mark();
            }
        };
    }
}
