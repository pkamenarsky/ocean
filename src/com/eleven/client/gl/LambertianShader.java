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
public class LambertianShader extends BasicShader3D {
	
	@Override
	protected void initImpl() throws ShaderException {
		initProgram(Resources.INSTANCE.vertexShader().getText(), 
				Resources.INSTANCE.fragmentShader().getText());
	}
	
	public int getAttributeTexCoord() {
		return getAttributeLocation("aTexCoord");
	}
	
	/**
	 * Sets the uLightPosition.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setLightPosition(float x, float y, float z) {
		gl.uniform3f(getUniformLocation("uLightPosition"), x, y, z);
	}
	
	/**
	 * Sets the uLightPosition.
	 * 
	 * @param value
	 */
	public void setLightPosition(Tuple3f value) {
		gl.uniform(getUniformLocation("uLightPosition"), value);
	}
	
	public void setEyePosition(float x, float y, float z) {
		gl.uniform3f(getUniformLocation("uEyePosition"), x, y, z);
	}

	/**
	 * Sets the uDiffuseColor.
	 * 
	 * @param r
	 * @param g
	 * @param b 
	 * @param a
	 */
	public void setDiffuseColor(float r, float g, float b, float a) {
		gl.uniform4f(getUniformLocation("uDiffuseColor"), r, g, b, a);
	}
	
	/**
	 * Sets the uDiffuseColor.
	 * 
	 * @param value
	 */
	public void setDiffuseColor(Tuple4f value) {
		gl.uniform(getUniformLocation("uDiffuseColor"), value);
	}
	
	public void setTime(float time) {
		gl.uniform1f(getUniformLocation("time"), time);
	}
	
	public void setWaterheight(float waterHeight) {
		gl.uniform1f(getUniformLocation("waterHeight"), waterHeight);
	}
	
	public void setAmplitude(float amplitude[]) {
		gl.uniform1fv(getUniformLocation("amplitude"), amplitude);
	}
	
	public void setWavelength(float wavelength[]) {
		gl.uniform1fv(getUniformLocation("wavelength"), wavelength);
	}
	
	public void setSpeed(float speed[]) {
		gl.uniform1fv(getUniformLocation("speed"), speed);
	}
	
	public void setDirection(float direction[]) {
		gl.uniform2fv(getUniformLocation("direction"), direction);
	}
	
	public void setCubemap(TextureUnit textureUnit) {
		gl.uniform1i(getUniformLocation("uCubemap"), textureUnit.getValue() - TextureUnit.TEXTURE0.getValue());
	}	
	
	/** Resource files. */
	interface Resources extends ClientBundle {
		Resources INSTANCE = GWT.create(Resources.class);
		
		@Source("shaders/lambertian.vp")
		TextResource vertexShader();

		@Source("shaders/lambertian.fp")
		TextResource fragmentShader();
	}
}
