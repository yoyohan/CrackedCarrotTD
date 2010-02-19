package com.crackedcarrot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

public class NativeRender implements GLSurfaceView.Renderer {

	private static native void nativeAlloc(int n, Sprite s);
	private static native void nativeDataPoolSize(int size);
    private static native void nativeResize(int w, int h);
    private static native void nativeDrawFrame();
    private static native void nativeSurfaceCreated();
    private static native int  nativeLoadTexture();
    	
	public Sprite[] mSprites;
	public int[] mCropWorkspace;
	public int[] mTextureNameWorkspace;
	public boolean mUseHardwareBuffers;
	public Context mContext;
	public static BitmapFactory.Options sBitmapOptions
    = new BitmapFactory.Options();

	public NativeRender(Context context) {
        // Pre-allocate and store these objects so we can use them at runtime
        // without allocating memory mid-frame.
        mTextureNameWorkspace = new int[1];
        mCropWorkspace = new int[4];
        
        // Set our bitmaps to 16-bit, 565 format.
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        
		mContext = context;
		System.loadLibrary("render");
	}

	public void onDrawFrame(GL10 gl) {
		nativeDrawFrame();
	}

	//@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		nativeResize(width, height);
	}
	
	//@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		nativeSurfaceCreated();
		
		/*
         * Some one-time OpenGL initialization can be made here probably based
         * on features of this particular context
         */
        /*gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        /*
         * By default, OpenGL enables features that improve quality but reduce
         * performance. One might want to tweak that especially on software
         * renderer.
         */
/*        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
*/       
        if (mSprites != null) {
            
            // If we are using hardware buffers and the screen lost context
            // then the buffer indexes that we recorded previously are now
            // invalid.  Forget them here and recreate them below.
            
            // Load our texture and set its texture name on all sprites.
            
            // To keep this sample simple we will assume that sprites that share
            // the same texture are grouped together in our sprite list. A real
            // app would probably have another level of texture management, 
            // like a texture hash.
            
            int lastLoadedResource = -1;
            int lastTextureId = -1;
            
            for (int x = 0; x < mSprites.length; x++) {
                int resource = mSprites[x].getResourceId();
                if (resource != lastLoadedResource) {
					lastTextureId = loadBitmap(mContext, gl, resource);
                    lastLoadedResource = resource;
                }
                mSprites[x].setTextureName(lastTextureId);
            }
        }
	}
	
	public void setSprites(Sprite[] spriteArray) {
        mSprites = spriteArray;
        nativeDataPoolSize(mSprites.length);
        for(int i = 0; i < mSprites.length; i++){
        	nativeAlloc(i, mSprites[i]);
        }
	}

/*	public int loadBitmap(Context context, int resourceId){
		
		InputStream is = context.getResources().openRawResource(resourceId);
		Bitmap bitmap;
		
		i
		
		try{
			bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
		}finally{
				try{
					is.close();
				}catch(IOException e){
					
				}
		}
		
        mCropWorkspace[0] = 0;
        mCropWorkspace[1] = bitmap.getHeight();
        mCropWorkspace[2] = bitmap.getWidth();
        mCropWorkspace[3] = -bitmap.getHeight();
                
        ByteBuffer buf = ByteBuffer.allocateDirect(10000);
        bitmap.copyPixelsToBuffer(buf);
        
        nativeLoadTexture();
        
		bitmap.recycle();
		return 0;
	}*/
	
	public int loadBitmap(Context context, GL10 gl, int resourceId) {
        int textureName = -1;
        if (context != null && gl != null) {
            gl.glGenTextures(1, mTextureNameWorkspace, 0);

            textureName = mTextureNameWorkspace[0];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

            InputStream is = context.getResources().openRawResource(resourceId);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore.
                }
            }

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

            mCropWorkspace[0] = 0;
            mCropWorkspace[1] = bitmap.getHeight();
            mCropWorkspace[2] = bitmap.getWidth();
            mCropWorkspace[3] = -bitmap.getHeight();
            
            bitmap.recycle();

            ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
                    GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);

            
            int error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("SpriteMethodTest", "Texture Load GLError: " + error);
            }
        
        }

        return textureName;
    }
}
