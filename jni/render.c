#include "render.h"
#define LOG_TAG "NATIVE_RENDER"


	//The number of idividual sprites.
int noOfSprites = 0;
	//Array with pointers to GLSprites.
GLSprite* renderSprites;

	//GLuint* textureNameWorkspace;
	//GLuint* cropWorkspace;


void Java_com_crackedcarrot_NativeRender_nativeDataPoolSize(JNIEnv* env,
															jobject thiz, 
															jint size){
                                                                  
    noOfSprites = size;
    renderSprites = malloc(sizeof(GLSprite) * noOfSprites);
		//textureNameWorkspace = malloc(sizeof(GLuint) * 1);
		//cropWorkspace = malloc(sizeof(GLuint) * 1);
	
    __android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE ALLOC",
						"Allocating memory pool for Sprites of size %d\n ", 
						noOfSprites);
}

void Java_com_crackedcarrot_NativeRender_nativeAlloc(JNIEnv*  env, 
													 jobject thiz, 
													 jint spriteNO, 
													 jobject sprite){
	
	__android_log_print(ANDROID_LOG_DEBUG, "NATIVE ALLOC",
						"Loading Texture for SpriteNo %d \n", spriteNO);
	
	GLSprite* sprites = renderSprites;
	
	sprites[spriteNO].object = (*env)->NewGlobalRef(env,sprite);
	
		//Get GLSprite class and renderable class.
	jclass class = (*env)->GetObjectClass(env, sprite);
		//jclass super = (*env)->GetSuperclass(env, class);
	
		//cache the x,y,z pos ID
	jfieldID id = (*env)->GetFieldID(env, class, "x", "F");
	sprites[spriteNO].x = id;
	id = (*env)->GetFieldID(env, class, "y", "F");
	sprites[spriteNO].y = id;
	id = (*env)->GetFieldID(env, class, "z", "F");
	sprites[spriteNO].z = id;
	
		//cache the width/height IDs
	id = (*env)->GetFieldID(env, class, "width", "F");
	sprites[spriteNO].width = id;
	id = (*env)->GetFieldID(env, class, "height", "F");
	sprites[spriteNO].height = id;
	
	id = (*env)->GetFieldID(env, class, "draw", "Z");
	sprites[spriteNO].draw = id;
		//cache TextureName
	id = (*env)->GetFieldID(env, class, "mTextureName", "I");
	sprites[spriteNO].textureName = id;
	
	sprites[spriteNO].vertBuffer = malloc(sizeof(GLfloat) * 4 * 3);
	sprites[spriteNO].textureCoordBuffer = malloc(sizeof(GLfloat) * 4 * 2);
	sprites[spriteNO].indexBuffer = malloc(sizeof(GLuint) * 6);
	sprites[spriteNO].indexCount = 6;
	
	GLuint* indexBuffer = sprites[spriteNO].indexBuffer;
	
	indexBuffer[0] = 0;
	indexBuffer[1] = 1;
	indexBuffer[2] = 2;
	indexBuffer[3] = 3;
	indexBuffer[4] = 2;
	indexBuffer[5] = 1;
	
	GLfloat* vertBuffer = sprites[spriteNO].vertBuffer;
	
		
	/*__android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE ALLOC", 
						"Texture X:%f Texture Y:%f Texture Z:%f\n",
						(*env)->GetFloatField(env,sprites[spriteNO].object,sprites[spriteNO].x),
						(*env)->GetFloatField(env,sprites[spriteNO].object,sprites[spriteNO].y),
						(*env)->GetFloatField(env,sprites[spriteNO].object,sprites[spriteNO].z));*/
	
	/*__android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE ALLOC", 
						"The texture id is: ‰d",
						(*env)->GetIntField(env,sprites[spriteNO].object, sprites[spriteNO].textureName));*/
						
						
	
}

