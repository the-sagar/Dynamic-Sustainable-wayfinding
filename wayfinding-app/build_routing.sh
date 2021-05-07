#!/usr/bin/env bash
cd RoutingBackend
export ANDROID_NDK_HOME=/mnt/PData/Android/Sdk/ndk/21.4.7075529
export GOOS=android
export GOARCH=arm64

export abi=aarch64-linux-android29
export CC=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang
export CXX=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang++
export CGO_ENABLED=1
GO_BLD_CMD_ARG_VAL__PKGDIR=${GOPATH}/pkg/gomobile/pkg_android_${GOARCH}
GO_BLD_CMD_ARG_VAL__O=../android/app/src/main/libs/arm64-v8a/librouting.so

export GO_BLD_CMD_ARG_VAL__LDFLAGS="-w -linkmode=external '-extldflags=-pie'"

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
go_build_cmd="${go_build_cmd} rpc/grpcs/grpc.go"

echo $go_build_cmd
eval $go_build_cmd

export GOOS=android
export GOARCH=amd64

export abi=x86_64-linux-android29
export CC=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang
export CXX=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/${abi}-clang++
export CGO_ENABLED=1
GO_BLD_CMD_ARG_VAL__PKGDIR=${GOPATH}/pkg/gomobile/pkg_android_${GOARCH}
GO_BLD_CMD_ARG_VAL__O=../android/app/src/main/libs/x86_64/librouting.so

export GO_BLD_CMD_ARG_VAL__LDFLAGS="-w -linkmode=external '-extldflags=-pie'"

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
go_build_cmd="${go_build_cmd} rpc/grpcs/grpc.go"

echo $go_build_cmd
eval $go_build_cmd

cd ..
