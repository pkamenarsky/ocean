/*
 * Copyright 2009 Hao Nguyen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.eleven.client.gl;

import gwt.g3d.client.gl2.enums.TextureUnit;
import gwt.g3d.client.shader.BasicShader3D;
import gwt.g3d.client.shader.ShaderException;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Represents a per-pixel lambertian shader.
 * 
 * This shader inherits all of {@link BasicShader3D} variables. In additions,
 * it also contains the following variables:
 * uniform vec3 uLightPosition;
 * uniform vec4 uDiffuseColor;
 * 
 * @author hao1300@gmail.com
 */
public class SkyboxShader extends BasicShader3D {
	
	private final Matrix3f normalMatrix = new Matrix3f();

	@Override
	protected void initImpl() throws ShaderException {
		initProgram(Resources.INSTANCE.vertexShader().getText(), 
				Resources.INSTANCE.fragmentShader().getText());
	}
	
	@Override
	public void setModelViewMatrix(Matrix4f value) {
		gl.uniformMatrix(getUniformLocation("matWorldView"), value);
		value.get(normalMatrix);
		normalMatrix.invert();
		normalMatrix.transpose();
		gl.uniformMatrix(getUniformLocation("uNormalMatrix"), normalMatrix);
	}
	
	/**
	 * Sets the uProjectionMatrix.
	 * 
	 * @param value
	 */
	@Override
	public void setProjectionMatrix(Matrix4f value) {
		gl.uniformMatrix(getUniformLocation("matProjection"), value);
	}
	
	/**
	 * Gets the attribute location of the aPosition variable.
	 */
	public int getAttributePosition() {
		return getAttributeLocation("rm_Vertex");
	}

	public void setEyePosition(float x, float y, float z) {
		gl.uniform3f(getUniformLocation("vViewPosition"), x, y, z);
	}
	
	public void setSky(TextureUnit textureUnit) {
		gl.uniform1i(getUniformLocation("uSky"), textureUnit.getValue() - TextureUnit.TEXTURE0.getValue());
	}	
	
	/** Resource files. */
	interface Resources extends ClientBundle {
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("shaders/skybox.vp")
		TextResource vertexShader();

		@Source("shaders/skybox.fp")
		TextResource fragmentShader();
	}
}
