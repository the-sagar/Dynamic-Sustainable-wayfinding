#!/bin/bash
export ANDROID_NDK_HOME=/mnt/PData/Android/Sdk/ndk/21.4.7075529
export GOOS=android
export GOARCH=arm64

export abi=aarch64-linux-android21
export CC=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang
export CXX=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang++
export CGO_ENABLED=1

export GO_BLD_CMD_ARG_VAL__TAGS="android libsqlite3 oAzblobCache noS3Cache noRedisCache noPostgisProvider"

BASE_TEGOLA_SUBDIR=github.com/go-spatial/tegola
TEGOLA_SRC_DIR=${GOPATH}/src/$BASE_TEGOLA_SUBDIR
cd $TEGOLA_SRC_DIR
TEGOLA_VER_STRING__SHORT_HASH="$(git rev-parse --short HEAD)"
TEGOLA_VER_STRING__BRANCH="$(git rev-parse --abbrev-ref HEAD)"
TEGOLA_ARCH_FRIENDLY=arm64-v8a
TEGOLA_VER_STRING=${TEGOLA_VER_STRING__SHORT_HASH}.${TEGOLA_VER_STRING__BRANCH}.${GOOS}.${TEGOLA_ARCH_FRIENDLY}
        
export GO_BLD_CMD_ARG_VAL__LDFLAGS="-w -X ${BASE_TEGOLA_SUBDIR}/cmd/tegola/cmd.Version=${TEGOLA_VER_STRING} -linkmode=external '-extldflags=-pie'"

TEGOLA_BUILD_OUTPUT_DIR=${GOPATH}/pkg/${BASE_TEGOLA_SUBDIR}/android
TEGOLA_BUILD_BIN=tegola__${TEGOLA_VER_STRING}.so
TEGOLA_BUILD_BIN_PATH=${TEGOLA_BUILD_OUTPUT_DIR}/${TEGOLA_BUILD_BIN}
GO_BLD_CMD_ARG_VAL__O=$TEGOLA_BUILD_BIN_PATH

go_build_cmd="go build -p=8"
if [[ -n "${GO_BLD_CMD_ARG_VAL__PKGDIR}" ]]; then
go_build_cmd="${go_build_cmd} -pkgdir=\"${GO_BLD_CMD_ARG_VAL__PKGDIR}\""
fi
if [[ -n "${GO_BLD_CMD_ARG_VAL__TAGS}" ]]; then
go_build_cmd="${go_build_cmd} -tags \"${GO_BLD_CMD_ARG_VAL__TAGS}\""
fi
if [[ -n "${GO_BLD_CMD_ARG_VAL__LDFLAGS}" ]]; then
go_build_cmd="${go_build_cmd} -ldflags \"${GO_BLD_CMD_ARG_VAL__LDFLAGS}\""
fi
if [[ -n "${GO_BLD_CMD_ARG_VAL__O}" ]]; then
go_build_cmd="${go_build_cmd} -o ${GO_BLD_CMD_ARG_VAL__O}"
fi
go_build_cmd="${go_build_cmd} -x -v ."

# create TEGOLA_BUILD_OUTPUT_DIR
mkdir -p $TEGOLA_BUILD_OUTPUT_DIR > /dev/null 2>&1
BUILD_WRK_DIR=${TEGOLA_SRC_DIR}/cmd/tegola
cd $BUILD_WRK_DIR

echo $go_build_cmd 
eval $go_build_cmd


export GOOS=android
export GOARCH=amd64
export abi=x86_64-linux-android21
export CC=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang
export CXX=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang++
TEGOLA_ARCH_FRIENDLY=x86_64
TEGOLA_VER_STRING=${TEGOLA_VER_STRING__SHORT_HASH}.${TEGOLA_VER_STRING__BRANCH}.${GOOS}.${TEGOLA_ARCH_FRIENDLY}

TEGOLA_VER_STRING=${TEGOLA_VER_STRING__SHORT_HASH}.${TEGOLA_VER_STRING__BRANCH}.${GOOS}.${TEGOLA_ARCH_FRIENDLY}
        
export GO_BLD_CMD_ARG_VAL__LDFLAGS="-w -X ${BASE_TEGOLA_SUBDIR}/cmd/tegola/cmd.Version=${TEGOLA_VER_STRING} -linkmode=external '-extldflags=-pie'"

TEGOLA_BUILD_OUTPUT_DIR=${GOPATH}/pkg/${BASE_TEGOLA_SUBDIR}/android
TEGOLA_BUILD_BIN=tegola__${TEGOLA_VER_STRING}.so
TEGOLA_BUILD_BIN_PATH=${TEGOLA_BUILD_OUTPUT_DIR}/${TEGOLA_BUILD_BIN}
GO_BLD_CMD_ARG_VAL__O=$TEGOLA_BUILD_BIN_PATH

go_build_cmd="go build -p=8"
if [[ -n "${GO_BLD_CMD_ARG_VAL__PKGDIR}" ]]; then
go_build_cmd="${go_build_cmd} -pkgdir=\"${GO_BLD_CMD_ARG_VAL__PKGDIR}\""
fi
if [[ -n "${GO_BLD_CMD_ARG_VAL__TAGS}" ]]; then
go_build_cmd="${go_build_cmd} -tags \"${GO_BLD_CMD_ARG_VAL__TAGS}\""
fi
if [[ -n "${GO_BLD_CMD_ARG_VAL__LDFLAGS}" ]]; then
go_build_cmd="${go_build_cmd} -ldflags \"${GO_BLD_CMD_ARG_VAL__LDFLAGS}\""
fi
if [[ -n "${GO_BLD_CMD_ARG_VAL__O}" ]]; then
go_build_cmd="${go_build_cmd} -o ${GO_BLD_CMD_ARG_VAL__O}"
fi
go_build_cmd="${go_build_cmd} -x -v ."

# create TEGOLA_BUILD_OUTPUT_DIR
mkdir -p $TEGOLA_BUILD_OUTPUT_DIR > /dev/null 2>&1
BUILD_WRK_DIR=${TEGOLA_SRC_DIR}/cmd/tegola
cd $BUILD_WRK_DIR

eval $go_build_cmd
