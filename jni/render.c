#include "render.h"
#define LOG_TAG "NATIVE_RENDER"


	//The number of idividual sprites of the four differant kinds.
int noOfTypes = 4;
int noOfType[4];
	//Array with pointers to arrays of GLSprites, one array per kind of sprite.
GLSprite* typeSprites[4];

	//GLuint* textureNameWorkspace;
	//GLuint* cropWorkspace;


void Java_com_crackedcarrot_NativeRender_nativeDataPoolSize(JNIEnv* env,
															jobject thiz, 
															jint size,
															jint type){
                                                                  
    int* noOfSprites = &noOfType[type];
	*noOfSprites = size;
	
    typeSprites[type] = malloc(sizeof(GLSprite) * *noOfSprites);
		//textureNameWorkspace = malloc(sizeof(GLuint) * 1);
		//cropWorkspace = malloc(sizeof(GLuint) * 1);
	
    __android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE ALLOC",
						"Allocating memory pool for SpriteType %d of size %d\n ", 
						type, 
						noOfType[type]);
}

void Java_com_crackedcarrot_NativeRender_nativeAlloc(JNIEnv*  env, 
													 jobject thiz, 
													 jint spriteNO, 
													 jobject sprite, 
													 jint type){
	
	__android_log_print(ANDROID_LOG_DEBUG, "NATIVE ALLOC",
						"Loading Texture for SpriteType %d, SpriteNo %d \n", type, spriteNO);
	
	GLSprite* sprites = typeSprites[type];
	
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
	
		//cache TextureName
	id = (*env)->GetFieldID(env, class, "mTextureName", "I");
	sprites[spriteNO].textureName = id;
	
	
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
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	glEnable(GL_TEXTURE_2D);
	glMatrixMode(GL_MODELVIEW);
}

void Java_com_crackedcarrot_NativeRender_nativeDrawFrame(JNIEnv*  env){
	
    int i;
	int j;
	GLSprite* sprites;
	
		//	glMatrixMode(GL_MODELVIEW);
	
	for (i = 0; i < noOfTypes; i++) {
		sprites = typeSprites[i];
		for (j = 0; sprites != NULL && j < noOfType[i]; j++) {
				//__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, "Drawing sprite no:%d of a total:%d of type %d !\n", j, noOfType[i], i);
			
			glBindTexture(GL_TEXTURE_2D,
						  (*env)->GetIntField(env,sprites[j].object, sprites[j].textureName));
			
			glDrawTexfOES((*env)->GetFloatField(env,sprites[j].object, sprites[j].x)
						, (*env)->GetFloatField(env,sprites[j].object, sprites[j].y)
						, (*env)->GetFloatField(env,sprites[j].object, sprites[j].z)
						, (*env)->GetFloatField(env,sprites[j].object, sprites[j].width)
						, (*env)->GetFloatField(env,sprites[j].object, sprites[j].height));
		}
    }
}

void Java_com_crackedcarrot_NativeRender_nativeSurfaceCreated(JNIEnv*  env){
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
	glClearColor(0.5f, 0.5f, 0.5f, 1);
	glShadeModel(GL_FLAT);
	glDisable(GL_DEPTH_TEST);
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
