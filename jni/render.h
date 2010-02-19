#include <jni.h>
#include <android/log.h>
#include <malloc.h>
#include <GLES/glplatform.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

#define BACKGROUND 0;
#define MONSTER 1;
#define TOWER 2;
#define SHOT 3;

typedef struct {
    jobject object;
    jfieldID width, height;
    jfieldID x, y, z;
    jfieldID textureName;
} GLSprite;
