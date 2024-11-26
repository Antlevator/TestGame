package org.example.util;

import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureUtil {

    public static int loadTexture(String filepath) {
        try (MemoryStack stack = stackPush()) {

            // Load the image from the resources folder as an InputStream
            InputStream is = TextureUtil.class.getResourceAsStream(filepath);
            if (is == null) {
                throw new RuntimeException("Failed to load image: " + filepath);
            }

            // Convert the InputStream to a ByteBuffer
            ByteBuffer imageBuffer = null;
            try {
                imageBuffer = readInputStreamToByteBuffer(is);
            } catch(IOException e) {
                throw new RuntimeException("Failed to read image data.", e);
            }
            if (imageBuffer == null) {
                throw new RuntimeException("Failed to read image data.");
            }

            // Decode the image data
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Decode the image data into width, height, channels
            ByteBuffer image = stbi_load_from_memory(imageBuffer, width, height, channels, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load image data from memory.");
            }

            // Generate OpenGL texture
            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            // Free the image
            stbi_image_free(image);

            return textureID;
        }
    }

    // Helper method to convert InputStream to ByteBuffer
    private static ByteBuffer readInputStreamToByteBuffer(InputStream inputStream) throws IOException {
        // Read the entire InputStream into a byte array
        byte[] bytes = inputStream.readAllBytes();

        // Create a ByteBuffer from the byte array
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        buffer.flip();  // Set the buffer position to 0

        return buffer;
    }
}
