import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;
  
public class Aliens_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Aliens_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(0f,25f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    robot.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean animation = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }
   
  public void incXPosition() {
    robot.incXPosition();
  }
   
  public void decXPosition() {
    robot.decXPosition();
  }
  
  // public void loweredArms() {
  //   stopAnimation();
  //   robot.loweredArms();
  // }
   
  // public void raisedArms() {
  //   stopAnimation();
  //   robot.raisedArms();
  // }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  // textures
  private TextureLibrary textures;

  private Camera camera;
  private Mat4 perspective;
  private Model floor;
  private Model background;
  private Light light;
  //private SGNode robotRoot;
  
  private Robot robot;
  private Alien alien;

  private void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "background", "textures/snow_background.jpg");
    textures.add(gl, "snowfall", "textures/snowfall_black.jpg");
    textures.add(gl, "alien_head1", "textures/alien_head1.jpg");
    textures.add(gl, "alien_body1", "textures/alien_body1.jpg");

    
    light = new Light(gl);
    light.setCamera(camera);
    
    // floor
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices_floor.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_standard.txt", "fs_standard_1t.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 16.0f);
    floor = new Model(name, mesh, new Mat4(1), shader, material, light, camera, textures.get("background"));

    name = "background";
    mesh = new Mesh(gl, TwoTriangles.vertices_background.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_background.txt", "fs_background.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 16.0f); 
    // diffuse texture only for this model
    background = new Model(name, mesh, new Mat4(1), shader, material, light, camera, textures.get("background"), textures.get("snowfall"));
    
    //robot = new Robot(gl, camera, light, 
    //                  textures.get("jade_diffuse"), textures.get("jade_specular"),
    //                  textures.get("container_diffuse"), textures.get("container_specular"),
    //                  textures.get("watt_diffuse"), textures.get("watt_specular")); 
    alien = new Alien(gl, camera, light, textures.get("alien_head1"), textures.get("alien_body1"));
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.setModelMatrix(getMforFloor());
    floor.render(gl); 
    background.setModelMatrix(getMforBackground());
    background.render(gl);
    if (animation) {
      double elapsedTime = getSeconds()-startTime;
      robot.updateAnimation(elapsedTime);
    }
    //robot.render(gl);
    alien.render(gl);
  }

  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }

  // combine two planes
  private float SIDE_LENGTH = 12f;
  private Mat4 getMforFloor() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(SIDE_LENGTH,1f,SIDE_LENGTH), modelMatrix);
    return modelMatrix;
}

  private Mat4 getMforBackground() {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(SIDE_LENGTH,1f,SIDE_LENGTH), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,SIDE_LENGTH*0.5f,-SIDE_LENGTH*0.5f), modelMatrix);
    return modelMatrix;
  }
  
}