package com.crackedcarrot;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import com.crackedcarrot.textures.TextureData;
import com.crackedcarrot.textures.TextureLibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

public class NativeRender implements GLSurfaceView.Renderer {
	
	public	static final int BACKGROUND = 0;
	public 	static final int SHOT		= 1;
	public  static final int EFFECT		= 2;
	public 	static final int CREATURE	= 3;
	public  static final int HUD		= 4;
	public	static final int TOWER		= 5;
	
	private static native void nativeAllocTextureBuffers(int length);
	private static native void nativeSetTextureBuffer(TextureData textureData);
	private static native void nativeAlloc(int n, Sprite s);
	private static native void nativeDataPoolSize(int type, int size);
    private static native void nativeResize(int w, int h);
    private static native void nativeDrawFrame();
    private static native void nativeSurfaceCreated();
    private static native void nativeFreeSprites();
    private static native void nativeFreeTex(int i);
    
    private Semaphore lock1 = new Semaphore(0);
    private Semaphore lock2 = new Semaphore(0);
    
	private Sprite[][] sprites = new Sprite[6][];
	//private Sprite[] renderList;
	
	private int[] mCropWorkspace;
	private int[] mTextureNameWorkspace;
	//public boolean mUseHardwareBuffers;
	private Context mContext;
	private GLSurfaceView view;
	private GL10 glContext;
	private static BitmapFactory.Options sBitmapOptions
    = new BitmapFactory.Options();
	
	private TextureLibrary texLib;
	private HashMap<Integer,TextureData> textureMap = new HashMap<Integer,TextureData>();
	
	public NativeRender(Context context, GLSurfaceView view, TextureLibrary texLib, Sprite[] HUDObjects) {
        // Pre-allocate and store these objects so we can use them at runtime
        // without allocating memory mid-frame.
        mTextureNameWorkspace = new int[1];
        mCropWorkspace = new int[4];
        
        // Set our bitmaps to 16-bit, 565 format.
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        
        this.texLib = texLib;
		mContext = context;
		this.view = view;
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
		Log.d("GL_INFO", gl.glGetString(GL11.GL_EXTENSIONS));
		
		/*if(renderList != null){
			for(int i = 0; i < renderList.length; i++){
	            // If we are using hardware buffers and the screen lost context
	            // then the buffer indexes that we recorded previously are now
	            // invalid.  Forget them here and recreate them below.
	            
	            // Load our texture and set its texture name on all sprites.
	            
	            int lastTextureId = -1;
	            textureMap.clear();
	            for (int x = 0; x < renderList.length; x++) {
	                int resource = renderList[i].getResourceId();
	                if (!textureMap.containsKey(resource)) {
						lastTextureId = loadBitmap(mContext, gl, resource);
						textureMap.put(resource, lastTextureId);
	                }
	                renderList[i].setTextureName(lastTextureId);
	            }
	        }
		}
		*/
		glContext = gl;
		nativeSurfaceCreated();
		nativeAllocTextureBuffers(texLib.size());
		lock1.release();
	}
	
	public void finalizeSprites() throws InterruptedException {
		lock1.acquire();
		//TODO verify that the data inside sprites are
		//proper.
		
		/*int listSize = 0;
		for(int i = 0; i < sprites.length; i++){
			if(sprites[i] != null)
				listSize += sprites[i].length;
		}

		renderList = new Sprite[listSize];

		for(int i = 0, j = 0; i < sprites.length; i++){
			if(sprites[i] != null){
				for(int k = 0; k < sprites[i].length; k++){
					renderList[j] = sprites[i][k];
					j++;
				}
			}
		}
		*/
		
		//Do some simple verification of the data inside the sprites.
		
		for(int i = 0; i < sprites.length; i++){
			
			if(sprites[i] == null)
				continue;
			
			for(int j = 0; j < sprites[i].length-1; j++){
				if(sprites[i][j].getSubType() == sprites[i][j+1].getSubType() &&
						!sprites[i][j].equals(sprites[i][j+1])){
				
					Log.e("NATIVE RENDER", "Inconsistent data in the same subtype");
				}
			}
		}
		
		//This needs to run on the render Thread to get access to the glContext.
		view.queueEvent(new Runnable(){
			//@Override
			public void run() {
				for(int i = 0; i < sprites.length; i++){
					if(sprites[i] != null)
						nativeDataPoolSize(i,sprites[i].length);
					else
						nativeDataPoolSize(i, 0);
				}
				for(int j = 0; j < sprites.length; j++){
					if(sprites[j] == null)
						continue;
					
					for(int i = 0; i < sprites[j].length; i++){
						nativeAlloc(i, sprites[j][i]);
						//Try to load textures
						int resource = sprites[j][i].getResourceId();
						if(resource == 0){
							Log.d("FIN SPRITES", "Error Invalid resource ID");
						}
						if (!textureMap.containsKey(resource)) {
							TextureData d = loadTexture(resource);
							sprites[j][i].setCurrentTexture(d);
						}else{
							sprites[j][i].setCurrentTexture(textureMap.get(resource));
						}
					}
				}
				lock2.release();
			}
		});
		lock2.acquire();
		lock1.release();
		//End of code that needs to run in the render thread.
	}
	
