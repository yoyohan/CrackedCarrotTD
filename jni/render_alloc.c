#include "render.h"

//#define emulator

void Java_com_crackedcarrot_NativeRender_nativeDataPoolSize(JNIEnv* env,
															jobject thiz, 
															jint type, 
															jint size){
                                                                  
    noOfSprites[type] = size;
    renderSprites[type] = malloc(sizeof(GLSprite) * noOfSprites[type]);
		//textureNameWorkspace = malloc(sizeof(GLuint) * 1);
		//cropWorkspace = malloc(sizeof(GLuint) * 1);
	
    __android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE ALLOC",
						"Allocating memory pool for Sprites of size %d ", 
						noOfSprites);
}

void Java_com_crackedcarrot_NativeRender_nativeAlloc(JNIEnv*  env, 
													 jobject thiz, 
													 jint spriteNO, 
													 jobject sprite){
	
													
	//Get the class and read the type info
	jclass class = (*env)->GetObjectClass(env, sprite);
	id = (*env)->GetFieldID(env, class, "type", "I");
	jint type = (*env)->GetIntField(env, sprite, id);
	
	//Use type to figure out what element to manipulate
	GLSprite* thisSprite = &renderSprites[type][spriteNO];
	//Set a variable, dont cache reference as we do with the rest of the members of the
	//sprite class, since the type is imutable.
	thisSprite->type = type;
	
	id = (*env)->GetFieldID(env, class, "subType", "I");
	jint subType = (*env)->GetIntField(env, sprite, id);
	thisSprite->subType = subType;
	
	//Cache reference to this object
	thisSprite->object = (*env)->NewGlobalRef(env,sprite);
	
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
	id = (*env)->GetFieldID(env, class, "scale", "F");
	thisSprite->scale = id;
	
	id = (*env)->GetFieldID(env, class, "draw", "Z");
	thisSprite->draw = id;
	
	id = (*env)->GetFieldID(env, class, "r", "F");
	thisSprite->r = id;
	id = (*env)->GetFieldID(env, class, "g", "F");
	thisSprite->g = id;
	id = (*env)->GetFieldID(env, class, "b", "F");
	thisSprite->b = id;
	id = (*env)->GetFieldID(env, class, "opacity", "F");
	thisSprite->opacity = id;
	
		//cache TextureName
	id = (*env)->GetFieldID(env, class, "mTextureName", "I");
	thisSprite->textureName = id;
	
	thisSprite->bufferName[0] = 0; 
	thisSprite->bufferName[1] = 0;
	thisSprite->bufferName[2] = 0;
	
	//If this is not the first sprite of its type and its not an animation
	//We can just use the same VBOs as the last sprite.
	GLSprite* last = NULL;
	if(spriteNO != 0){
		last = &renderSprites[type][spriteNO-1]
	}
	if(last != NULL && last->subType == thisSprite->subType){
		thisSprite->bufferName[0] = last->bufferName[0]; 
		thisSprite->bufferName[1] = last->bufferName[1];
		thisSprite->bufferName[2] = last->bufferName[2];
	}
	else{
		initHwBuffers(env, thisSprite, type);
	}	
	//spritesReady = 1;	
}

void initHwBuffers(JNIEnv* env, GLSprite* sprite, jint type){
	
	GLsizeiptr vertBufSize;
	GLsizeiptr textCoordBufSize;
	GLsizeiptr indexBufSize;
	
	vertBufSize = sizeof(GLfloat) * 4 * 3;
	textCoordBufSize = sizeof(GLfloat) * 4 * 2;
	indexBufSize = sizeof(GLuint) * 6;
	
	GLfloat vertBuffer[4*3];
	GLfloat textureCoordBuffer[4*2];
	GLuint  indexBuffer[6];
	sprite->indexCount = 6;	
	
	//This be the vertex order for our quad, its totaly square man.
	indexBuffer[0] = 0;
	indexBuffer[1] = 1;
	indexBuffer[2] = 2;
	indexBuffer[3] = 1;
	indexBuffer[4] = 2;
	indexBuffer[5] = 3;
		
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
	
	//Texture Coords
	
	textureCoordBuffer[0] = 0.0; textureCoordBuffer[1] = 1.0;
	textureCoordBuffer[2] = 1.0; textureCoordBuffer[3] = 1.0;
	textureCoordBuffer[4] = 0.0; textureCoordBuffer[5] = 0.0;
	textureCoordBuffer[6] = 1.0; textureCoordBuffer[7] = 0.0;
	
	
	/*float w = (*env)->GetFloatField(env, sprite->object, sprite->width);
	float h = (*env)->GetFloatField(env, sprite->object, sprite->height);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "sprite has width: %f and hegiht %f", w,h);
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
	glBufferData(GL_ARRAY_BUFFER, vertBufSize, vertBuffer, GL_STATIC_DRAW);
	
	glBindBuffer(GL_ARRAY_BUFFER, sprite->bufferName[TEX_OBJECT]);
	glBufferData(GL_ARRAY_BUFFER, textCoordBufSize, textureCoordBuffer,GL_STATIC_DRAW);
	
	glBindBuffer(GL_ARRAY_BUFFER, 0);
	
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sprite->bufferName[INDEX_OBJECT]);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufSize, indexBuffer, GL_STATIC_DRAW);
	
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	
	/*__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "IndexObject %d", sprite->bufferName[INDEX_OBJECT]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "VertObject  %d", sprite->bufferName[VERT_OBJECT]);
	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "TextureObject %d", sprite->bufferName[TEX_OBJECT]);
	*/
}

void Java_com_crackedcarrot_NativeRender_nativeFreeSprites(JNIEnv* env){
	GLSprite* freeSprites = renderSprites;
	GLSprite* currentSprite;
	int spritesToFree = noOfSprites;
	int i;
	
	noOfSprites = 0;
	renderSprites = NULL;
	
	for(i = 0; i < spritesToFree; i++){
		currentSprite = &freeSprites[i];
		__android_log_print(ANDROID_LOG_DEBUG, "NATIVE_FREE_SPRITES", "Freeing sprite %d",currentSprite->bufferName[INDEX_OBJECT]);
		free(currentSprite->vertBuffer);
		free(currentSprite->textureCoordBuffer);
		free(currentSprite->indexBuffer);
		
		#ifndef emulator
		glDeleteBuffers(3, currentSprite->bufferName);
		#endif
		(*env)->DeleteGlobalRef(env, currentSprite->object);
	}
	
	free(freeSprites);
	freeSprites = NULL;
}

void Java_com_crackedcarrot_NativeRender_nativeFreeTex(JNIEnv* env, jobject thiz, jint textureName){
	glDeleteTextures(1, &textureName);
}
