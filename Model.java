import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

public class Model {
  
  private String name;
  private Mesh mesh;
  private Mat4 modelMatrix;
  private Shader shader;
  private Material material;
  private Camera camera;
  private Light[] lights;
  private Texture diffuse;
  private Texture specular;

  public Model() {
    name = null;
    mesh = null;
    modelMatrix = null;
    material = null;
    camera = null;
    lights = null;
    shader = null;
  }
  
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] light, Camera camera, Texture diffuse, Texture specular) {
    this.name = name;
    this.mesh = mesh;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.material = material;
    this.lights = light;
    this.camera = camera;
    this.diffuse = diffuse;
    this.specular = specular;
  }
  
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights, Camera camera, Texture diffuse) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, diffuse, null);
  }
  
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights, Camera camera) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, null, null);
  }

  public void setName(String s) {
    this.name = s;
  }

  public void setMesh(Mesh m) {
    this.mesh = m;
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
  }

  public Shader getShader(){
    return this.shader;
  }
  public void setShader(Shader shader) {
    this.shader = shader;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void setLight(Light[] lights) {
    this.lights = lights;
  }

  public void setDiffuse(Texture t) {
    this.diffuse = t;
  }

  public void setSpecular(Texture t) {
    this.specular = t;
  }

  public void renderName(GL3 gl) {
    System.out.println("Name = "+name);  
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }


  // second version of render is so that modelMatrix can be overriden with a new parameter
  public void render(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null in model render");
      return;
    }

    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));

    // set shader variables. Be careful that these variables exist in the shader

    shader.use(gl);

    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    for (int i = 0; i < lights.length; i++) {
      shader.setVec3(gl, "light.position", lights[i].getPosition());
      shader.setVec3(gl, "light.ambient", lights[i].getMaterial().getAmbient());
      shader.setVec3(gl, "light.diffuse", lights[i].getMaterial().getDiffuse());
      shader.setVec3(gl, "light.specular", lights[i].getMaterial().getSpecular());
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());
    this.dynamic(gl);


    // If there is a mismatch between the number of textures the shader expects and the number we try to set here, then there will be problems.
    // Assumption is the user supplied the right shader and the right number of textures for the model

    if (diffuse!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      diffuse.bind(gl);
    }
    if (specular!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      specular.bind(gl);
    }

    // then render the mesh
    mesh.render(gl);
  } 
  
  private boolean mesh_null() {
    return (mesh==null);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);  // only need to dispose of mesh
  }

  private void dynamic(GL3 gl) {
    double elapsedTime = getSeconds() - startTime;
    double t = elapsedTime*0.1;
    float offsetY = (float)(t - Math.floor(t));
    float offsetX = 0.0f;
    shader.setFloat(gl, "offset", offsetX, offsetY);;
  }
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
}