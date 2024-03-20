package jade;

public abstract class Component {
  public GameObject gameObject;
  public abstract void start();
  public abstract void update(float dt);
}
