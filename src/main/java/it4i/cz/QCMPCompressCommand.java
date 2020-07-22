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
import ij.WindowManager;
import ij.process.ShortProcessor;
import it4i.cz.ui.CompressionDialog;
import it4i.cz.ui.ImageInfo;
import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

/**
 * This example illustrates how to create an ImageJ {@link Command} plugin.
 * <p>
 * The code here is a simple Gaussian blur using ImageJ Ops.
 * </p>
 * <p>
 * You should replace the parameter fields with your own inputs and outputs,
 * and replace the {@link `run`} method implementation with your own logic.
 * </p>
 */

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

    @Parameter
    private UIService uiService;


    @Override
    public void run() {
        ImagePlus currentImage = WindowManager.getCurrentImage();
        if (currentImage == null) {
            IJ.showMessage("No image is opened.");
            return;
        }

        if (currentImage.getType() != ImagePlus.GRAY16) {
            IJ.showMessage("Only 16 bit images are currently supported.");
            return;
        }

        logger.info("Current image file: " + currentImage.getFileInfo().fileName);

        final V3i imageDims = new V3i(currentImage.getWidth(), currentImage.getHeight(), currentImage.getNSlices());

        final ImageInfo imageInfo = new ImageInfo(currentImage.getTitle(),
                imageDims.toString(),
                Integer.toString(currentImage.getBitDepth()));
        CompressionDialog dialog = new CompressionDialog("QCMP compression", imageInfo);
        if (dialog.exec()) {

            final CompressionOptions options = dialog.getChosenOptions();
            final ShortProcessor imageProcessor = (ShortProcessor) currentImage.getProcessor();
            options.setInputDataInfo(new BufferInputData(imageProcessor.getPixels(), imageDims, InputData.PixelType.Gray16));

            ImageCompressor imageCompressor = new ImageCompressor(options);
            if (imageCompressor.compress()) {
                IJ.showMessage("Compressed file is saved at: " + options.getOutputFilePath());
            } else {
                IJ.showMessage("Failed to compress the image. Check error log.");
            }

            System.out.println("options are prepared.");
        }


        //        // display result
        //        for (RandomAccessibleInterval<T> elem : results) {
        //            uiService.show(elem);
        //        }
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


        // ask the user for a file to open
        //        final File file = ij.ui().chooseFile(null, "open");
        //
        //        if (file != null) {
        //            // load the dataset
        //            final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());
        //
        //            // show the image
        //            ij.ui().show(dataset);
        //
        //            // invoke the plugin
        //            ij.command().run(QCMPCompressCommand.class, true);
        //        }
    }
}
