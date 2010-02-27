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
	
	
	GLsizeiptr vertBufSize;
	GLfloat* vertBuffer;
	GLsizeiptr textCoordBufSize;
	GLfloat* textureCoordBuffer;
	GLsizeiptr indexBufSize;
	GLuint* indexBuffer;
	GLuint 	indexCount;
	
	GLuint bufferName[3];

} GLSprite;

enum bufferTag {
	INDEX_OBJECT = 0,
	VERT_OBJECT = 1,
	TEX_OBJECT	= 2
};

void initHwBuffers(GLSprite* sprite);
