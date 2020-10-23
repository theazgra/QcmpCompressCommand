package it4i.cz;

import cz.it4i.qcmp.compression.CompressionOptions;
import cz.it4i.qcmp.compression.ImageCompressor;
import cz.it4i.qcmp.data.V3i;
import cz.it4i.qcmp.fileformat.QuantizationType;
import cz.it4i.qcmp.io.FileInputData;
import cz.it4i.qcmp.kdtree.KDTree;
import cz.it4i.qcmp.kdtree.KDTreeBuilder;
import cz.it4i.qcmp.utilities.Stopwatch;
import org.scijava.Priority;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.Arrays;

@Plugin(type = Command.class, menuPath = "Compression>Test", priority = Priority.HIGH_PRIORITY)
public class TestCommand implements Command {

    @Parameter
    LogService logger;

    @Override
    public void run() {
        //DataCompressor.main(new String[]{"-d", "D:\\biology\\tiff_data\\planes100.tif.QCMP"});
        final CompressionOptions co = new CompressionOptions();
        co.setQuantizationVector(new V3i(3));
        co.setCodebookCacheFolder("D:\\biology\\benchmark\\dc_cache_huffman\\");
        co.setCodebookType(CompressionOptions.CodebookType.Global);
        co.setQuantizationType(QuantizationType.Vector3D);
        co.setInputDataInfo(new FileInputData("D:\\biology\\tiff_data\\planes100.raw"));
        co.getInputDataInfo().setDimension(new V3i(1041, 996, 100));
        co.setOutputFilePath("D:\\biology\\tiff_data\\planes100.tif.qcmp");
        co.setVerbose(true);
        co.setWorkerCount(1);


        final ImageCompressor ic = new ImageCompressor(co);
        ic.compress();

        //        ImageDecompressor id = new ImageDecompressor(co);
        //        id.decompressToFile();
    }

    private void testKdTreeBuild(final int[][] vectors, final int bucketSize, final int maxE) {
        final Stopwatch s = Stopwatch.startNew();
        final KDTreeBuilder builder = new KDTreeBuilder(vectors[0].length, bucketSize);
        final KDTree tree = builder.buildTree(vectors);
        s.stop();
        logger.info("=================================================================");
        logger.info("k = : " + vectors[0].length);
        logger.info("Bucket size: " + bucketSize);
        logger.info("Total node count: " + tree.getTotalNodeCount());
        logger.info("Terminal node count: " + tree.getTerminalNodeCount());
        logger.info("Build time: " + s.getElapsedTimeString());
        logger.info("MaxE: " + maxE);

        int passed = 0;
        for (final int[] qVec : vectors) {
            final int searchResult = tree.findNearestBBF(qVec, maxE);
            final boolean result = Arrays.equals(qVec, vectors[searchResult]);
            if (result) {
                ++passed;
            }
        }
        logger.info(String.format("Vectors matched: %d/%d", passed, vectors.length));

        logger.info("=================================================================");
    }
}
