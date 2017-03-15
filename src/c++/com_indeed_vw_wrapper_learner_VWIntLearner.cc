#include <vw.h>
#include "jni_base_learner.h"
#include "com_indeed_vw_wrapper_learner_VWIntLearner.h"

jint intPredictor(example* vec, JNIEnv *env) { return vec->pred.multiclass; }

JNIEXPORT jint JNICALL Java_com_indeed_vw_wrapper_learner_VWIntLearner_predict(JNIEnv *env, jobject obj, jstring example_string, jboolean learn, jlong vwPtr)
{ return base_predict<jint>(env, obj, example_string, learn, vwPtr, intPredictor);
}
