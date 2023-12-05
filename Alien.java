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

public class Alien {
    
  private Camera camera;
  private Light light;

  private Model head_sphere, body_sphere, arm_sphere, eye_sphere, ear_sphere, antenna_sphere;
  private TransformNode translateX, alienMoveTranslate, bodyRotate, headRotate;
  private SGNode alienRoot;
  private float xPosition = 0;
  private float bodyRotateAngleStart = 5, bodyRotateAngle = bodyRotateAngleStart;


  public Alien(GL3 gl, Camera cameraIn, Light lightIn, Texture t1, Texture t2) {
      this.camera = cameraIn;
      this.light = lightIn;
  
      head_sphere = makeSphere(gl, t1,t2);
      body_sphere = makeSphere(gl, t1, t2);

      float bodyScale = 4f;
      float headScale = 3f;
      float bodyHeight = 4.0f;
      float alien1X = 2.5f;
      float alien1Z = 2.5f;

      alienRoot = new NameNode("root");
      alienMoveTranslate = new TransformNode("move",Mat4Transform.translate(xPosition,bodyHeight/2,0));
      TransformNode alienTranslate = new TransformNode("alien transform",Mat4Transform.translate(0,0,0));
      
      NameNode body = new NameNode("body"); 
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        m = Mat4.multiply(m, Mat4Transform.scale(bodyScale,bodyScale,bodyScale));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(bodyRotateAngle));
        TransformNode bodyTransform = new TransformNode("alien body", m);
          ModelNode bodyShape = new ModelNode("Sphere(body)", body_sphere);
      bodyRotate = new TransformNode("alien transform",Mat4Transform.rotateAroundZ(bodyRotateAngle));

      NameNode head = new NameNode("head"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,(bodyScale+headScale)/2,0));
        m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
        TransformNode headTransform = new TransformNode("alien head", m);
          ModelNode headShape = new ModelNode("Sphere(head)", head_sphere);

      
      alienRoot.addChild(alienMoveTranslate);
        alienMoveTranslate.addChild(alienTranslate);
          alienTranslate.addChild(bodyRotate);
            bodyRotate.addChild(body);
              body.addChild(bodyTransform);
                bodyTransform.addChild(bodyShape);
              body.addChild(head);
                head.addChild(headTransform);
                headTransform.addChild(headShape);
      alienRoot.update();

  }

  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "vs_standard.txt", "fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return sphere;
  } 

  public void render(GL3 gl) {
    updateBody();
    alienRoot.draw(gl);
  }

  private void updateBody() {
    double elapsedTime = getSeconds()-startTime;
    bodyRotateAngle = bodyRotateAngleStart * (float)Math.sin(elapsedTime);
    bodyRotate.setTransform(Mat4Transform.rotateAroundZ(bodyRotateAngle));
    alienRoot.update();
  }

  public void dispose(GL3 gl) {
      body_sphere.dispose(gl);
    }

  private double startTime;

  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

}
