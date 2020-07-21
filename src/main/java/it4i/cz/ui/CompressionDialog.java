package it4i.cz.ui;

import azgracompress.compression.CompressionOptions;
import azgracompress.data.V2i;
import ij.IJ;

import java.awt.*;

public class CompressionDialog {

    private final int KEY_QUANTIZATION_TYPE = 1;
    private final int KEY_CODEBOOK_TYPE = 2;
    private final int KEY_CODEBOOK_SIZE = 3;
    private final int KEY_VECTOR_WIDTH = 4;
    private final int KEY_VECTOR_HEIGHT = 5;
    private final int KEY_CODEBOOK_CACHE_FOLDER = 6;
    private final int KEY_OUTPUT_FILE = 7;

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

    public CompressionDialog(String title) {
        dialog = new CustomDialog(title);
        buildDialog();
        options = new CompressionOptions();
    }

    public boolean exec() {
        return dialog.exec();
    }

    public CompressionOptions getChosenOptions() {
        // TODO(Moravec): Set rest of the options.
        return options;
    }

    private void buildDialog() {
        dialog.addComboBox("Quantization method", new String[]{SQ, VQ1D, VQ2D}, VQ2D, KEY_QUANTIZATION_TYPE)
                .addComboBox("Codebook type", new String[]{Individual, MiddlePlane, Global}, Global, KEY_CODEBOOK_TYPE)
                .addComboBox("Quantization codebook size",
                             new String[]{"4", "8", "16", "32", "64", "128", "256"}, "128", KEY_CODEBOOK_SIZE)
                .addIntegerField("Vector width", 3, KEY_VECTOR_WIDTH)
                .addIntegerField("Vector height", 3, KEY_VECTOR_HEIGHT)
                .addOpenFolderField("Codebook cache folder", KEY_CODEBOOK_CACHE_FOLDER)
                .addSaveFileField("Compressed file", KEY_OUTPUT_FILE);

        dialog.setValidator(model -> {
            try {
                final int vecWidth = Integer.parseInt(((TextField) model.getComponent(KEY_VECTOR_WIDTH)).getText());
                final int vecHeight = Integer.parseInt(((TextField) model.getComponent(KEY_VECTOR_HEIGHT)).getText());
                options.setVectorDimension(new V2i(vecWidth, vecHeight));
            } catch (NumberFormatException e) {
                IJ.showMessage("Invalid vector dimensions.");
                return false;
            }
            return true;
        });
    }
}
