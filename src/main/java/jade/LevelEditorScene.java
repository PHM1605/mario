package jade;

import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

  private float[] vertexArray ={
      // position        // color                    // UV coordinate
      200f, 0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,    1, 1,// Bottom right red
      0f, 200f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,      0, 0,// Top left green
      200f, 200f, 0.0f,  0.0f, 0.0f, 1.0f, 1.0f, 1, 0, // Top right blue
      0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,      0, 1 // Bottom left yellow
  };

  // counter-clockwise order
  private int[] elementArray = {
      2, 1, 0, // top right triangle
      0, 1, 3
  };

  private int vaoID, vboID, eboID;
  private Shader defaultShader;
  private Texture testTexture;
  GameObject testObj;
  private boolean firstTime = true;

  public LevelEditorScene() {
  }

  @Override
  public void init() {
    System.out.println("Creating 'test object");
    this.testObj = new GameObject("test object");
    this.testObj.addComponent(new SpriteRenderer());
    this.testObj.addComponent(new FontRenderer());
    this.addGameObjectToScene(testObj);

    this.camera = new Camera(new Vector2f(-200, -300));
    defaultShader = new Shader("assets/shaders/default.glsl");
    defaultShader.compile();
    this.testTexture = new Texture("assets/images/testImage.png");

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
    int uvSize = 2;
    int vertexSizeBytes = (positionsSize + colorSize +uvSize) * Float.BYTES;
    // location 0 in glsl file; last 2 are "how many bytes to find the next" and "offset size"
    glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize* Float.BYTES);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize+colorSize)*Float.BYTES);
    glEnableVertexAttribArray(2);
  }

  @Override
  public void update(float dt) {
    defaultShader.use();
    // Upload texture to shader
    defaultShader.uploadTexture("TEX_SAMPLER", 0);
    glActiveTexture(GL_TEXTURE0);
    testTexture.bind();

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

    if(firstTime) {
      System.out.println("Creating gameObject!");
      GameObject go = new GameObject("Game Test 2");
      go.addComponent(new SpriteRenderer());
      this.addGameObjectToScene(go);
      firstTime = false;
    }


    for (GameObject go: this.gameObjects) {
      go.update(dt);
    }
  }

}


