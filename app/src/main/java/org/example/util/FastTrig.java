package org.example.util;

public class FastTrig {

    private FastTrig() {}

    public static final float PI = (float) Math.PI;  // Explicitly cast Math.PI to float
    public static final float TWO_PI = (float) (2 * Math.PI);  // Explicitly cast Math.PI to float

    // Number of entries in the lookup table (corresponding to 0 to 2*pi radians).
    private static final int TABLE_SIZE = 628; // 2*pi / 0.01
    private static final float[] sinTable = new float[TABLE_SIZE];

    // Populate the sine table
    static {
        for (int i = 0; i < TABLE_SIZE; i++) {
            float angle = i * 0.01f; // Angle in radians
            sinTable[i] = (float) Math.sin(angle); // Explicitly cast result to float
        }
    }

    // Fast sine approximation using the lookup table
    public static float fastSin(float x) {
        // Normalize the angle to the range [0, 2*pi)
        x = x % TWO_PI;
        if (x < 0) x += TWO_PI; // Ensure it's positive

        // Convert the angle to the index in the table
        int index = (int) (x / 0.01f); // 0.01 is the step size between entries

        // Return the precomputed sine value
        return sinTable[index];
    }

    // Fast cosine approximation using the sine lookup table
    public static float fastCos(float x) {
        // Normalize the angle to the range [0, 2*pi)
        x = x % TWO_PI;
        if (x < 0) x += TWO_PI; // Ensure it's positive

        // Convert the angle to the index in the table for sine
        int index = (int) (x / 0.01f); // 0.01 is the step size between entries

        // To get the cosine, offset by 90 degrees (pi/2 radians)
        int cosIndex = (index + (TABLE_SIZE / 4)) % TABLE_SIZE;

        // Return the precomputed cosine value from the sine table (shifted by pi/2)
        return sinTable[cosIndex];
    }

}
