#include "com_indeed_vw_wrapper_learner_VWActionScoresLearner.h"
#include <vw.h>
#include "jni_base_learner.h"

jobject action_scores_prediction(example* vec, JNIEnv *env)
{ jclass action_score_class = env->FindClass("com/indeed/vw/wrapper/learner/ActionScore");
  jmethodID action_score_constructor = env->GetMethodID(action_score_class, "<init>", "(IF)V");

  ACTION_SCORE::action_scores a_s = vec->pred.a_s;
  size_t num_values = a_s.size();
  jobjectArray j_action_scores = env->NewObjectArray(num_values, action_score_class, 0);

  jclass action_scores_class = env->FindClass("com/indeed/vw/wrapper/learner/ActionScores");
  for (uint32_t i=0; i<num_values; ++i)
  { ACTION_SCORE::action_score a = a_s[i];
    jobject j_action_score = env->NewObject(action_score_class, action_score_constructor, a.action, a.score);
    env->SetObjectArrayElement(j_action_scores, i, j_action_score);
  }
  jmethodID action_scores_constructor = env->GetMethodID(action_scores_class, "<init>", "([Lcom/indeed/vw/wrapper/learner/ActionScore;)V");
  return env->NewObject(action_scores_class, action_scores_constructor, j_action_scores);
}

JNIEXPORT jobject JNICALL Java_com_indeed_vw_wrapper_learner_VWActionScoresLearner_predict(JNIEnv *env, jobject obj, jstring example_string, jboolean learn, jlong vwPtr)
{ return base_predict<jobject>(env, example_string, learn, vwPtr, action_scores_prediction);
}

JNIEXPORT jobject JNICALL Java_com_indeed_vw_wrapper_learner_VWActionScoresLearner_predictMultiline(JNIEnv *env, jobject obj, jobjectArray example_strings, jboolean learn, jlong vwPtr)
{ return base_predict<jobject>(env, example_strings, learn, vwPtr, action_scores_prediction);
}
