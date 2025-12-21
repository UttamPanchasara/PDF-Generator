package android.print;

import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

/**
 * Helper class to print PDF documents using Android's Print framework.
 * Placed in android.print package to access package-private PrintDocumentAdapter callbacks.
 */
@SuppressWarnings("ClassCanBeRecord")
public class PdfPrint {
    private static final String TAG = PdfPrint.class.getSimpleName();
    private final PrintAttributes printAttributes;

    public PdfPrint(@NonNull PrintAttributes printAttributes) {
        this.printAttributes = printAttributes;
    }

    public void print(
            @NonNull final PrintDocumentAdapter printAdapter,
            @NonNull final File path,
            @NonNull final String fileName,
            @NonNull final CallbackPrint callback
    ) {
        printAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(@Nullable PrintDocumentInfo info, boolean changed) {
                ParcelFileDescriptor outputFile = getOutputFile(path, fileName);
                if (outputFile == null) {
                    callback.onFailure("Failed to create output file. Check storage permissions and path.");
                    return;
                }

                printAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, outputFile, new CancellationSignal(), new PrintDocumentAdapter.WriteResultCallback() {
                    @Override
                    public void onWriteFinished(@Nullable PageRange[] pages) {
                        super.onWriteFinished(pages);
                        closeFileDescriptor(outputFile);

                        if (pages != null && pages.length > 0) {
                            File file = new File(path, fileName);
                            String filePath = file.getAbsolutePath();
                            callback.onSuccess(filePath);
                        } else {
                            callback.onFailure("No pages were written to PDF");
                        }
                    }

                    @Override
                    public void onWriteFailed(@Nullable CharSequence error) {
                        super.onWriteFailed(error);
                        closeFileDescriptor(outputFile);
                        String errorMsg = error != null ? error.toString() : "Unknown write error";
                        callback.onFailure("PDF write failed: " + errorMsg);
                    }

                    @Override
                    public void onWriteCancelled() {
                        super.onWriteCancelled();
                        closeFileDescriptor(outputFile);
                        callback.onFailure("PDF write was cancelled");
                    }
                });
            }

            @Override
            public void onLayoutFailed(@Nullable CharSequence error) {
                super.onLayoutFailed(error);
                String errorMsg = error != null ? error.toString() : "Unknown layout error";
                callback.onFailure("PDF layout failed: " + errorMsg);
            }

            @Override
            public void onLayoutCancelled() {
                super.onLayoutCancelled();
                callback.onFailure("PDF layout was cancelled");
            }
        }, null);
    }

    @Nullable
    private ParcelFileDescriptor getOutputFile(@NonNull File path, @NonNull String fileName) {
        try {
            if (!path.exists()) {
                boolean created = path.mkdirs();
                if (!created && !path.exists()) {
                    Log.e(TAG, "Failed to create directory: " + path.getAbsolutePath());
                    return null;
                }
            }

            File file = new File(path, fileName);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    Log.w(TAG, "Failed to delete existing file: " + file.getAbsolutePath());
                }
            }

            boolean created = file.createNewFile();
            if (!created && !file.exists()) {
                Log.e(TAG, "Failed to create file: " + file.getAbsolutePath());
                return null;
            }

            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
            return null;
        }
    }

    private void closeFileDescriptor(@Nullable ParcelFileDescriptor descriptor) {
        if (descriptor != null) {
            try {
                descriptor.close();
            } catch (Exception e) {
                Log.e(TAG, "Failed to close ParcelFileDescriptor", e);
            }
        }
    }

    /**
     * Callback interface for PDF generation results.
     */
    public interface CallbackPrint {
        /**
         * Called when PDF is successfully created.
         * @param path The absolute path to the created PDF file.
         */
        void onSuccess(@NonNull String path);

        /**
         * Called when PDF generation fails.
         * @param errorMsg Description of the error.
         */
        void onFailure(@NonNull String errorMsg);
    }
}
