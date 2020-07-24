/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package it4i.cz;

import azgracompress.compression.CompressionOptions;
import azgracompress.compression.ImageCompressor;
import azgracompress.data.V3i;
import azgracompress.io.BufferInputData;
import azgracompress.io.InputData;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.process.ShortProcessor;
import it4i.cz.ui.CompressionDialog;
import it4i.cz.ui.ImageInfo;
import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.management.openmbean.OpenMBeanConstructorInfo;


// TODO(Moravec): Place plugin inside Plugins menu.
//@Plugin(type = Command.class, menuPath = "Plugins>Compression>QCMP Compression")
@Plugin(type = Command.class, menuPath = "Compression>QCMP Compression")
public class QCMPCompressCommand implements Command {

    // Different quantization methods.
    private static final String SQ = "Scalar";
    private static final String VQ1D = "Row Vector";
    private static final String VQ2D = "Matrix Vector";

    // Different codebook types.
    private static final String Individual = "Individual";
    private static final String MiddlePlane = "Middle plane";
    private static final String Global = "Global";

    //    @Parameter(label = "Codebook cache", required = false, validater = "/d")
    //    private String codebookCacheDirectory;

    /**
     * Injected parameters.
     */
    @Parameter
    private LogService logger;


    @Override
    public void run() {
        boolean runDecompressOnTmpFile = false;
        String tmpFile = null;

        final ImagePlus currentImage = WindowManager.getCurrentImage();
        if (currentImage == null) {
            IJ.showMessage("No image is opened.");
            return;
        }
        if (currentImage.getType() != ImagePlus.GRAY16) {
            IJ.showMessage("Only 16 bit images are currently supported.");
            return;
        }

        final int stackSize = currentImage.getImageStackSize();
        final ImageStack imageStack = currentImage.getImageStack();
        assert (currentImage.getNSlices() == stackSize);
        final V3i datasetDims = new V3i(currentImage.getWidth(), currentImage.getHeight(), currentImage.getImageStackSize());

        final ImageInfo imageInfo = new ImageInfo(currentImage.getTitle(),
                datasetDims.toString(),
                Integer.toString(currentImage.getBitDepth()));
        CompressionDialog dialog = new CompressionDialog("QCMP compression", imageInfo);
        if (dialog.exec()) {

            final CompressionOptions options = dialog.getChosenOptions();
            final Object[] pixelBuffers = new Object[stackSize];
            for (int planeIndex = 0; planeIndex < stackSize; planeIndex++) {
                final Object planePixelBuffer = imageStack.getPixels(planeIndex + 1);
                assert (planePixelBuffer != null);
                pixelBuffers[planeIndex] = planePixelBuffer;
            }

            if (options.getOutputFilePath() == null || options.getOutputFilePath().isEmpty()) {
                tmpFile = getTmpFile();
                if (tmpFile == null) {
                    IJ.showMessage("No output file was defined, unable to create temporary file.");
                    return;
                }
                options.setOutputFilePath(tmpFile);
                runDecompressOnTmpFile = true;
            }

            options.setInputDataInfo(new BufferInputData(pixelBuffers,
                    datasetDims,
                    InputData.PixelType.Gray16,
                    currentImage.getOriginalFileInfo().fileName));

            ImageCompressor imageCompressor = new ImageCompressor(options);

            imageCompressor.setStatusListener(statusMessage -> DefaultListeners.handleStatusReport(logger, statusMessage));
            imageCompressor.setProgressListener((message, index, finalIndex) ->
                    DefaultListeners.handleProgressReport(logger, message, index, finalIndex));

            if (imageCompressor.compress()) {
                if (!runDecompressOnTmpFile)
                    IJ.showMessage("Compressed file is saved at: " + options.getOutputFilePath());
            } else {
                IJ.showMessage("Failed to compress the image. Check error log.");
            }

            if (runDecompressOnTmpFile) {
                // Decompress and show the file!
                new QCMPDecompressCommand().openQCMPFile(tmpFile);
            }
        }
    }

    private String getTmpFile() {
        try {
            return File.createTempFile("qcmp_temp_file", FileExtensions.QCMP).getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }
}