	public void setSprites(Sprite[] spriteArray, int type){
		sprites[type] = spriteArray;
	}
	
	/**
	 * Frees all allocated sprite data in the render.
	 * this means nothing is left on the screen after this.
	 * 
	 * However All textures remain in their buffers. And can be reused.
	 * @throws InterruptedException 
	 */
	public void freeSprites() throws InterruptedException{
		lock1.acquire();
		view.queueEvent(new Runnable(){
			//@Override
			public void run() {
				nativeFreeSprites();
				lock2.release();
			}
		});

		//this.renderList = null;
		lock2.acquire();
		lock1.release();
	}
	/**
	 * This loads a texture into the render for drawing,
	 * needs to be done before use. If the texture is already
	 * loaded it just returns the texture name without loading
	 * again.
	 * 
	 * Takes the resource id of the sprite and returns the
	 * internal texture name;
	 * 
	 * 
	 * @param resourceId
	 * @return textureName
	 * @throws InterruptedException 
	 */
	private TextureData loadTexture(int rId){
		final int resourceId = rId;
		int lastTextureId = 0;
		if (!textureMap.containsKey(resourceId)) {
			lastTextureId = loadBitmap(mContext, glContext, resourceId);
			TextureData d = new TextureData(lastTextureId, texLib.getFrameData(resourceId));
			textureMap.put(resourceId, d);
			nativeSetTextureBuffer(d);
			return d;
		}
		return null;
	}
	
	public void preloadTextureLibrary() throws InterruptedException{
		lock1.acquire();
		view.queueEvent(new Runnable(){
			//@Override
			public void run() {
				Iterator<Integer> it = texLib.textureResourceIdIterator();
				while(it.hasNext()){
					loadTexture(it.next());
				}
				lock2.release();
			}
		});

		//this.renderList = null;
		lock2.acquire();
		lock1.release();
	}
		
	/**
	 * This frees the texture specified by resource id
	 * from the buffers.
	 * 
	 * Use full when you want to reuse most of the allocated
	 * textures for a new wave and want to avoid reloading all of them.
	 * 
	 * @param resourceId
	 * @throws InterruptedException 
	 */
	public void freeTexture(int resourceId) throws InterruptedException{
		lock1.acquire();
		final int rId = resourceId;
		view.queueEvent(new Runnable(){
			//@Override
			public void run() {
				TextureData d = textureMap.get(rId);
				nativeFreeTex(d.mTextureName);
				textureMap.remove(rId);
				lock2.release();
			}
		});
		lock2.acquire();
		lock1.release();
	}
	
	/**
	 * Free all textures from buffers.
	 * To draw anything after this new textures must be loaded.
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	public void freeAllTextures() throws InterruptedException{	
		lock1.acquire();
		final HashMap<Integer,TextureData> map = (HashMap<Integer, TextureData>) textureMap.clone();
		textureMap.clear();

		view.queueEvent(new Runnable(){
			//@Override
			public void run() {

				Iterator<TextureData> it = map.values().iterator();
				while(it.hasNext()){
					nativeFreeTex(it.next().mTextureName);
				}
				lock2.release();
			}
		});
		lock2.acquire();
		lock1.release();
	}
	
	/**
	 * Takes the resourceId of a sprite and 
	 * returns the texture name if it exist.
	 * @param resourceId
	 * @return
	 * @throws InterruptedException 
	 */
	public TextureData getTexture(int resourceId) throws InterruptedException{
		lock1.acquire();
		TextureData data = textureMap.get(resourceId);
		lock1.release();
		return data;
	}
	
	private int loadBitmap(Context context, GL10 gl, int resourceId) {
		Log.d("JAVA_LOADTEXTURE","Resource id:"+resourceId);
		
		
        int textureName = -1;
        if (context != null && gl != null) {
            gl.glGenTextures(1, mTextureNameWorkspace, 0);

            textureName = mTextureNameWorkspace[0];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

            gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

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
                Log.e("JAVA_LOADTEXTURE", "Texture Load GLError: " + error);
            }
        
        }
        Log.d("JAVA_LOADTEXTURE", "Loading texture, id is: " + textureName);
        return textureName;
    }
}
