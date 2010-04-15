#include "render.h"

void Java_com_crackedcarrot_NativeRender_nativeDataPoolSize(JNIEnv* env,
															jobject thiz, 
															jint type, 
															jint size){

                                                                  
    noOfSprites[type] = size;
    if(noOfSprites[type] > 0){
        renderSprites[type] = malloc(sizeof(GLSprite) * noOfSprites[type]);

    }
    __android_log_print(ANDROID_LOG_DEBUG, 
		    				"NATIVE ALLOC",
		    				"Allocating memory pool for Sprites, Type %d of size %d ", 
		    				type, noOfSprites[type]);
}

void Java_com_crackedcarrot_NativeRender_nativeAlloc(JNIEnv*  env, 
													 jobject thiz, 
													 jint spriteNO, 
													 jobject sprite){
													
	//Get the class and read the type info
	jclass class = (*env)->GetObjectClass(env, sprite);
	jfieldID id = (*env)->GetFieldID(env, class, "type", "I");
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
	id = (*env)->GetFieldID(env, class, "x", "F");
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
	
	id = (*env)->GetFieldID(env, class, "nFrames", "I");
	thisSprite->nFrames = id;
	id = (*env)->GetFieldID(env, class, "cFrame", "I");
	thisSprite->cFrame = id;
	
		//cache TextureName
	id = (*env)->GetFieldID(env, class, "mTextureName", "I");
	thisSprite->textureName = id;
	
	thisSprite->bufferName = malloc(sizeof(GLuint)*2);
	
	thisSprite->bufferName[0] = 0; 
	thisSprite->bufferName[1] = 0;
	thisSprite->textureBufferNames = NULL;
	
	//If this is not the first sprite of its type and its not an animation
	//We can just use the same VBOs as the last sprite.
    GLSprite* last = NULL;
	if(spriteNO != 0){
		last = &renderSprites[thisSprite->type][spriteNO-1];
	}
	if(last != NULL && last->subType == thisSprite->subType){
		thisSprite->bufferName = last->bufferName;
		thisSprite->textureBufferNames = last->textureBufferNames;
		thisSprite->indexCount = last->indexCount;
		/*__android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE_ALLOC", 
						"VBOs EQUAL: Vert: %d, Index: %d, Tex: %d",
						thisSprite->bufferName[VERT_OBJECT] == last->bufferName[VERT_OBJECT],
						thisSprite->bufferName[INDEX_OBJECT] == last->bufferName[INDEX_OBJECT],
						thisSprite->textureBufferNames == last->textureBufferNames);*/
		
/*		__android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE ALLOC",
						"Sprite No: %d of Type: %d and subType: %d .  Can share data with the previous sprite",
						 spriteNO, type, subType);
		__android_log_print(ANDROID_LOG_DEBUG, 
						"NATIVE ALLOC",
						"It has been assigned the buffers: %d, %d and %d",
						 thisSprite->bufferName[0], thisSprite->bufferName[1], thisSprite->textureBufferNames[0]);*/
	}
	else{
	    __android_log_print(ANDROID_LOG_DEBUG, 
		                "NATIVE ALLOC", 
		                "Sprite No: %d of Type: %d and subType: %d .   Needs new buffers.",
		                spriteNO, type, subType);
		initHwBuffers(env, thisSprite);
	}

    //Class specific code
	if(type == CREATURE){
	    jclass creature = (*env)->FindClass(env, "com/crackedcarrot/Creature");

	    if(creature == NULL){
            __android_log_print(ANDROID_LOG_FATAL, "NATIVE ALLOC", "Failed to get creature class");
	    }

	    if((*env)->IsInstanceOf(env, thisSprite->object, creature)){
            thisSprite->crExtens = malloc(sizeof(crExtensions));
            thisSprite->crExtens->dead = (*env)->GetFieldID(env, creature, "dead", "Z");

	    }
	    else{
            __android_log_print(ANDROID_LOG_FATAL, "NATIVE ALLOC", "ERROR, java class and typeVar does not MATCH!!");
	    }
	}
	else{
        thisSprite->crExtens = NULL;
	}
}

