package it4i.cz.ui;

public class ImageInfo {
    private final String imageName;
    private final String dimensionString;
    private final String BitDepth;

    public ImageInfo(String imageName, String dimensionString, String bitDepth) {
        this.imageName = imageName;
        this.dimensionString = dimensionString;
        BitDepth = bitDepth;
    }

    public String getImageName() {
        return imageName;
    }

    public String getDimensionString() {
        return dimensionString;
    }

    public String getBitDepth() {
        return BitDepth;
    }
}
