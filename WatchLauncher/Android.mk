LOCAL_PATH :=$(call my-dir)
include $(CLEAR_VARS)
#添加三方jar包以及support支持包
LOCAL_STATIC_JAVA_LIBRARIES := android-common \
    android-support-v4 \
    android-support-percent \
    android-support-v14-preference \
    android-support-v7-appcompat \
    android-support-v7-preference \
    android-support-v7-recyclerview \
    pickpicture \
    glide3 \
    gson
LOCAL_JAVA_LIBRARIES := telephony-common
#配置res路径
LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res \
    frameworks/support/percent/res \
    frameworks/support/v14/preference/res \
    frameworks/support/v7/appcompat/res \
    frameworks/support/v7/preference/res \
    frameworks/support/v7/recyclerview/res

#防止res资源冲突
LOCAL_AAPT_FLAGS := --auto-add-overlay \
    --extra-packages android.support.percent \
    --extra-packages android.support.v14.preference \
    --extra-packages android.support.v7.appcompat \
    --extra-packages android.support.v7.preference \
    --extra-packages android.support.v7.recyclerview \

LOCAL_ASSET_DIR := $(LOCAL_PATH)/assets
    
#添加源码路径
LOCAL_SRC_FILES :=$(call all-java-files-under, java)
#apk编译环境配置
LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := WatchLauncher
LOCAL_CERTIFICATE :=platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_OVERRIDES_PACKAGES := Home Launcher2 Launcher3 Mms

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
#预编译三方jar包
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES :=pickpicture:libs/pickpicture-1.0.0.jar \
    glide3:libs/glide-3.6.0.jar \
    gson:libs/gson-2.8.0.jar
include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under, $(LOCAL_PATH))