void Java_com_crackedcarrot_NativeRender_nativeResize(JNIEnv*  env, jobject  thiz, jint w, jint h){
		
	glViewport(0, 0, w, h);
	/*
	 * Set our projection matrix. This doesn't have to be done each time we
	 * draw, but usually a new projection needs to be set when the viewport
	 * is resized.
	 */
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrthof(0.0f, w, 0.0f, h, 0.0f, 1.0f);
	
	glShadeModel(GL_FLAT);
	glEnable(GL_BLEND);
	glEnable(GL_DEPTH_TEST);
	glDepthMask(GL_TRUE);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	glEnable(GL_TEXTURE_2D);
	glMatrixMode(GL_MODELVIEW);
}

void Java_com_crackedcarrot_NativeRender_nativeDrawFrame(JNIEnv*  env){
	
    int i;
	GLSprite* sprites = renderSprites;
	GLint prevTexture = -1;
	GLint currTexture = 0;
	GLfloat* vertBuffer;
	GLfloat* textureCoordBuffer;
	GLuint* indexBuffer;
	GLuint  indexCount;
		//	glMatrixMode(GL_MODELVIEW);
	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	
	for (i = 0; i < noOfSprites; i++) {
		//__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, "Drawing sprite no:%d of a total:%d of type %d !\n", j, noOfType[i], i);
		
		if((*env)->GetBooleanField(env,sprites[i].object, sprites[i].draw)){
			
			vertBuffer 			= sprites[i].vertBuffer;
			textureCoordBuffer 	= sprites[i].textureCoordBuffer;
			indexBuffer			= sprites[i].indexBuffer;
			indexCount			= sprites[i].indexCount;
			currTexture = (*env)->GetIntField(env,sprites[i].object, sprites[i].textureName);
			if(currTexture != prevTexture){ 
				glBindTexture(GL_TEXTURE_2D, currTexture);
				prevTexture = currTexture;
			}
		
			glPushMatrix();
			glLoadIdentity();
			glTranslatef((*env)->GetFloatField(env, sprites[i].object, sprites[i].x),
						(*env)->GetFloatField(env, sprites[i].object, sprites[i].y),
						(*env)->GetFloatField(env, sprites[i].object, sprites[i].z));
						
			glBindBuffer(GL_ARRAY_BUFFER, *vertBuffer);
			glVertexPointer(3, GL_FLOAT, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, *textureCoordBuffer);
			glTexCoordPointer(2, GL_FLOAT, 0, 0);
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, *indexBuffer);
			glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_SHORT, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			
			glPopMatrix();
			
			glDisableClientState(GL_VERTEX_ARRAY);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		}
    }
}

void Java_com_crackedcarrot_NativeRender_nativeSurfaceCreated(JNIEnv*  env){
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
	glClearColor(0.5f, 0.5f, 0.5f, 1);
	glShadeModel(GL_FLAT);
	glEnable(GL_TEXTURE_2D);
	/*
	 * By default, OpenGL enables features that improve quality but reduce
	 * performance. One might want to tweak that especially on software
	 * renderer.
	 */
	glDisable(GL_DITHER);
	glDisable(GL_LIGHTING);

	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
	__android_log_print(ANDROID_LOG_DEBUG, "NATIVE_SURFACE_CREATE", "The surface has been created.\n");

}

void Java_com_crackedcarrot_NativeRender_nativeFreeSprites(JNIEnv* env){
	noOfSprites = 0;
}

/*jint Java_com_crackedcarrot_NativeRender_nativeLoadTexture(JNIEnv* env, jobject thiz, ????){
	
	GLuint* texture;
	
	glGenTextures(1, textureNameWorkspace, 0);
	texture = textureNameWorkspace[0];
	
	glBindTexture(GL_TEXTURE_2D, texture);
	
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
	
	texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
	
	mCropWorkspace[0] = 0;
	mCropWorkspace[1] = bitmap.getHeight();
	mCropWorkspace[2] = bitmap.getWidth();
	mCropWorkspace[3] = -bitmap.getHeight();
	
	return *texture;
}*/
