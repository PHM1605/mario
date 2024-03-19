package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
  private Matrix4f projectionMatrix, viewMatrix;
  public Vector2f position;

  public Camera(Vector2f position) {
    this.position = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
    adjustProjection();
  }

  public void adjustProjection() {
    projectionMatrix.identity();
    projectionMatrix.ortho(0.0f, 32.0f*40.0f, 0.0f, 32.0f*21.0f, 0.0f, 100.0f);
  }

  public Matrix4f getViewMatrix() {
    Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
    viewMatrix.identity();
    // cameraLocation, cameraDirection, up
    viewMatrix.lookAt(
        new Vector3f(position.x, position.y, 20.0f),
        new Vector3f(position.x, position.y, -1.0f),
        cameraUp);
    return viewMatrix;
  }

  public Matrix4f getProjectionMatrix() {
    return projectionMatrix;
  }
}
