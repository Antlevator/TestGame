package org.example.util;

import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderUtil {

    public static int createShaderProgram(
        String vertexShaderFilepath,
        String fragmentShaderFilepath
    ) {
        // create shader objects for linking to shader program
        int vertexShaderId = createVertexShader(vertexShaderFilepath);
        int fragmentShaderId = createFragmentShader(fragmentShaderFilepath);

        int shaderProgramId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgramId, vertexShaderId);
        GL20.glAttachShader(shaderProgramId, fragmentShaderId);
        GL20.glLinkProgram(shaderProgramId);
        int linkStatus = GL20.glGetProgrami(shaderProgramId, GL20.GL_LINK_STATUS);
        if(linkStatus == GL20.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(shaderProgramId);
            throw new RuntimeException("Error compiling shader: " + log);
        }

        // we no longer need shaders as independent objects once linked
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);

        return shaderProgramId;
    }

    private static int createVertexShader(String filepath) {
        return createShader(filepath, GL20.GL_VERTEX_SHADER);
    }

    private static int createFragmentShader(String filepath) {
        return createShader(filepath, GL20.GL_FRAGMENT_SHADER);
    }

    private static int createShader(String filepath, int shaderType) {
        // create a new shader in OpenGL context and retrieve id
        int shaderId = GL20.glCreateShader(shaderType);
        // read the GLSL source code from file
        String source = readFileAsString(filepath);
        // set the GLSL source code for this shader
        GL20.glShaderSource(shaderId, source);
        // compile the GLSL source code for this shader
        GL20.glCompileShader(shaderId);
        // get status of GLSL runtime compilation
        int compileStatus = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS);
        // check status, log errors
        if(compileStatus == GL20.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shaderId);
            throw new RuntimeException("Error compiling shader: " + log);
        }
        return shaderId;
    }

    private static String readFileAsString(String filepath) {
        // read the resource file in as a stream
        InputStream is = ShaderUtil.class.getResourceAsStream(filepath);
        if (is == null) {
            throw new RuntimeException("Failed to load shader: " + filepath);
        }
        // build a string containing the contents of the file
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read shader: " + filepath, e);
        }
        return stringBuilder.toString();
    }
}