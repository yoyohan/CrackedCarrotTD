#include <jni.h>
#include <android/log.h>
#include <malloc.h>
#include <GLES/glplatform.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

enum spriteType {
	BACKGROUND,
	SHOT,
	MONSTER,
	TOWER
};

typedef struct {
	
	enum spriteType type;
	
    jobject object;
    jfieldID width, height;
    jfieldID x, y, z;
	jfieldID draw;
    jfieldID textureName;
	jfieldID opacity;
	
	
	GLsizeiptr vertBufSize;
	GLfloat* vertBuffer;
	GLsizeiptr textCoordBufSize;
	GLfloat* textureCoordBuffer;
	GLsizeiptr indexBufSize;
	GLushort* indexBuffer;
	GLushort 	indexCount;
	
	GLuint bufferName[3];

} GLSprite;

//The number of idividual sprites.
int noOfSprites;
//Array with pointers to GLSprites.
GLSprite* renderSprites;
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

void Java_com_crackedcarrot_NativeRender_nativeDataPoolSize(JNIEnv* env, jobject thiz, jint size);
void Java_com_crackedcarrot_NativeRender_nativeAlloc(JNIEnv*  env, jobject thiz, jint spriteNO, jobject sprite);
void Java_com_crackedcarrot_NativeRender_nativeFreeSprites(JNIEnv* env);
void Java_com_crackedcarrot_NativeRender_nativeFreeTex(JNIEnv* env, jobject thiz, jint textureName);
