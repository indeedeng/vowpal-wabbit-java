#include "../../../../vowpalwabbit/vw.h"
#include "jni_base_learner.h"
#include "com_indeed_vw_wrapper_learner_VWLearners.h"

#define RETURN_TYPE "com/indeed/vw/wrapper/learner/VWLearners$VWReturnType"
#define RETURN_TYPE_INSTANCE "L" RETURN_TYPE ";"

JNIEXPORT jlong JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_initialize(JNIEnv *env, jobject obj, jstring command)
{ jlong vwPtr = 0;
  try
  { vw* vwInstance = VW::initialize(env->GetStringUTFChars(command, NULL));
    vwPtr = (jlong)vwInstance;
  }
  catch(...)
  { rethrow_cpp_exception_as_java_exception(env);
  }
  return vwPtr;
}

JNIEXPORT void JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_closeInstance(JNIEnv *env, jobject obj, jlong vwPtr)
{ try
  { VW::sync_stats(*((vw*)vwPtr));
    VW::finish(*((vw*)vwPtr));
  }
  catch(...)
  { rethrow_cpp_exception_as_java_exception(env);
  }
}

JNIEXPORT jobject JNICALL Java_com_indeed_vw_wrapper_learner_VWLearners_getReturnType(JNIEnv *env, jobject obj, jlong vwPtr)
{ jclass clVWReturnType = env->FindClass(RETURN_TYPE);
  jfieldID field;
  vw* vwInstance = (vw*)vwPtr;
  if (vwInstance->p->lp.parse_label == simple_label.parse_label)
  { if (vwInstance->lda > 0)
      field = env->GetStaticFieldID(clVWReturnType , "VWFloatArrayType", RETURN_TYPE_INSTANCE);
    else
      field = env->GetStaticFieldID(clVWReturnType , "VWFloatType", RETURN_TYPE_INSTANCE);
  }
  else if (vwInstance->p->lp.parse_label == MULTILABEL::multilabel.parse_label)
    field = env->GetStaticFieldID(clVWReturnType , "VWIntArrayType", RETURN_TYPE_INSTANCE);
  else if (vwInstance->p->lp.parse_label == MULTICLASS::mc_label.parse_label ||
           vwInstance->p->lp.parse_label == CB::cb_label.parse_label ||
           vwInstance->p->lp.parse_label == CB_EVAL::cb_eval.parse_label ||
           vwInstance->p->lp.parse_label == COST_SENSITIVE::cs_label.parse_label)
    field = env->GetStaticFieldID(clVWReturnType , "VWIntType", RETURN_TYPE_INSTANCE);
  else
    field = env->GetStaticFieldID(clVWReturnType , "Unknown", RETURN_TYPE_INSTANCE);
  return env->GetStaticObjectField(clVWReturnType, field);
}

