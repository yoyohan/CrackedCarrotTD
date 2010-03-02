#include "render.h"
#define LOG_TAG "NATIVE_RENDER"


	//The number of idividual sprites.
int noOfSprites = 0;
	//Array with pointers to GLSprites.
GLSprite* renderSprites = NULL;
int spritesReady = 0;

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
	
	//__android_log_print(ANDROID_LOG_DEBUG, "NATIVE ALLOC",
	//					"Loading Texture for SpriteNo %d \n", spriteNO);
	GLSprite* thisSprite = &renderSprites[spriteNO];			
	
	thisSprite->object = (*env)->NewGlobalRef(env,sprite);
	
		//Get GLSprite class and renderable class.
	jclass class = (*env)->GetObjectClass(env, sprite);
		//jclass super = (*env)->GetSuperclass(env, class);
	
		//cache the x,y,z pos ID
	jfieldID id = (*env)->GetFieldID(env, class, "x", "F");
	thisSprite->x = id;
	id = (*env)->GetFieldID(env, class, "y", "F");
	thisSprite->y = id;
	id = (*env)->GetFieldID(env, class, "z", "F");
	thisSprite->z = id;
	
		//cache the width/height IDs
	id = (*env)->GetFieldID(env, class, "width", "F");
	thisSprite->width = id;
	id = (*env)->GetFieldID(env, class, "height", "F");
	thisSprite->height = id;
	
	id = (*env)->GetFieldID(env, class, "draw", "Z");
	thisSprite->draw = id;
		//cache TextureName
	id = (*env)->GetFieldID(env, class, "mTextureName", "I");
	thisSprite->textureName = id;
	
	thisSprite->vertBufSize = sizeof(GLfloat) * 4 * 3;
	thisSprite->textCoordBufSize = sizeof(GLfloat) * 4 * 2;
	thisSprite->indexBufSize = sizeof(GLuint) * 6;
	
	thisSprite->vertBuffer = malloc(thisSprite->vertBufSize);
	thisSprite->textureCoordBuffer = malloc(thisSprite->textCoordBufSize);
	thisSprite->indexBuffer = malloc(thisSprite->indexBufSize);
	thisSprite->indexCount = 6;
	
	GLushort* indexBuffer = thisSprite->indexBuffer;
	
	
	//This be the vertex order for our quad, its totaly square man.
	indexBuffer[0] = 0;
	indexBuffer[1] = 1;
	indexBuffer[2] = 2;
	indexBuffer[3] = 1;
	indexBuffer[4] = 2;
	indexBuffer[5] = 3;
	
	GLfloat* vertBuffer = thisSprite->vertBuffer;
	
	GLfloat width = (*env)->GetFloatField(env, thisSprite->object, thisSprite->width);
	GLfloat height = (*env)->GetFloatField(env, thisSprite->object, thisSprite->height);
		
	//VERT1
	vertBuffer[0] = 0.0;
	vertBuffer[1] = 0.0;
	vertBuffer[2] = 0.0;	
	//VERT2
	vertBuffer[3] = width;
	vertBuffer[4] = 0.0;
	vertBuffer[5] = 0.0;
	//VERT3
	vertBuffer[6] = 0.0;
	vertBuffer[7] = height;
	vertBuffer[8] = 0.0;
	//VERT4
	vertBuffer[9] = width;
	vertBuffer[10] = height;
	vertBuffer[11] = 0.0;
	//WOOO I CAN HAS QUAD!
	
	
	GLfloat* textureCoordBuffer = thisSprite->textureCoordBuffer;
	//Texture Coords
	textureCoordBuffer[0] = 0.0; textureCoordBuffer[1] = 1.0;
	textureCoordBuffer[2] = 1.0; textureCoordBuffer[3] = 1.0;
	textureCoordBuffer[4] = 0.0; textureCoordBuffer[5] = 0.0;
	textureCoordBuffer[6] = 1.0; textureCoordBuffer[7] = 0.0;
	
	thisSprite->bufferName[0] = 0; 
	thisSprite->bufferName[1] = 0;
	thisSprite->bufferName[2] = 0;
	
	
	//Init of our quad is done.
	
	initHwBuffers(env, thisSprite);
	
	//spritesReady = 1;	
}

