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

  private Model head_sphere, body_sphere, arm_sphere, eye_sphere, ear_sphere, antstick_sphere, antsphere_sphere;
  private TransformNode translateX, alienMoveTranslate, bodyRotate, headRotate;
  private SGNode alienRoot;
  private float xPosition = 0;
  private float bodyRotateAngleStart = 10, bodyRotateAngle = bodyRotateAngleStart;
  private float headRotateAngleStart = 15, headRotateAngle = headRotateAngleStart;


  public Alien(GL3 gl, Camera cameraIn, Light lightIn, float posX, Texture t1, Texture t2, Texture t3, Texture t4) {
      this.camera = cameraIn;
      this.light = lightIn;
      this.xPosition = posX;

      arm_sphere = makeSphere(gl, t1,t2);
      eye_sphere = makeSphere(gl, t1, t3);
      body_sphere = makeSphere(gl, t1, t4);
      head_sphere = makeSphere(gl, t2, t3);
      ear_sphere = makeSphere(gl, t2, t4);
      antstick_sphere = makeSphere(gl, t3, t4);
      antsphere_sphere = makeSphere(gl, t3, t4);

      float headRadius = 2f/2;
      float headScale = 2f;
      float bodyRadius = 3.0f/2;
      float bodyScale = 3f;
      float armScaleX = 0.2f;
      float armScaleY = 1.3f;
      float armScaleZ = 0.2f;
      float earScaleX = 0.13f;
      float earScaleY = 1.2f;
      float earScaleZ = 0.13f;
      float eyeScale = 0.35f;
      float antStiScaleX = 0.13f;
      float antStiScaleY = 1.3f;
      float antStiScaleZ = 0.13f;
      float antSpheScale = 0.5f;

      alienRoot = new NameNode("root");
      alienMoveTranslate = new TransformNode("move",Mat4Transform.translate(xPosition,bodyRadius,0));
      TransformNode alienTranslate = new TransformNode("alien transform",Mat4Transform.translate(0,0,0));
      
      NameNode body = new NameNode("body"); 
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        m = Mat4.multiply(m, Mat4Transform.scale(bodyScale,bodyScale,bodyScale));
        TransformNode bodyTransform = new TransformNode("alien body", m);
          ModelNode bodyShape = new ModelNode("Sphere(body)", body_sphere);
      // body animate
      bodyRotate = new TransformNode("body rotate",Mat4Transform.rotateAroundZ(bodyRotateAngle));

      NameNode head = new NameNode("head"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,bodyRadius+headRadius,0));
        m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
        TransformNode headTransform = new TransformNode("alien head", m);
          ModelNode headShape = new ModelNode("Sphere(head)", head_sphere);
      headRotate = new TransformNode("head rotate", Mat4Transform.rotateAroundZ(headRotateAngle));

      NameNode leftArm = new NameNode("leftArm"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-bodyRadius,0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(45));
        m = Mat4.multiply(m, Mat4Transform.scale(armScaleX,armScaleY,armScaleZ));
        TransformNode leftArmTransform = new TransformNode("alien left arm", m);
          ModelNode leftArmShape = new ModelNode("Sphere(arm)", arm_sphere);

      NameNode rightArm = new NameNode("rightArm"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(bodyRadius,0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(-45));
        m = Mat4.multiply(m, Mat4Transform.scale(armScaleX,armScaleY,armScaleZ));
        TransformNode rightArmTransform = new TransformNode("alien right arm", m);
          ModelNode rightArmShape = new ModelNode("Sphere(arm)", arm_sphere);

      NameNode leftEar = new NameNode("leftEar"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-headRadius,bodyRadius+1.5f*headRadius,0));
        m = Mat4.multiply(m, Mat4Transform.scale(earScaleX,earScaleY,earScaleZ));
        TransformNode leftEarTransform = new TransformNode("alien left ear", m);
          ModelNode leftEarShape = new ModelNode("Sphere(ear)", ear_sphere);

      NameNode rightEar = new NameNode("rightEar"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(headRadius,bodyRadius+1.5f*headRadius,0));
        m = Mat4.multiply(m, Mat4Transform.scale(earScaleX,earScaleY,earScaleZ));
        TransformNode rightEarTransform = new TransformNode("alien right ear", m);
          ModelNode rightEarShape = new ModelNode("Sphere(ear)", ear_sphere);

      NameNode leftEye = new NameNode("leftEye"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-0.5f,bodyRadius+headRadius,headRadius));
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
        TransformNode leftEyeTransform = new TransformNode("alien left eye", m);
          ModelNode leftEyeShape = new ModelNode("Sphere(eye)", eye_sphere);
          
      NameNode rightEye = new NameNode("rightEye"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f,bodyRadius+headRadius,headRadius));
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
        TransformNode rightEyeTransform = new TransformNode("alien right eye", m);
          ModelNode rightEyeShape = new ModelNode("Sphere(eye)", eye_sphere);

      NameNode antennaStick = new NameNode("antennaStick"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,bodyScale+headRadius,0));
        m = Mat4.multiply(m, Mat4Transform.scale(antStiScaleX,antStiScaleY,antStiScaleZ));
        TransformNode antennaStickTransform = new TransformNode("antenna stick", m);
          ModelNode antennaStickShape = new ModelNode("Sphere(antennaStick)", antstick_sphere);

      NameNode antennaSphere = new NameNode("antennaSphere"); 
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,bodyScale+headRadius+antStiScaleY/2,0));
        m = Mat4.multiply(m, Mat4Transform.scale(antSpheScale,antSpheScale,antSpheScale));
        TransformNode antennaSphereTransform = new TransformNode("antenna sphere", m);
          ModelNode antennaSphereShape = new ModelNode("Sphere(antennaSphere)", antsphere_sphere);

      
      alienRoot.addChild(alienMoveTranslate);
        alienMoveTranslate.addChild(alienTranslate);
          alienTranslate.addChild(bodyRotate);
            bodyRotate.addChild(body);
              body.addChild(bodyTransform);
                bodyTransform.addChild(bodyShape);
              body.addChild(headRotate);
                headRotate.addChild(head);
                  head.addChild(headTransform);
                    headTransform.addChild(headShape);
                  head.addChild(leftEarTransform);
                    leftEarTransform.addChild(leftEar);
                      leftEar.addChild(leftEarShape);
                  head.addChild(rightEarTransform);
                    rightEarTransform.addChild(rightEar);
                      rightEar.addChild(rightEarShape);
                  head.addChild(leftEyeTransform);
                    leftEyeTransform.addChild(leftEye);
                      leftEye.addChild(leftEyeShape);
                  head.addChild(rightEyeTransform);
                    rightEyeTransform.addChild(rightEye);
                      rightEye.addChild(rightEyeShape);
                  head.addChild(antennaStickTransform);
                    antennaStickTransform.addChild(antennaStick);
                      antennaStick.addChild(antennaStickShape);
                  head.addChild(antennaSphereTransform);
                    antennaSphereTransform.addChild(antennaSphere);
                      antennaSphere.addChild(antennaSphereShape);
              body.addChild(leftArm);
                leftArm.addChild(leftArmTransform);
                  leftArmTransform.addChild(leftArmShape);
              body.addChild(rightArm);
                rightArm.addChild(rightArmTransform);
                  rightArmTransform.addChild(rightArmShape);
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
    headRotateAngle = headRotateAngleStart * (float)Math.sin(elapsedTime*0.7f);
    bodyRotate.setTransform(Mat4Transform.rotateAroundZ(bodyRotateAngle));
    headRotate.setTransform(Mat4Transform.rotateAroundZ(headRotateAngle));
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
