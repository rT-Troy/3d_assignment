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
    this.camera.setPosition(new Vec3(0f,15f,18f));
    this.camera.setTarget(new Vec3(0f, 5f, 0f));
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
    lights[0].dispose(gl);
    lights[1].dispose(gl);
    lights[2].dispose(gl);
    // genlight1.dispose(gl);
    // genlight2.dispose(gl);
    floor.dispose(gl);
    alien1.dispose(gl);
    alien2.dispose(gl);
    lamPost.dispose(gl);
  }
  
  // ***************************************************
  /* INTERACTION
   *  achieve rock and roll for each alien independently
   *  achieve each light switch independently
   */
  // set default status
  private boolean rock1 = true;
  private boolean rock2 = true;
  private boolean roll1 = true;
  private boolean roll2 = true;
  private boolean light1 = false;
  private boolean light2 = false;
  private boolean spotlight = false;
   
  public void rock1Animation() {rock1 = !rock1;}
  public void rock2Animation() {rock2 = !rock2;}

  public void roll1Animation() {roll1 = !roll1;}
  public void roll2Animation() {roll2 = !roll2;}

  public void light1Swith() {light1 = !light1;}
  public void light2Swith() {light2 = !light2;}
  public void spotLightSwith() {spotlight = !spotlight;}
  
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

  private Light[] lights = new Light[3];  //light list
  private Alien alien1;
  private Alien alien2;
  private LampPost lamPost;

  private void initialise(GL3 gl) {

    textures = new TextureLibrary();
    textures.add(gl, "background", "textures/snow_background.jpg");
    textures.add(gl, "snowfall", "textures/snowfall.jpg");
    textures.add(gl, "texture1", "textures/jade.jpg");
    textures.add(gl, "texture1_spec", "textures/jade_specular.jpg");
    textures.add(gl, "texture2", "textures/ear0xuu2.jpg");
    textures.add(gl, "texture2_spec", "textures/ear0xuu2_specular.jpg");
    textures.add(gl, "texture3", "textures/jup0vss1.jpg");
    textures.add(gl, "texture3_spec", "textures/jup0vss1_specular.jpg");
    textures.add(gl, "quicksand", "textures/quicksand.jpg");

    //Build lights with position and identify if it is spotlight or general light
    lights[0] = new Light(gl,new Vec3(-3f,7.8f,0f), true);
    lights[0].setCamera(camera);
    lights[1] = new Light(gl,new Vec3(5f,4f,-5f), false);
    lights[1].setCamera(camera);
    lights[2] = new Light(gl,new Vec3(0f,6f,-2f),false);
    lights[2].setCamera(camera);

    // floor
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices_floor.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_m_1t.txt");
    Material material = new Material(new Vec3(0f, 0.2f, 0.2f), new Vec3(0.0f, 0.25f, 0.5f), new Vec3(0.25f, 0.25f, 0.25f), 16.0f);
    floor = new Model(name, mesh, new Mat4(1), shader, material, lights, camera, textures.get("background"));

    // background: snow scene with snowfall
    name = "background";
    mesh = new Mesh(gl, TwoTriangles.vertices_background.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "shaders/vs_background.txt", "shaders/fs_background.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 16.0f); 
    background = new Model(name, mesh, new Mat4(1), shader, material, lights, camera, textures.get("background"), textures.get("snowfall"));
    
    // preset the x position of alien
    float posX1 = -1f;
    float posX2 = 3f;
    alien1 = new Alien(gl, camera, lights, posX1, textures.get("texture1"), textures.get("texture1_spec"), textures.get("texture2"), textures.get("texture2_spec"), textures.get("texture3"), textures.get("texture3_spec"));
    alien2 = new Alien(gl, camera, lights, posX2, textures.get("quicksand"), textures.get("texture3_spec"), textures.get("texture1"), textures.get("texture1_spec"), textures.get("texture2"), textures.get("texture2_spec"));
    
    lamPost = new LampPost(gl, camera, lights, textures.get("background"), textures.get("snowfall"));
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    floor.setModelMatrix(getMforFloor());
    floor.render(gl); 
    background.setModelMatrix(getMforBackground());
    background.render(gl);
    // detect the button status
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
    if (light1) {
      lights[1].turnOff();
    } else {
      lights[1].turnOn();
    }
    if (light2) {
      lights[2].turnOff();
    } else {
      lights[2].turnOn();
    }
    if (spotlight) {
      lights[0].turnOff();
    } else {
      lights[0].turnOn();
    }
    alien1.render(gl);
    alien2.render(gl);
    lamPost.render(gl);
    lights[0].render(gl);
    lights[1].render(gl);
    lights[2].render(gl);
  }

//************************************
  // match each plane at the join
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