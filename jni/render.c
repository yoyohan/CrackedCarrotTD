#include "render.h"
#define LOG_TAG "NATIVE_RENDER"

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

	glEnable(GL_BLEND);
	glEnable(GL_TEXTURE_2D);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);	
	
	/*
	 * By default, OpenGL enables features that improve quality but reduce
	 * performance. One might want to tweak that especially on software
	 * renderer.
	 */
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_DITHER);
	glDisable(GL_LIGHTING);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
	
	glClearColor(1, 1, 1, 1);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_MODELVIEW);
}

void Java_com_crackedcarrot_NativeRender_nativeDrawFrame(JNIEnv*  env){
	
    int i;
    
    GLuint* bufferName;
    GLint currTexture = -1;
    GLint prevTexture = -2;
	GLSprite* currSprt = NULL;
    
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
			currSprt = &renderSprites[i];
			bufferName = currSprt->bufferName;			
			
			currTexture = (*env)->GetIntField(env,currSprt->object, currSprt->textureName);
			if(currTexture == 0){
				__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "EEEK! INVALID TEXTUREID BAD ! BAD %d", currTexture);
			}
			
			if(currTexture != prevTexture){ 
			    glBindTexture(GL_TEXTURE_2D, currTexture);
				prevTexture = currTexture;
			}
		
			glPushMatrix();
			glLoadIdentity();
			glColor4f(1, 1, 1, (*env)->GetFloatField(env, currSprt->object, currSprt->opacity));
			glTranslatef((*env)->GetFloatField(env, currSprt->object, currSprt->x),
						(*env)->GetFloatField(env, currSprt->object, currSprt->y),
						(*env)->GetFloatField(env, currSprt->object, currSprt->z));
		
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
			glDrawElements(GL_TRIANGLES, currSprt->indexCount, GL_UNSIGNED_SHORT, 0);
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
	__android_log_print(ANDROID_LOG_DEBUG, "NATIVE_SURFACE_CREATED", "The surface has been created.");
	
}

void Java_com_crackedcarrot_NativeRender_nativeUpdateSprite(JNIEnv* env, jobject thiz, jint spriteNo){
	
}
