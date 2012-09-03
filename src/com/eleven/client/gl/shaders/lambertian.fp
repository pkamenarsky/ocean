#ifdef GL_ES
precision highp float;
#endif

uniform vec4 uDiffuseColor;
uniform samplerCube uCubemap;
uniform sampler2D uSand;
uniform sampler2D uWaterbump;

varying vec2 vBump;

varying vec3 vNormal, vLightDir;
varying vec3 vReflection;
varying vec2 vRefractionOffset;

varying vec3 vwNormal;
varying vec3 vView;

void main() {
//	float nDotL = dot(normalize(vNormal), normalize(vLightDir));
//	gl_FragColor = vec4(uDiffuseColor.rgb * max(0.0, nDotL), 1.0);

	vec3 bump = normalize(texture2D(uWaterbump, vBump / 5.0).xzy);
		
//	vec3 reflect = textureCube(uCubemap, (vReflection + bump) / 2.0).xyz;
	vec3 reflect = textureCube(uCubemap, vReflection).xyz;
	vec3 refract = texture2D(uSand, vRefractionOffset).xyz; // * vec3(170.0 / 255.0, 250.0 / 255.0, 178.0 / 255.0);
	
	float dot = dot(normalize(vwNormal), normalize(-vView));
	float fresnel = pow(1.0 - dot, 5.0);
	
//	if(dot(normalize(vwNormal), normalize(-vView)) > 0.25)
//		gl_FragColor = vec4(r * 0.6 + c * 0.4, 1.0);
//	else
//		gl_FragColor = vec4(r * 0.3 + c * 0.7, 1.0);

//	gl_FragColor = vec4(c * 0.6 + r * 0.4, 1.0);
	gl_FragColor = vec4(refract * (1.0 - fresnel) + reflect * fresnel, 1.0);
}
