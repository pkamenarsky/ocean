package com.eleven.client.gl;

import gwt.g2d.client.media.Audio;
import gwt.g3d.client.Surface3D;
import gwt.g3d.client.gl2.GL2;
import gwt.g3d.client.gl2.WebGLTexture;
import gwt.g3d.client.gl2.enums.ClearBufferMask;
import gwt.g3d.client.gl2.enums.EnableCap;
import gwt.g3d.client.gl2.enums.PixelFormat;
import gwt.g3d.client.gl2.enums.PixelInternalFormat;
import gwt.g3d.client.gl2.enums.PixelType;
import gwt.g3d.client.gl2.enums.TextureMagFilter;
import gwt.g3d.client.gl2.enums.TextureMinFilter;
import gwt.g3d.client.gl2.enums.TextureParameterName;
import gwt.g3d.client.gl2.enums.TextureTarget;
import gwt.g3d.client.gl2.enums.TextureUnit;
import gwt.g3d.client.gl2.enums.TextureWrapMode;
import gwt.g3d.client.math.MatrixStack;
import gwt.g3d.client.mesh.StaticMesh;
import gwt.g3d.client.primitive.MeshData;
import gwt.g3d.client.primitive.PrimitiveFactory;
import gwt.g3d.client.shader.AbstractShader;
import gwt.g3d.client.shader.ShaderException;
import gwt.g3d.resources.client.ExternalTexture2DResource;
import gwt.g3d.resources.client.MagFilter;
import gwt.g3d.resources.client.MinFilter;
import gwt.g3d.resources.client.Texture2DResource;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class GL_hl implements EntryPoint {

	Vector<WebGLTexture>	glTextures = new Vector<WebGLTexture>();
	
	private MeshData makePlane(int sizex, int sizey, float level, float scale) {
		
		int size = (sizex + 1) * (sizey + 1);
		
		float[] vertices = new float[3 * size];
		float[] normals = new float[3 * size];
		float[] texCoords = new float[2 * size];
		int[] triangles = new int[6 * sizex * sizey];
		
		int index = 0;
		
		for(int x = 0; x <= sizex; x++)
			for(int y = 0; y <= sizey; y++) {
				vertices[3 * (x + y * (sizex + 1)) + 0] = ((float) x - (sizex / 2)) / sizex / 2 * scale;
				vertices[3 * (x + y * (sizex + 1)) + 1] = ((float) y - (sizey / 2)) / sizey / 2 * scale;
				vertices[3 * (x + y * (sizex + 1)) + 2] = level;
				
				normals[3 * (x + y * (sizex + 1)) + 0] = 0;
				normals[3 * (x + y * (sizex + 1)) + 1] = 0;
				normals[3 * (x + y * (sizex + 1)) + 2] = 1;
				
				texCoords[2 * (x + y * (sizex + 1)) + 0] = ((float) x / sizex) * scale;
				texCoords[2 * (x + y * (sizex + 1)) + 1] = ((float) y / sizey) * scale;
			}
		
		for(int x = 0; x < sizex; x++)
			for(int y = 0; y < sizey; y++) {
				int	offset = (x + y * (sizex + 1));
				
				triangles[index++] = offset;
				triangles[index++] = offset + (sizex + 1);
				triangles[index++] = offset + 1;

				triangles[index++] = offset + 1;
				triangles[index++] = offset + (sizex + 1);
				triangles[index++] = offset + (sizex + 1) + 1;
			}
		
		return new MeshData(vertices, triangles, normals, texCoords);
	}
	
	interface Resources extends ClientBundleWithLookup {
		static String	skybase = "images/skylh2/sky_day01_08";
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("images/sky_blue.jpg")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sky();
		
		@Source("images/sand2.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sand();
		
		@Source("images/waterbump2.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource waterbump();
		
		@Source("images/Caustics_0.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource caustics();
		
		@Source(skybase + "ft.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sky_ft();
		
		@Source(skybase + "bk.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sky_bk();
		
		@Source(skybase + "lf.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sky_lf();
		
		@Source(skybase + "rt.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sky_rt();
		
		@Source(skybase + "up.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sky_up();
		
		@Source(skybase + "dn.png")
		@MagFilter(TextureMagFilter.LINEAR)
		@MinFilter(TextureMinFilter.LINEAR)
		ExternalTexture2DResource sky_dn();
	}
	
	interface ActionCompleteListener {
		public void complete();
	}
	
	interface TextureLoaderListener<T> {
		public void imageLoaded(Texture2DResource resource, T data);
	}
	
	class CubemapLoader implements TextureLoaderListener<TextureTarget> {

		TextureTarget			targets[] = new TextureTarget[6];
		ImageElement			textures[] = new ImageElement[6];
		
		int						count = 0;
		
		GL2						gl;
		ActionCompleteListener	listener;
				
		public CubemapLoader(GL2 gl, ActionCompleteListener listener) {
			this.gl = gl;
			this.listener = listener;
		}

		@Override
		public void imageLoaded(Texture2DResource resource, TextureTarget data) {
			if(count >= 6)
				return;
			
			targets[count] = data;
			textures[count++] = resource.getImage();

			if(count == 6) {
				WebGLTexture	cube = gl.createTexture();
				
				glTextures.add(cube);
				
				gl.activeTexture(TextureUnit.TEXTURE0);
				gl.bindTexture(TextureTarget.TEXTURE_CUBE_MAP, cube);

				for(int i = 0; i < 6; i++)
					gl.texImage2D(targets[i], 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, textures[i]);
				
				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_MIN_FILTER, TextureMinFilter.LINEAR.getValue());
				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_MAG_FILTER, TextureMagFilter.LINEAR.getValue());

				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_WRAP_S, TextureWrapMode.CLAMP_TO_EDGE.getValue());
				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_WRAP_T, TextureWrapMode.CLAMP_TO_EDGE.getValue());
				
				listener.complete();
			}
		}
	}
	
	private <T> void loadTextureAsync(ExternalTexture2DResource resource, final T data, final TextureLoaderListener<T> listener) {
		resource.getTexture(new ResourceCallback<Texture2DResource>() {
			
			@Override
			public void onSuccess(Texture2DResource resource) {
				listener.imageLoaded(resource, data);
			}
			
			@Override
			public void onError(ResourceException e) {
				  Window.alert("FAIL");
			}
		});				
	}
	
	private void loadSkybox(final GL2 gl, ActionCompleteListener listener) {
		CubemapLoader	loader = new CubemapLoader(gl, listener);
				
		loadTextureAsync(Resources.INSTANCE.sky_rt(), TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_X, loader);
		loadTextureAsync(Resources.INSTANCE.sky_lf(), TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_X, loader);
		
		loadTextureAsync(Resources.INSTANCE.sky_ft(), TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_Y, loader);
		loadTextureAsync(Resources.INSTANCE.sky_bk(), TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_Y, loader);
		
		loadTextureAsync(Resources.INSTANCE.sky_up(), TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_Z, loader);
		loadTextureAsync(Resources.INSTANCE.sky_dn(), TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_Z, loader);		
	}
	
	private void loadSkybox2(final GL2 gl, final WaterShader shader) {
		
		Resources.INSTANCE.sky().getTexture(new ResourceCallback<Texture2DResource>() {
			
			@Override
			public void onSuccess(Texture2DResource resource) {
//				Window.alert("OLOL");

				WebGLTexture	cube = gl.createTexture();
				
				glTextures.add(cube);

				gl.activeTexture(TextureUnit.TEXTURE0);
				gl.bindTexture(TextureTarget.TEXTURE_CUBE_MAP, cube);
				
				gl.texImage2D(TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_X, 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, resource.getImage());
				gl.texImage2D(TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_X, 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, resource.getImage());
				gl.texImage2D(TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, resource.getImage());
				gl.texImage2D(TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_Y, 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, resource.getImage());
				gl.texImage2D(TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, resource.getImage());
				gl.texImage2D(TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_Z, 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, resource.getImage());
				
				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_MIN_FILTER, TextureMinFilter.LINEAR.getValue());
				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_MAG_FILTER, TextureMagFilter.LINEAR.getValue());

				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_WRAP_S, TextureWrapMode.CLAMP_TO_EDGE.getValue());
				gl.texParameteri(TextureTarget.TEXTURE_CUBE_MAP, TextureParameterName.TEXTURE_WRAP_T, TextureWrapMode.CLAMP_TO_EDGE.getValue());
				
				gl.bindTexture(TextureTarget.TEXTURE_CUBE_MAP, cube);
				shader.setSky(TextureUnit.TEXTURE0);
				
//				Window.alert(gl.getError().toString());
			}
			
			@Override
			public void onError(ResourceException e) {
				  Window.alert("FAIL");
			}
		});
	}

	private void loadTexture(final GL2 gl, ExternalTexture2DResource texture, final AbstractShader shader, final TextureUnit unit, final String name) {
		
		texture.getTexture(new ResourceCallback<Texture2DResource>() {
			
			@Override
			public void onSuccess(Texture2DResource resource) {
/*
				Texture2D	texture = resource.createTexture(gl);

				gl.activeTexture(unit);
				texture.bind();
				
				shader.setNamedTexture(unit, name);
*/
				
				WebGLTexture	texture = gl.createTexture();
				
				glTextures.add(texture);

				gl.activeTexture(unit);
				gl.bindTexture(TextureTarget.TEXTURE_2D, texture);
				
				gl.texImage2D(TextureTarget.TEXTURE_2D, 0, PixelInternalFormat.RGBA, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, resource.getImage());
				
				gl.texParameteri(TextureTarget.TEXTURE_2D, TextureParameterName.TEXTURE_MIN_FILTER, TextureMinFilter.LINEAR_MIPMAP_LINEAR.getValue());
				gl.texParameteri(TextureTarget.TEXTURE_2D, TextureParameterName.TEXTURE_MAG_FILTER, TextureMagFilter.LINEAR.getValue());

				gl.texParameteri(TextureTarget.TEXTURE_2D, TextureParameterName.TEXTURE_WRAP_S, TextureWrapMode.REPEAT.getValue());
				gl.texParameteri(TextureTarget.TEXTURE_2D, TextureParameterName.TEXTURE_WRAP_T, TextureWrapMode.REPEAT.getValue());
				
				gl.bindTexture(TextureTarget.TEXTURE_2D, texture);
				gl.generateMipmap(TextureTarget.TEXTURE_2D);
				
				shader.bind();
				shader.setNamedTexture(unit, name);
			}
			
			@Override
			public void onError(ResourceException e) {
				  Window.alert("FAIL");
			}
		});
	}

	
	float position[] = {0.0f, 18.0f, 6.0f};
	float center[] = {0.0f, 0.0f, 0.0f};
	
	float angles[] = {0, 90, 0};
	
	
	@Override
	public void onModuleLoad() {
		  // Adds the Surface3D to the document.
		  final Surface3D surface = new Surface3D(Utils.getScreenWidth(), Utils.getScreenHeight() - 5);
		  RootPanel.get().add(surface);
		  final GL2 gl = surface.getGL();
		  if (gl == null) {
		    Window.alert("No WebGL context found. Exiting.");
		    return;
		  }

		  surface.setFocus(true);
		  
		  // Sets up the GL context.
		  gl.clearColor(0.0f, 0f, 0f, 1f);
		  gl.clearDepth(1);
		  gl.viewport(0, 0, surface.getWidth(), surface.getHeight());
		        
		  gl.disable(EnableCap.DEPTH_TEST);
//		  gl.enable(EnableCap.DEPTH_TEST);
//		  gl.depthFunc(DepthFunction.LEQUAL);
		  gl.clear(ClearBufferMask.COLOR_BUFFER_BIT, ClearBufferMask.DEPTH_BUFFER_BIT);    

		  final WaterShader waterShader = new WaterShader();
		  try {
		    waterShader.init(gl);
		  } catch (ShaderException e) {
		    Window.alert("Error loading the shader: " + e.getMessage());
		    return;
		  }
		        
		  // Binds the shader.
		  waterShader.bind();

		  // Creates a sphere.
		  final StaticMesh waterMesh = new StaticMesh(gl, makePlane(2, 2, 0.0f, 50000.0f));
//		  mesh.setBeginMode(BeginMode.LINES);
		  waterMesh.setPositionIndex(waterShader.getAttributePosition());
		  waterMesh.setNormalIndex(waterShader.getAttributeNormal());
		                
		  waterShader.setLightPosition(0, -2200, 100);
		  waterShader.setEyePosition(position[0], position[1], position[2]);

		  loadTexture(gl, Resources.INSTANCE.sand(), waterShader, TextureUnit.TEXTURE1, "uSand");
		  loadTexture(gl, Resources.INSTANCE.waterbump(), waterShader, TextureUnit.TEXTURE2, "uWaterbump");
		  loadTexture(gl, Resources.INSTANCE.caustics(), waterShader, TextureUnit.TEXTURE3, "uCaustics");

		  // Sets up the model view matrix.
		  MatrixStack.MODELVIEW.push();
		  MatrixStack.MODELVIEW.lookAt(position[0], position[1], position[2], center[0], center[1], center[2], 0.0f, 0.0f, 1.0f);
//		  MatrixStack.MODELVIEW.translate(0, -2, -5);
		  waterShader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
		  MatrixStack.MODELVIEW.pop();

		  // Sets up a basic camera for projection.
		  MatrixStack.PROJECTION.pushIdentity();
		  MatrixStack.PROJECTION.perspective(45, 1, .1f, 100);
		  waterShader.setProjectionMatrix(MatrixStack.PROJECTION.get());
		  MatrixStack.PROJECTION.pop();

		  final SkyboxShader skyboxShader = new SkyboxShader();
		  try {
		    skyboxShader.init(gl);
		  } catch (ShaderException e) {
		    Window.alert("Error loading the shader: " + e.getMessage());
		    return;
		  }
		  
		  // Binds the shader.
		  skyboxShader.bind();

		  // Creates a sphere.
		  final StaticMesh skyboxMesh = new StaticMesh(gl, PrimitiveFactory.makeBox());
//		  mesh.setBeginMode(BeginMode.LINES);
		  skyboxMesh.setPositionIndex(skyboxShader.getAttributePosition());
		                
		  skyboxShader.setEyePosition(position[0], position[1], position[2]);

		  loadSkybox(gl, new ActionCompleteListener() {
			
			@Override
			public void complete() {
				waterShader.bind();
				  waterShader.setSky(TextureUnit.TEXTURE0);
				  
				  skyboxShader.bind();
				  skyboxShader.setSky(TextureUnit.TEXTURE0);
			}
		});

		  // Sets up the model view matrix.
		  MatrixStack.MODELVIEW.push();
		  MatrixStack.MODELVIEW.lookAt(position[0], position[1], position[2], center[0], center[1], center[2], 0.0f, 0.0f, 1.0f);
//		  MatrixStack.MODELVIEW.translate(0, -2, -5);
		  skyboxShader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
		  MatrixStack.MODELVIEW.pop();

		  // Sets up a basic camera for projection.
		  MatrixStack.PROJECTION.pushIdentity();
		  MatrixStack.PROJECTION.perspective(45, 1, .1f, 100);
		  skyboxShader.setProjectionMatrix(MatrixStack.PROJECTION.get());
		  MatrixStack.PROJECTION.pop();

		  
		  final Label	fps = new Label();
//		  RootPanel.get().add(fps, 1100, 50);
		  
		  Audio	audio = new Audio(GWT.getHostPageBaseURL() + "resources/38017__rockdoctor__sea2.ogg");
		  RootPanel.get().add(audio);
		  audio.setLoop(true);
		  audio.setAutoplay(true);
//		  audio.setVolume(0.5f);
		  
		  // Draws the mesh.
		  final Timer timer = new Timer() {
			  
			  int frames = 0;
			  long oldTime = 0;
			  long startTime;
			  
			  {
				  startTime = new Date().getTime();
			  }
			  
			  @Override
			  public void run() {
				  
				  long currentTime = new Date().getTime() - startTime;
				  
				  if(currentTime - oldTime > 1000) {
					  fps.setText("FPS: " + frames);
					  
					  frames = 0;
					  oldTime  = currentTime;
				  }

				  float[] forward = {0, 0, 0}, right = {0, 0, 0}, up = {0, 0, 0}, to = {0, 0, 0};
				  Utils.AngleVectors(angles, forward, right, up);
				  Utils.VectorAdd(position, forward, to);
				  
				  MatrixStack.MODELVIEW.push();
				  MatrixStack.MODELVIEW.lookAt(position[0], position[1], position[2], to[0], to[1], to[2], up[0], up[1], up[2]);
				  
				  
//				  System.out.println("1: " + gl.getError().toString());
				  
//				  gl.depthMask(true);
				  gl.clear(ClearBufferMask.COLOR_BUFFER_BIT, ClearBufferMask.DEPTH_BUFFER_BIT);
//				  gl.disable(EnableCap.DEPTH_TEST);
				  gl.depthMask(false);

//				  System.out.println("2: " + gl.getError().toString());

				  skyboxShader.bind();
				  skyboxShader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
//				  gl.depthMask(false);
				  skyboxMesh.draw();
//				  gl.flush();

//				  System.out.println("3: " + gl.getError().toString());

				  waterShader.bind();
				  waterShader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
//				  System.out.println("3.1: " + gl.getError().toString());
				  waterShader.setTime(currentTime / 1000.0f);
//				  System.out.println("3.2: " + gl.getError().toString());
//				  gl.disable(EnableCap.DEPTH_TEST);
//				  gl.depthMask(false);
				  waterMesh.draw();
//				  gl.finish();
				  
//				  System.out.println("4: " + gl.getError().toString());
				  
				  frames++;
				  MatrixStack.MODELVIEW.pop();
			  }  
		  };
		  
		  timer.scheduleRepeating(1);
/*		  
		  final Button	stop = new Button("Stop");
		  RootPanel.get().add(stop, 1200, 50);
		  stop.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				timer.cancel();
				
				waterMesh.dispose();
				skyboxMesh.dispose();
				
				waterShader.dispose();
				skyboxShader.dispose();
				
				for(WebGLTexture texture : glTextures)
					gl.deleteTexture(texture);
				
				gl.enable(EnableCap.DEPTH_TEST);
				gl.depthMask(true);
				gl.finish();
			}
		});
*/		  
		  
		  surface.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == 'd')
					angles[1] -= 10;
				else if(event.getCharCode() == 'a')
					angles[1] += 10;
			}
		});
//		  mesh.dispose();
//		  shader.dispose();
/*		  
		  Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				surface.setWidth(Window.getClientWidth());
				surface.setHeight(Window.getClientHeight());
			}
		});
*/		}
}
