/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package it4i.cz;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ShortProcessor;
import it4i.cz.ui.CompressionDialog;
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
        CompressionDialog dialog = new CompressionDialog("QCMP compression");
        if (dialog.exec() || true)
            return;
        ImagePlus currentImage = WindowManager.getCurrentImage();
        if (currentImage == null) { // There is no current image, we ask user to open the image.
            IJ.showMessage("No image is opened.");
            return;
        }

        if (currentImage.getType() != ImagePlus.GRAY16) {
            IJ.showMessage("Only 16 bit images are currently supported.");
            return;
        }
        logger.info("Current image file: " + currentImage.getFileInfo().fileName);
        currentImage = currentImage.duplicate();

        final int width = currentImage.getWidth();
        final int height = currentImage.getHeight();
        final int sliceCount = currentImage.getNSlices();
        logger.info("Width: " + width);
        logger.info("Height: " + height);
        logger.info("ZCount: " + sliceCount);
        logger.info("BitDepth: " + currentImage.getBitDepth());
        logger.info("BPP: " + currentImage.getBytesPerPixel());


        final ShortProcessor imageProcessor = (ShortProcessor) currentImage.getProcessor();
        final short[] pixelData = (short[]) imageProcessor.getPixels();


        //        GenericDialog dialog = new GenericDialog("My command-plugin title");
        //        dialog.addChoice("Quantization method", new String[]{SQ, VQ1D, VQ2D}, VQ2D);
        //        dialog.addChoice("Codebook type", new String[]{Individual, MiddlePlane, Global}, Global);
        //        dialog.addChoice("Quantization codebook size", new String[]{"4", "8", "16", "32", "64", "128",
        //        "256"}, "128");
        //        dialog.addNumericField("Vector width", 3, 0);
        //        dialog.addNumericField("Vector height", 3, 0);
        //        dialog.addStringField("Codebook cache folder", "");
        //
        //
        //        Button btn = new Button("my button");
        //        btn.addActionListener(new ActionListener() {
        //            @Override
        //            public void actionPerformed(ActionEvent actionEvent) {
        //                System.out.println("Button was clicked, source --> " + actionEvent.getSource());
        //            }
        //        });
        //
        //        Panel cacheFolderContainer = new Panel();
        //        cacheFolderContainer.add(new TextField());
        //        cacheFolderContainer.add(btn);
        //        cacheFolderContainer.setLayout(new FlowLayout());
        //        dialog.addPanel(cacheFolderContainer);
        //        dialog.showDialog();

        //        dialog.addStringField("Name", "Your name here");
        //        dialog.showDialog();


        //        logger.info("Input file " + inputImageFile.getPath());
        //        logger.info("Quantization method: " + quantizationMethod + "(" + codebookType + " codebook, L = " +
        //        codebookSize + ")");
        //        logger.info(String.format("Vector dimensions [%dx%d]", vectorWidth, vectorHeight));
        //
        //        try {
        //            final Dataset loadedDataset = datasetIOService.open(inputImageFile.getAbsolutePath());
        //
        //            logger.info("Source: " + loadedDataset.getSource());
        //            logger.info("Width: " + loadedDataset.getWidth());
        //            logger.info("Height: " + loadedDataset.getHeight());
        //            logger.info("Depth: " + loadedDataset.getDepth());
        //            logger.info("TypeLabelShort: " + loadedDataset.getTypeLabelShort());
        //            logger.info("TypeLabelLong: " + loadedDataset.getTypeLabelLong());
        //            logger.info("Frames: " + loadedDataset.getFrames());
        //
        //            uiService.show(loadedDataset);
        //
        //        } catch (IOException e) {
        //            logger.error(String.format("Failed to load an image from: '%s'", inputImageFile.getAbsolutePath
        //            ()), e);
        //        }

        //        final Img<T> image = (Img<T>) currentData.getImgPlus();
        //        //
        //        // Enter image processing code here ...
        //        // The following is just a Gauss filtering example
        //        //
        //        final double[] sigmas = {1.0, 3.0, 5.0};
        //
        //        List<RandomAccessibleInterval<T>> results = new ArrayList<>();
        //
        //        for (double sigma : sigmas) {
        //            results.add(opService.filter().gauss(image, sigma));
        //        }
        //
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
