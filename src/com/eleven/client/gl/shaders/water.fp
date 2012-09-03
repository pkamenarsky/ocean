#ifdef GL_ES
precision highp float;
#endif


uniform samplerCube uSky;
uniform sampler2D uWaterbump;
uniform sampler2D uSand;
uniform sampler2D uCaustics;


varying vec3 vLight;
varying vec3 vPosition;
varying vec3 vNormal;
varying vec3 vView;

varying vec2 vTexCoord1;
varying vec2 vTexCoord2;
varying vec2 vTexCoord3;


const vec3 scale = vec3(0.01001, 0.01001, 0.01001);

const float bumpFactor = 0.05;
const float waterDepth = 3.0;
const float causticsScale = 1.5;

const vec3 tintColor = vec3(20.0 / 255.0, 110.0 / 250.0, 140.0 / 255.0);
const float fresnelPow = 2.0;

//const vec3 tintColor = vec3(10.0 / 255.0, 80.0 / 250.0, 110.0 / 255.0);
//const float fresnelPow = 5.0;

vec3 saturate(vec3 v) {
	return vec3(clamp(v.x, 0.0, 1.0), clamp(v.y, 0.0, 1.0), clamp(v.z, 0.0, 1.0));
}

void main(void)
{
	vec3 wave1 = texture2D(uWaterbump, vTexCoord1).rgb;
	vec3 wave2 = texture2D(uWaterbump, vTexCoord2).rgb;
	vec3 wave3 = texture2D(uWaterbump, vTexCoord3).rgb;

	float factor = length(vView);
//	float factor = 50;
	
	vec3 normal = normalize(vNormal + (wave1 + wave2 + wave3 - 1.5) * bumpFactor /* / (factor / 50.0) */);
	vec3 view = normalize(vView);
	
	float fresnel = pow(1.0 - dot(-view, normal), fresnelPow);

	vec3 reflectVec = reflect(-view, normal);
    vec3 refractVec = normalize(refract(view, normal, 0.77));
    
	vec2 refractTex = vec2((vPosition.x * scale.x + refractVec.x * waterDepth) * 1.0, (vPosition.y * scale.x + refractVec.y * waterDepth) * 1.0);
	vec3 cubeTex = vec3(-reflectVec.x, -reflectVec.y, -reflectVec.z);

	vec3 reflectColor = textureCube(uSky, cubeTex).xyz;
	vec3 refractColor = texture2D(uSand, refractTex).xyz + texture2D(uCaustics, refractTex * causticsScale).xyz + tintColor; // * clamp((factor / 20.0), 1.0, 1.0);

//	vec3 specular = pow(saturate(dot(-view, reflect(normalize(vLight), normal))), 64.0) * 0.4;
	vec3 specular = vec3(0.0, 0.0, 0.0);

//	fresnel = 0.0;

	gl_FragColor = vec4(reflectColor * fresnel + refractColor * (1.0 - fresnel) + specular, 1.0) ;
}