void initHwBuffers(JNIEnv* env, GLSprite* sprite){
	GLsizeiptr vertBufSize;
	GLsizeiptr textCoordBufSize;
	GLsizeiptr indexBufSize;
	
	vertBufSize = sizeof(GLfloat) * 4 * 3;
	textCoordBufSize = sizeof(GLfloat) * 4 * 2;
	indexBufSize = sizeof(GLushort) * 6;
	
	GLfloat vertBuffer[4*3];
	GLfloat textureCoordBuffer[4*2];
	GLushort  indexBuffer[6];
	sprite->indexCount = 6;	
	
	//This be the vertex order for our quad, its totaly square man.
	indexBuffer[0] = 0;
	indexBuffer[1] = 1;
	indexBuffer[2] = 2;
	indexBuffer[3] = 1;
	indexBuffer[4] = 2;
	indexBuffer[5] = 3;
		
	GLfloat width = (*env)->GetFloatField(env, sprite->object, sprite->width);
	GLfloat height = (*env)->GetFloatField(env, sprite->object, sprite->height);

	__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "New vertBuffer with sizes width: %f, height: %f", width, height);
		
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
	
	glGenBuffers(2, sprite->bufferName);
	//__android_log_print(ANDROID_LOG_DEBUG, "HWBUFFER ALLOC", "GenBuffer retured error: %d", glGetError());
	glBindBuffer(GL_ARRAY_BUFFER, sprite->bufferName[VERT_OBJECT]);
	glBufferData(GL_ARRAY_BUFFER, vertBufSize, vertBuffer, GL_STATIC_DRAW);
	
	//Texture Coords
	
	int frames = (*env)->GetIntField(env, sprite->object, sprite->nFrames);
	sprite->textureBufferNames = malloc(frames * sizeof(GLuint));
	glGenBuffers(frames, sprite->textureBufferNames);
	GLfloat texFraction = 1.0 / frames;
	GLfloat startFraction = 0.0;
	GLfloat endFraction;
	int i;
	for(i = 0; i < frames; i++){
		endFraction = startFraction + texFraction;
		__android_log_print(ANDROID_LOG_DEBUG, 
		                "HWBUFFER ALLOC", 
		                "Allocating texCoords Set No: %d from %f to %f", 
		                i ,startFraction, endFraction );
		textureCoordBuffer[0] = startFraction; 	textureCoordBuffer[1] = 1.0;
		textureCoordBuffer[2] = endFraction; 	textureCoordBuffer[3] = 1.0;
		textureCoordBuffer[4] = startFraction; 	textureCoordBuffer[5] = 0.0;
		textureCoordBuffer[6] = endFraction; 	textureCoordBuffer[7] = 0.0;
		glBindBuffer(GL_ARRAY_BUFFER, sprite->textureBufferNames[i]);
		glBufferData(GL_ARRAY_BUFFER, textCoordBufSize, textureCoordBuffer,GL_STATIC_DRAW);
		startFraction = endFraction;
	}
	
	glBindBuffer(GL_ARRAY_BUFFER, 0);
	
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sprite->bufferName[INDEX_OBJECT]);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufSize, indexBuffer, GL_STATIC_DRAW);
	
	__android_log_print(ANDROID_LOG_DEBUG, 
		                "HWBUFFER ALLOC", 
		                "Sprite has been assigned the new buffers: %d, %d and %d", 
		                sprite->bufferName[INDEX_OBJECT] ,sprite->bufferName[VERT_OBJECT], sprite->textureBufferNames[0] );

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

}

void Java_com_crackedcarrot_NativeRender_nativeFreeSprites(JNIEnv* env){
	GLSprite* currSprt;
	int spritesToFree;
	int i;
	int j;
	
	for(j = 0; j < 6; j++){
		spritesToFree = noOfSprites[j];
		for(i = 0; i < spritesToFree; i++){
			currSprt = &renderSprites[j][i];
			__android_log_print(ANDROID_LOG_DEBUG, "NATIVE_FREE_SPRITES", "Freeing sprite %d:%d", j,i);
			(*env)->DeleteGlobalRef(env, currSprt->object);
			if(currSprt->textureBufferNames != NULL){
				glDeleteBuffers((*env)->GetIntField(env, currSprt->object, currSprt->nFrames),
				 				currSprt->textureBufferNames);
				//free(currSprt->textureBufferNames);
				currSprt->textureBufferNames = NULL;
			}
			if(currSprt->bufferName != NULL){
				glDeleteBuffers(2, currSprt->bufferName);
				//free(currSprt->bufferName);
				currSprt->bufferName = NULL;
			}
		}
	
		free(renderSprites[j]);
		noOfSprites[j] = 0;
	}
}

void Java_com_crackedcarrot_NativeRender_nativeFreeTex(JNIEnv* env, jobject thiz, jint textureName){
	glDeleteTextures(1, &textureName);
}
