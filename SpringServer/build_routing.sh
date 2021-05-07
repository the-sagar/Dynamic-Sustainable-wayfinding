#!/usr/bin/env bash
cd RoutingBackend
GOOS=darwin go build -o ../src/main/resources/native/macos/librouting.so github.com/xiaokangwang/osmRoute/rpc/grpcs 
GOOS=linux GOARCH=amd64 go build -o ../src/main/resources/native/linux_amd64/librouting.so github.com/xiaokangwang/osmRoute/rpc/grpcs
GOOS=windows GOARCH=amd64 go build -o ../src/main/resources/native/windows_amd64/librouting.dll github.com/xiaokangwang/osmRoute/rpc/grpcs
cd ..
