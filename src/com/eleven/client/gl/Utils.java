package com.eleven.client.gl;

public class Utils {
	
    public final static int PITCH = 0; // up / down 
    public final static int YAW = 1; // left / right 
    public final static int ROLL = 2; // fall over 
	
    static final float shortratio = 360.0f / 65536.0f;
    static final float piratio = (float) (Math.PI / 360.0);

    public static void VectorAdd(float[] a, float[] b, float[] to) {
        to[0] = a[0] + b[0];
        to[1] = a[1] + b[1];
        to[2] = a[2] + b[2];
}
    
    public static void AngleVectors(float[] angles, float[] forward, float[] right, float[] up) {

        float cr = 2.0f * piratio;
        float angle = (float) (angles[YAW] * (cr));
        float sy = (float) Math.sin(angle);
        float cy = (float) Math.cos(angle);
        angle = (float) (angles[PITCH] * (cr));
        float sp = (float) Math.sin(angle);
        float cp = (float) Math.cos(angle);

        if (forward != null) {
                forward[0] = cp * cy;
                forward[1] = cp * sy;
                forward[2] = -sp;
        }

        if (right != null || up != null) {
                angle = (float) (angles[ROLL] * (cr));
                float sr = (float) Math.sin(angle);
                cr = (float) Math.cos(angle);

                if (right != null) {
                        right[0] = (-sr * sp * cy + cr * sy);
                        right[1] = (-sr * sp * sy + -cr * cy);
                        right[2] = -sr * cp;
                }
                if (up != null) {
                        up[0] = (cr * sp * cy + sr * sy);
                        up[1] = (cr * sp * sy + -sr * cy);
                        up[2] = cr * cp;
                }
        }
    }
    
    public static native int getScreenWidth() /*-{ 
		return $wnd.screen.width;
	}-*/;
    
    public static native int getScreenHeight() /*-{ 
		return $wnd.screen.height;
	}-*/;
}
