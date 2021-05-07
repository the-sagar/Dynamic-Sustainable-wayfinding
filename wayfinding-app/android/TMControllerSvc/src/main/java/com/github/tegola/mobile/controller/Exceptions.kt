package com.github.tegola.mobile.controller

class Exceptions {
    class UnsupportedCPUABIException(message: String?) : Exception(message)
    class TegolaBinaryNotExecutableException(message: String?) : Exception(message)
    class InvalidTegolaArgumentException(message: String?) : Exception(message)

    //used in jni tcs_native_aux_supp.c
    class NativeSignalException(s_signame_from_native: String?) : Exception(s_signame_from_native)
}