void initHwBuffers(JNIEnv* env, GLSprite* sprite){
	
	
	float w = (*env)->GetFloatField(env, sprite->object, sprite->width);
	float h = (*env)->GetFloatField(env, sprite->object, sprite->height);
	/*__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "sprite has width: %f and hegiht %f", w,h);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "init HW buffers useing data:");
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "Indices : %d %d %d %d %d %d",
						sprite->indexBuffer[0], sprite->indexBuffer[1], sprite->indexBuffer[2],
						sprite->indexBuffer[3],sprite->indexBuffer[4],sprite->indexBuffer[5]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "Vert 0: %f,%f,%f", sprite->vertBuffer[0],sprite->vertBuffer[1],sprite->vertBuffer[2]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "Vert 1: %f,%f,%f", sprite->vertBuffer[3],sprite->vertBuffer[4],sprite->vertBuffer[5]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "Vert 3: %f,%f,%f", sprite->vertBuffer[6],sprite->vertBuffer[7],sprite->vertBuffer[8]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "Vert 4: %f,%f,%f", sprite->vertBuffer[9],sprite->vertBuffer[10],sprite->vertBuffer[11]);
	*/
	glGenBuffers(3, sprite->bufferName);
	//__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "GenBuffer retured error: %d", glGetError());
	glBindBuffer(GL_ARRAY_BUFFER, sprite->bufferName[VERT_OBJECT]);
	glBufferData(GL_ARRAY_BUFFER, sprite->vertBufSize, sprite->vertBuffer, GL_STATIC_DRAW);
	
	glBindBuffer(GL_ARRAY_BUFFER, sprite->bufferName[TEX_OBJECT]);
	glBufferData(GL_ARRAY_BUFFER, sprite->textCoordBufSize, sprite->textureCoordBuffer,GL_STATIC_DRAW);
	
	glBindBuffer(GL_ARRAY_BUFFER, 0);
	
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sprite->bufferName[INDEX_OBJECT]);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, sprite->indexBufSize, sprite->indexBuffer, GL_STATIC_DRAW);
	
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	
	/*__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "IndexObject %d", sprite->bufferName[INDEX_OBJECT]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "VertObject  %d", sprite->bufferName[VERT_OBJECT]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "TextureObject %d", sprite->bufferName[TEX_OBJECT]);
	*/
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
	glOrthof(0.0f, w, 0.0f, h, 0.0f, 10.0f);
	
	glShadeModel(GL_FLAT);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	
	glEnable(GL_BLEND);
	//glEnable(GL_DEPTH_TEST);
	glEnable(GL_TEXTURE_2D);
	
	/*
	 * By default, OpenGL enables features that improve quality but reduce
	 * performance. One might want to tweak that especially on software
	 * renderer.
	 */
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_DITHER);
	glDisable(GL_LIGHTING);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
	
	glClearColor(0.5f, 0.5f, 0.5f, 1);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_MODELVIEW);
}

void Java_com_crackedcarrot_NativeRender_nativeDrawFrame(JNIEnv*  env){
	
    int i;
    
    GLuint* bufferName;
    GLint currTexture = -1;
    GLint prevTexture = -2;
    
	/*GLfloat* vertBuffer;
	GLfloat* texCoordBuffer;
	GLushort* indexBuffer;
	GLshort indexCount;
	*/
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	
	for (i = 0; i < noOfSprites; i++) {		
		if((*env)->GetBooleanField(env,renderSprites[i].object, renderSprites[i].draw)){
			
			bufferName = renderSprites[i].bufferName;
			
			
/*			vertBuffer = renderSprites[i].vertBuffer;
			texCoordBuffer = renderSprites[i].textureCoordBuffer;
			indexBuffer = renderSprites[i].indexBuffer;
			indexCount = renderSprites[i].indexCount;
*/			
			
			currTexture = (*env)->GetIntField(env,renderSprites[i].object, renderSprites[i].textureName);
			if(currTexture != prevTexture){ 
			    glBindTexture(GL_TEXTURE_2D, currTexture);
				prevTexture = currTexture;
			}
		
			glPushMatrix();
			glLoadIdentity();
			glTranslatef((*env)->GetFloatField(env, renderSprites[i].object, renderSprites[i].x),
						(*env)->GetFloatField(env, renderSprites[i].object, renderSprites[i].y),
						(*env)->GetFloatField(env, renderSprites[i].object, renderSprites[i].z));
		
			glBindBuffer(GL_ARRAY_BUFFER, bufferName[VERT_OBJECT]);
			glVertexPointer(3, GL_FLOAT, 0, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, bufferName[TEX_OBJECT]);
			glTexCoordPointer(2, GL_FLOAT, 0, 0);
			
/*			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,"Texture:%d", currTexture);
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,"RENDER USEING DATA:");
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,"Verts: { %f,%f,%f} { %f,%f,%f} { %f,%f,%f} { %f,%f,%f}",
								vertBuffer[0],vertBuffer[1],vertBuffer[2],vertBuffer[3],
								vertBuffer[4],vertBuffer[5],vertBuffer[6],vertBuffer[7],
								vertBuffer[8],vertBuffer[9],vertBuffer[10],vertBuffer[11]);
											
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "TextCoords: {%f,%f} {%f,%f} {%f,%f} {%f,%f}",
								texCoordBuffer[0],texCoordBuffer[1],texCoordBuffer[2],texCoordBuffer[3],
								texCoordBuffer[4],texCoordBuffer[5],texCoordBuffer[6],texCoordBuffer[7]);
								
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Indices: {%u,%u,%u,%u,%u,%u}",
								indexBuffer[0],indexBuffer[1],indexBuffer[2],indexBuffer[3],
								indexBuffer[4],indexBuffer[5]);
			
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "IndexCount: %u", indexCount);
*/			
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName[INDEX_OBJECT]);
			glDrawElements(GL_TRIANGLES, renderSprites[i].indexCount, GL_UNSIGNED_SHORT, 0);
/*			
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "DrawElements returned error: %d ", glGetError());
*/			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			
			glPopMatrix();
		}
    }
	glDisableClientState(GL_VERTEX_ARRAY);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
}

void Java_com_crackedcarrot_NativeRender_nativeSurfaceCreated(JNIEnv*  env){
	__android_log_print(ANDROID_LOG_DEBUG, "NATIVE_SURFACE_CREATE", "The surface has been created.");
	
}

void Java_com_crackedcarrot_NativeRender_nativeFreeSprites(JNIEnv* env){
	noOfSprites = 0;
}
