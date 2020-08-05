package it4i.cz;

import azgracompress.data.ImageU16Dataset;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;

abstract class ImageStackHelper {

    public static void displayDataset(final ImageU16Dataset dataset, final String name) {


        final ImagePlus img = NewImage.createShortImage(name,
                                                        dataset.getPlaneDimensions().getX(),
                                                        dataset.getPlaneDimensions().getY(),
                                                        dataset.getPlaneCount(),
                                                        ImagePlus.GRAY16);

        final ImageStack imageStack = img.getImageStack();
        for (int planeIndex = 0; planeIndex < dataset.getPlaneCount(); planeIndex++) {
            imageStack.setPixels(dataset.getPlaneData(planeIndex), planeIndex + 1);
        }

        img.show();
    }
}
