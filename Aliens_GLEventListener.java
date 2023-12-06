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
    alien1.dispose(gl);
    alien2.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean rock1 = true;
  private boolean rock2 = true;
  private boolean roll1 = true;
  private boolean roll2 = true;
  private double savedTime = 0;
   
  public void rock1Animation() {
    rock1 = !rock1;
  }
   
  public void rock2Animation() {
    rock2 = !rock2;
  }

  public void roll1Animation() {
    roll1 = !roll1;
  }
   
  public void roll2Animation() {
    roll2 = !roll2;
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
  private Alien alien1;
  private Alien alien2;

  private void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "background", "textures/snow_background.jpg");
    textures.add(gl, "snowfall", "textures/snowfall_black.jpg");
    textures.add(gl, "texture1", "textures/jade.jpg");
    textures.add(gl, "texture1_spec", "textures/jade_specular.jpg");
    textures.add(gl, "texture2", "textures/ear0xuu2.jpg");
    textures.add(gl, "texture2_spec", "textures/ear0xuu2_specular.jpg");
    textures.add(gl, "texture3", "textures/jup0vss1.jpg");
    textures.add(gl, "texture3_spec", "textures/jup0vss1_specular.jpg");
    textures.add(gl, "quicksand", "textures/quicksand.jpg");

    light = new Light(gl);
    light.setCamera(camera);
    light.setPosition(new Vec3(-3f,5f,0f));
    
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
    
    float posX1 = -1f;
    float posX2 = 3f;
    alien1 = new Alien(gl, camera, light, posX1, textures.get("texture1"), textures.get("texture1_spec"), textures.get("texture2"), textures.get("texture2_spec"), textures.get("texture3"), textures.get("texture3_spec"));
    alien2 = new Alien(gl, camera, light, posX2, textures.get("quicksand"), textures.get("texture3_spec"), textures.get("texture1"), textures.get("texture1_spec"), textures.get("texture2"), textures.get("texture2_spec"));
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    // light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.setModelMatrix(getMforFloor());
    floor.render(gl); 
    background.setModelMatrix(getMforBackground());
    background.render(gl);
    if (rock1) {
      alien1.alienRock();
    } else {
      alien1.stopRock();
    }
    if (rock2) {
      alien2.alienRock();
    } else {
      alien2.stopRock();
    }
    if (roll1) {
      alien1.alienRoll();
    }else {
      alien1.stopRoll();
    }
    if (roll2) {
      alien2.alienRoll();
    }else {
      alien2.stopRoll();
    }
    alien1.render(gl);
    alien2.render(gl);
  }

  // // The light's postion is continually being changed, so needs to be calculated for each frame.
  // private Vec3 getLightPosition() {
  //   double elapsedTime = getSeconds()-startTime;
  //   float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
  //   float y = 2.7f;
  //   float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
  //   return new Vec3(x,y,z);   
  //   //return new Vec3(5f,3.4f,5f);
  // }

  
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