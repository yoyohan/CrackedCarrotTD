#include <jni.h>
#include <android/log.h>
#include <malloc.h>
#include <GLES/glplatform.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

enum spriteType {
	BACKGROUND,
	SHOT,
	ANIMATION,
	CREATURE,
	GRID,
	TOWER,
};

typedef struct {
	
	enum spriteType type;
	
    jobject object;
    jfieldID width, height, scale;
    jfieldID x, y, z;
	jfieldID draw;
    jfieldID textureName;
	jfieldID r, g, b, opacity;
	jfieldID nFrames;
	jfieldID cFrame;
	
	GLushort 	indexCount;
	GLuint bufferName[2];
	GLuint* textureBufferNames;

} GLSprite;

//The number of idividual sprites.
int noOfSprites[6];
//Array with pointers to GLSprites.
GLSprite* renderSprites[6];
//GLuint* textureNameWorkspace;
//GLuint* cropWorkspace;

enum bufferTag {
	INDEX_OBJECT = 0,
	VERT_OBJECT = 1,
	TEX_OBJECT	= 2
};

void initHwBuffers(JNIEnv* env, GLSprite* sprite);
void Java_com_crackedcarrot_NativeRender_nativeResize(JNIEnv*  env, jobject  thiz, jint w, jint h);
void Java_com_crackedcarrot_NativeRender_nativeDrawFrame(JNIEnv*  env);
void Java_com_crackedcarrot_NativeRender_nativeSurfaceCreated(JNIEnv*  env);

//void Java_com_crackedcarrot_NativeRender_nativeDataPoolSize(JNIEnv* env, jobject thiz, jint size);
//void Java_com_crackedcarrot_NativeRender_nativeAlloc(JNIEnv*  env, jobject thiz, jint spriteNO, jobject sprite);
//void Java_com_crackedcarrot_NativeRender_nativeFreeSprites(JNIEnv* env);
//void Java_com_crackedcarrot_NativeRender_nativeFreeTex(JNIEnv* env, jobject thiz, jint textureName);
//void Java_com_crackedcarrot_NativeRender_nativeUpdateSprite(JNIEnv* env, jobject thiz, jint spriteNo);