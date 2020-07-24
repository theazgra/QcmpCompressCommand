package it4i.cz.ui;

import azgracompress.compression.CompressionOptions;
import azgracompress.data.V2i;
import azgracompress.fileformat.QuantizationType;
import ij.IJ;

import java.awt.*;

public class CompressionDialog {

    private final int KEY_QUANTIZATION_TYPE = 1;
    private final int KEY_CODEBOOK_TYPE = 2;
    private final int KEY_CODEBOOK_SIZE = 3;
    private final int KEY_VECTOR_DIM = 4;
    private final int KEY_CODEBOOK_CACHE_FOLDER = 5;
    private final int KEY_OUTPUT_FILE = 6;
    private final int KEY_WORKER_COUNT = 7;

    // Different quantization methods.
    public static final String SQ = "Scalar";
    public static final String VQ1D = "Row Vector";
    public static final String VQ2D = "Matrix Vector";

    // Different codebook types.
    public static final String Individual = "Individual";
    public static final String MiddlePlane = "Middle plane";
    public static final String Global = "Global";

    private final CustomDialog dialog;
    private final CompressionOptions options;

    public CompressionDialog(final String title) {
        dialog = new CustomDialog(title);
        options = new CompressionOptions();
        buildDialog();
    }

    public CompressionDialog(final String title, final ImageInfo imageInfo) {

        dialog = new CustomDialog(title);
        options = new CompressionOptions();
        if (imageInfo != null) {
            dialog.addInfoField("Image:", imageInfo.getImageName())
                    .addInfoField("Image dimensions:", imageInfo.getDimensionString())
                    .addInfoField("Image bit depth:", imageInfo.getBitDepth());
        }
        buildDialog();
    }

    public boolean exec() {
        return dialog.exec();
    }

    public CompressionOptions getChosenOptions() {
        final String selectedQT = ((Choice) dialog.getComponent(KEY_QUANTIZATION_TYPE)).getSelectedItem();
        switch (selectedQT) {
            case SQ:
                options.setQuantizationType(QuantizationType.Scalar);
                break;
            case VQ1D:
                options.setQuantizationType(QuantizationType.Vector1D);
                break;
            case VQ2D:
                options.setQuantizationType(QuantizationType.Vector2D);
                break;
            default:
                options.setQuantizationType(QuantizationType.Invalid);
                break;
        }

        final String selectedCBT = ((Choice) dialog.getComponent(KEY_CODEBOOK_TYPE)).getSelectedItem();
        switch (selectedCBT) {
            case Individual:
                options.setCodebookType(CompressionOptions.CodebookType.Individual);
                break;
            case MiddlePlane:
                options.setCodebookType(CompressionOptions.CodebookType.MiddlePlane);
                break;
            case Global:
                options.setCodebookType(CompressionOptions.CodebookType.Global);
                break;
        }

        final String selectedCBS = ((Choice) dialog.getComponent(KEY_CODEBOOK_SIZE)).getSelectedItem();
        options.setBitsPerCodebookIndex((int) (Math.log(Integer.parseInt(selectedCBS)) / Math.log(2)));

        final String selectedVD = ((TextField) dialog.getComponent(KEY_VECTOR_DIM)).getText();
        final int vecDim = Integer.parseInt(selectedVD);
        switch (options.getQuantizationType()) {
            case Vector1D:
                options.setVectorDimension(new V2i(vecDim, 1));
                break;
            case Vector2D:
                options.setVectorDimension(new V2i(vecDim, vecDim));
        }

        final String selectedCacheFolder = ((TextField) dialog.getComponent(KEY_CODEBOOK_CACHE_FOLDER)).getText();
        options.setCodebookCacheFolder(selectedCacheFolder);

        final String selectedOF = ((TextField) dialog.getComponent(KEY_OUTPUT_FILE)).getText();
        options.setOutputFilePath(selectedOF);

        int workerCount = Integer.parseInt(((TextField) dialog.getComponent(KEY_WORKER_COUNT)).getText());
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (workerCount < 1) workerCount = 1;
        else if (workerCount > availableProcessors) workerCount = availableProcessors;
        options.setWorkerCount(workerCount);

        return options;
    }

    private void buildDialog() {
        final int defaultThreadCount = Runtime.getRuntime().availableProcessors() / 2;
        dialog.addComboBox("Quantization method", new String[]{SQ, VQ1D, VQ2D}, VQ2D, KEY_QUANTIZATION_TYPE)
                .addComboBox("Codebook type", new String[]{Individual, MiddlePlane, Global}, Global, KEY_CODEBOOK_TYPE)
                .addComboBox("Quantization codebook size",
                        new String[]{"4", "8", "16", "32", "64", "128", "256"}, "128", KEY_CODEBOOK_SIZE)
                .addIntegerField("Vector dimension", 3, KEY_VECTOR_DIM)
                .addOpenFolderField("Codebook cache folder", KEY_CODEBOOK_CACHE_FOLDER)
                .addSaveFileField("Compressed file", "qcmp", KEY_OUTPUT_FILE)
                .addIntegerField("Thread count", defaultThreadCount, KEY_WORKER_COUNT);

        // Set default QCMP cache folder.
        ((TextField) dialog.getComponent(KEY_CODEBOOK_CACHE_FOLDER)).setText(QcmpCacheHelper.getQcmpCacheDirectory());

        dialog.setValidator(model -> {
            try {
                final int vecWidth = Integer.parseInt(((TextField) model.getComponent(KEY_VECTOR_DIM)).getText());
            } catch (NumberFormatException e) {
                IJ.showMessage("Invalid vector dimensions.");
                return false;
            }
            return true;
        });
    }
}
