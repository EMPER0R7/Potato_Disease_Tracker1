package com.example.potatodiseasetracker;
import android.graphics.Bitmap;
import android.graphics.Color;


public class ImageValidator {

    private static final int GREEN_THRESHOLD = 100;


    public static boolean isPotatoLeaf(Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Counter for total green intensity
        int totalGreenIntensity = 0;

        // Iterate over each pixel in the bitmap
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the color of the pixel
                int pixel = bitmap.getPixel(x, y);

                // Extract green intensity (0-255)
                int greenIntensity = (pixel >> 8) & 0xFF;

                // Accumulate total green intensity
                totalGreenIntensity += greenIntensity;
            }
        }

        // Calculate average green intensity
        int averageGreenIntensity = totalGreenIntensity / (width * height);

        // Check if the average green intensity exceeds the threshold
        return averageGreenIntensity >= GREEN_THRESHOLD;
    }
    }

