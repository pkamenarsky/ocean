#ifdef GL_ES
precision highp float;
#endif


uniform samplerCube uSky;


varying vec3 vTexCoord;


void main(void)
{
	vec3	coord = vec3(vTexCoord.x, vTexCoord.y, vTexCoord.z);
	gl_FragColor = textureCube(uSky, coord);
}
