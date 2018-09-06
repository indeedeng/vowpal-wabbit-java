#include <vw.h>
#include <stdio.h>
#include <unistd.h>
#include "jni_base_learner.h"
#include "com_indeed_vw_wrapper_learner_VWLearners.h"

#define RETURN_TYPE "com/indeed/vw/wrapper/learner/VWLearners$VWReturnType"
#define RETURN_TYPE_INSTANCE "L" RETURN_TYPE ";"

JNIEXPORT jlong JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_initialize(JNIEnv *env, jclass cls, jobjectArray jargs)
{ jlong vwPtr = 0;
  try
  { jint argc = env->GetArrayLength(jargs);
    char **argv = (char**) calloc(argc, sizeof(char*));
    for (int i = 0; i < argc; i++)
    { jstring jarg = (jstring) (env->GetObjectArrayElement(jargs, i));
      argv[i] = strdup(env->GetStringUTFChars(jarg, nullptr));
    }
    vw* vwInstance = VW::initialize(argc, argv);
    vwPtr = (jlong)vwInstance;
    return vwPtr;
  }
  catch(...)
  { rethrow_cpp_exception_as_java_exception(env);
  }
}

JNIEXPORT void JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_performRemainingPasses(JNIEnv *env, jclass obj, jlong vwPtr)
{ try
  { vw* vwInstance = (vw*)vwPtr;
    if (vwInstance->numpasses > 1)
      { adjust_used_index(*vwInstance);
        vwInstance->do_reset_source = true;
        VW::start_parser(*vwInstance);
        LEARNER::generic_driver(*vwInstance);
        VW::end_parser(*vwInstance);
      }
  }
  catch(...)
  { rethrow_cpp_exception_as_java_exception(env);
  }
}


JNIEXPORT void JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_closeInstance(JNIEnv *env, jclass obj, jlong vwPtr)
{ try
  { vw* vwInstance = (vw*)vwPtr;
    VW::finish(*vwInstance);
  }
  catch(...)
  { rethrow_cpp_exception_as_java_exception(env);
  }
}

JNIEXPORT void JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_saveModel(JNIEnv *env, jclass obj, jlong vwPtr, jstring filename)
{ try
  {
    const char* utf_string = env->GetStringUTFChars(filename, NULL);
    std::string filenameCpp(utf_string);
    env->ReleaseStringUTFChars(filename, utf_string);
    env->DeleteLocalRef(filename);
    VW::save_predictor(*(vw*)vwPtr, filenameCpp);
  }
  catch(...)
  { rethrow_cpp_exception_as_java_exception(env);
  }
}

JNIEXPORT jobject JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_getReturnType(JNIEnv *env, jclass obj, jlong vwPtr)
{ jclass clVWReturnType = env->FindClass(RETURN_TYPE);
  jfieldID field;
  vw* vwInstance = (vw*)vwPtr;
  switch (vwInstance->l->pred_type)
  { case prediction_type::prediction_type_t::action_probs:
      field = env->GetStaticFieldID(clVWReturnType , "ActionProbs", RETURN_TYPE_INSTANCE);
      break;
    case prediction_type::prediction_type_t::action_scores:
      field = env->GetStaticFieldID(clVWReturnType , "ActionScores", RETURN_TYPE_INSTANCE);
      break;
    case prediction_type::prediction_type_t::multiclass:
      field = env->GetStaticFieldID(clVWReturnType , "Multiclass", RETURN_TYPE_INSTANCE);
      break;
    case prediction_type::prediction_type_t::multilabels:
      field = env->GetStaticFieldID(clVWReturnType , "Multilabels", RETURN_TYPE_INSTANCE);
      break;
    case prediction_type::prediction_type_t::prob:
      field = env->GetStaticFieldID(clVWReturnType , "Prob", RETURN_TYPE_INSTANCE);
      break;
    case prediction_type::prediction_type_t::scalar:
      field = env->GetStaticFieldID(clVWReturnType , "Scalar", RETURN_TYPE_INSTANCE);
      break;
    case prediction_type::prediction_type_t::scalars:
      field = env->GetStaticFieldID(clVWReturnType , "Scalars", RETURN_TYPE_INSTANCE);
      break;
    default:
      field = env->GetStaticFieldID(clVWReturnType , "Unknown", RETURN_TYPE_INSTANCE);
  }

  return env->GetStaticObjectField(clVWReturnType, field);
}
