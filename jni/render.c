#include <jni.h>
#include <android/log.h>
#include <malloc.h>
#include <GLES/glplatform.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

typedef struct {
    jobject object;
    jfieldID width; jfieldID height;
    jfieldID x, y, z;
    jfieldID velocityX, velocityY, velocityZ;
    jfieldID textureName;
    jfieldID resourceId;
} GLSprite;

int noOfSprites = 0;
GLSprite* sprites;


void Java_com_crackedcarrot_NativeRender_nativeDataPoolSize(JNIEnv* env,
                                                                  jobject thiz, 
                                                                  jint size){
                                                                  
    noOfSprites = size;
    sprites = malloc(sizeof(GLSprite)*noOfSprites);
    __android_log_print(ANDROID_LOG_DEBUG, "NATIVE ALLOC","Allocating memory pool\n");
}

void Java_com_crackedcarrot_NativeRender_nativeAlloc(JNIEnv*  env, jobject thiz, jint spriteNO, jobject sprite){
     __android_log_print(ANDROID_LOG_DEBUG, "NATIVE ALLOC","Starting to Load Textures\n");
     
     //sprites[spriteNO].object = sprite;
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
     
     //cache the velocity IDs
     
     id = (*env)->GetFieldID(env, class, "velocityX", "F");
     sprites[spriteNO].velocityX = id;
     id = (*env)->GetFieldID(env, class, "velocityY", "F");
     sprites[spriteNO].velocityY = id;
     id = (*env)->GetFieldID(env, class, "velocityZ", "F");
     sprites[spriteNO].velocityZ = id;
     
     //cache the width/height IDs
     id = (*env)->GetFieldID(env, class, "width", "F");
     sprites[spriteNO].width = id;
     id = (*env)->GetFieldID(env, class, "height", "F");
     sprites[spriteNO].height = id;
     
     //cache Texture and Resource IDs
     id = (*env)->GetFieldID(env, class, "mTextureName", "I");
     sprites[spriteNO].textureName = id;
     id = (*env)->GetFieldID(env, class, "mResourceId", "I");
     sprites[spriteNO].resourceId = id;
     
     
     __android_log_print(ANDROID_LOG_DEBUG, "NATIVE ALLOC", 
                                              "Texture X:%f Texture Y:%f Texture Z:%f\n",
                                              sprites[spriteNO].x,
                                              sprites[spriteNO].y,
                                              sprites[spriteNO].z);
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

}

void Java_com_crackedcarrot_NativeRender_nativeDrawFrame(JNIEnv*  env){
    int x;
    if (sprites != NULL) {
            glMatrixMode(GL_MODELVIEW);
            for (x = 0; x < noOfSprites; x++) {
                glBindTexture(GL_TEXTURE_2D,(*env)->GetIntField(env,sprites[x].object, sprites[x].textureName));
                
                glDrawTexfOES((*env)->GetFloatField(env,sprites[x].object, sprites[x].x)
                                                    , (*env)->GetFloatField(env,sprites[x].object, sprites[x].y)
                                                    , (*env)->GetFloatField(env,sprites[x].object, sprites[x].z)
                                                    , (*env)->GetFloatField(env,sprites[x].object, sprites[x].width)
                                                    , (*env)->GetFloatField(env,sprites[x].object, sprites[x].height));
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

}
