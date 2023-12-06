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

public class LampPost {
    
  private Camera camera;
  private Light[] lights;

  private Model head_sphere, body_sphere;
  private TransformNode LampPostMoveTranslate;
  private SGNode LampPostRoot;
  //set lamp post x coordinate position
  private float xPosition = -4f;


  public LampPost(GL3 gl, Camera cameraIn, Light[] lightsIn, Texture t1) {
      this.camera = cameraIn;
      this.lights = lightsIn;

      body_sphere = makeSphere(gl, t1);
      head_sphere = makeSphere(gl, t1);

      float bodyRadius = 8.0f/2;
      float bodyScaleX = 0.2f;
      float bodyScaleY = 8f;
      float bodyScaleZ = 0.2f;
      float headScaleX = 2f;
      float headScaleY = 0.5f;
      float headScaleZ = 1f;


      LampPostRoot = new NameNode("root");
      LampPostMoveTranslate = new TransformNode("move",Mat4Transform.translate(xPosition,bodyRadius,0));
      TransformNode LampPostTranslate = new TransformNode("LampPost transform",Mat4Transform.translate(0,0,0));
      
      NameNode body = new NameNode("body"); 
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        m = Mat4.multiply(m, Mat4Transform.scale(bodyScaleX,bodyScaleY,bodyScaleZ));
        TransformNode bodyTransform = new TransformNode("LampPost body", m);
          ModelNode bodyShape = new ModelNode("Sphere(body)", body_sphere);

      NameNode head = new NameNode("head"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(1f,bodyRadius,0));
        m = Mat4.multiply(m, Mat4Transform.scale(headScaleX,headScaleY,headScaleZ));
        TransformNode headTransform = new TransformNode("LampPost head", m);
          ModelNode headShape = new ModelNode("Sphere(head)", head_sphere);

      LampPostRoot.addChild(LampPostMoveTranslate);
        LampPostMoveTranslate.addChild(LampPostTranslate);
          LampPostTranslate.addChild(body);
              body.addChild(bodyTransform);
                bodyTransform.addChild(bodyShape);
              body.addChild(head);
                head.addChild(headTransform);
                  headTransform.addChild(headShape);
      LampPostRoot.update();

  }

  private Model makeSphere(GL3 gl, Texture t1) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_m_1t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, lights, camera, t1);
    return sphere;
  } 

  public void render(GL3 gl) {
    LampPostRoot.draw(gl);
  } 

  public void dispose(GL3 gl) {
    body_sphere.dispose(gl);
    head_sphere.dispose(gl);
  } 
} 
