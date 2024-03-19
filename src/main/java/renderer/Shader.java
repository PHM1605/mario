package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

  private int shaderProgramID;
  private boolean beingUsed = false;
  private String vertexSource;
  private String fragmentSouce;
  private String filepath;

  public Shader(String filepath) {
    this.filepath = filepath;

    try {
      String source = new String(Files.readAllBytes(Paths.get(filepath)));
      String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
      // index of the next word after the first "#type"
      int index = source.indexOf("#type") + 6;
      int eol = source.indexOf("\r\n", index);
      String firstPattern = source.substring(index, eol).trim();
      // index of the next word after the second "#type"
      index = source.indexOf("#type", eol) + 6;
      eol = source.indexOf("\r\n", index);
      String secondPattern = source.substring(index, eol).trim();

      if (firstPattern.equals("vertex")) {
        vertexSource = splitString[1];
      } else if (firstPattern.equals("fragment")) {
        fragmentSouce = splitString[1];
      } else {
        throw new IOException("Unexpected token '" + firstPattern + "' in '" + filepath + "'");
      }

      if (secondPattern.equals("vertex")) {
        vertexSource = splitString[2];
      } else if (secondPattern.equals("fragment")) {
        fragmentSouce = splitString[2];
      } else {
        throw new IOException("Unexpected token '" + secondPattern + "' in '" + filepath + "'");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public void compile() {
    int vertexID, fragmentID;
    // Compile vertex shaders
    vertexID = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexID, vertexSource);
    glCompileShader(vertexID);
    // Check for errors in compilation
    int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
      System.out.println(glGetShaderInfoLog(vertexID, len));
      assert false: "";
    }

    // Compile fragment shaders
    fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentID, fragmentSouce);
    glCompileShader(fragmentID);
    // Check for errors in compilation
    success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
      System.out.println(glGetShaderInfoLog(fragmentID, len));
      assert false: "";
    }

    // Link shaders and check for errors
    shaderProgramID = glCreateProgram();
    glAttachShader(shaderProgramID, vertexID);
    glAttachShader(shaderProgramID, fragmentID);
    glLinkProgram(shaderProgramID);

    success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(shaderProgramID, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filepath + "'\n\tLinking shaders failed.");
      System.out.println(glGetProgramInfoLog(shaderProgramID, len));
      assert false: "";
    }
  }

  public void use() {
    if (!beingUsed) {
      // Binding
      glUseProgram(shaderProgramID);
      beingUsed = true;
    }

  }

  public void detach() {
    glUseProgram(0);
    beingUsed = false;
  }

  public void uploadMat3f(String varName, Matrix3f mat3) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    use(); // make sure shader is in used before buffering
    FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
    // Stash mat4 into the buffer
    mat3.get(matBuffer);
    glUniformMatrix3fv(varLocation, false, matBuffer);
  }

  public void uploadMat4f(String varName, Matrix4f mat4) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    use(); // make sure shader is in used before buffering
    FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
    // Stash mat4 into the buffer
    mat4.get(matBuffer);
    glUniformMatrix4fv(varLocation, false, matBuffer);
  }

  public void uploadVec2f(String varName, Vector2f vec) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    use();
    glUniform2f(varLocation, vec.x, vec.y);
  }

  public void uploadVec3f(String varName, Vector3f vec) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    use();
    glUniform3f(varLocation, vec.x, vec.y, vec.z);
  }

  public void uploadVec4f(String varName, Vector4f vec) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    use();
    glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
  }

  public void uploadFloat(String varName, float val) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    use();
    glUniform1f(varLocation, val);
  }

  public void uploadInt(String varName, int val) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    use();
    glUniform1i(varLocation, val);
  }
}
