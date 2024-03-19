package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import util.Time;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

  private float[] vertexArray ={
      // position        // color
      100.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Bottom right red
      0.5f, 100f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Top left green
      100.5f, 100.5f, 0.0f,  0.0f, 0.0f, 1.0f, 1.0f, // Top right blue
      0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f // Bottom left yellow
  };

  // counter-clockwise order
  private int[] elementArray = {
      2, 1, 0, // top right triangle
      0, 1, 3
  };

  private int vaoID, vboID, eboID;
  private Shader defaultShader;

  public LevelEditorScene() {
  }

  @Override
  public void init() {
    this.camera = new Camera(new Vector2f(0, 0));
    defaultShader = new Shader("assets/shaders/default.glsl");
    defaultShader.compile();

    // Generate VAO
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);
    // Generate VBO
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
    vertexBuffer.put(vertexArray).flip();
    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
    // Generate EBO
    IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
    elementBuffer.put(elementArray).flip();
    eboID = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    // Add the vertex attribute pointers (which location meaning position, which location meaning color...)
    int positionsSize = 3;
    int colorSize = 4;
    int floatSizeBytes = 4;
    int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
    // location 0 in glsl file; last 2 are "how many bytes to find the next" and "offset size"
    glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize*floatSizeBytes);
    glEnableVertexAttribArray(1);
  }

  @Override
  public void update(float dt) {
    camera.position.x -= dt * 50.0f;
    defaultShader.use();
    defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
    defaultShader.uploadMat4f("uView", camera.getViewMatrix());
    defaultShader.uploadFloat("uTime", Time.getTime());
    glBindVertexArray(vaoID);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
    // Unbinding
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);

    defaultShader.detach();
  }

}


