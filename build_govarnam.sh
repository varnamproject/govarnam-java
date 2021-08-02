cd ../govarnam

export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/21.4.7075529
export BUILD_DIR="../govarnam-java/build/"

go get

export GOARCH=386
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/darwin-x86_64/bin/i686-linux-android21-clang
go build -buildmode=c-shared -o $BUILD_DIR/x86/libgovarnam.so .
cp *.h $BUILD_DIR/x86/

echo "Build x86 success"

export GOARCH=amd64
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/darwin-x86_64/bin/x86_64-linux-android21-clang
go build -buildmode=c-shared -o $BUILD_DIR/x86_64/libgovarnam.so .
cp *.h $BUILD_DIR/x86_64/

echo "Build x86_64 success"

export GOARCH=arm
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/darwin-x86_64/bin/armv7a-linux-androideabi21-clang
go build -buildmode=c-shared -o $BUILD_DIR/armeabi-v7a/libgovarnam.so .
cp *.h $BUILD_DIR/armeabi-v7a/

echo "Build armeabi-v7a success"

export GOARCH=arm64
export GOOS=android
export CGO_ENABLED=1
export CC=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android21-clang
go build -buildmode=c-shared -o $BUILD_DIR/arm64-v8a/libgovarnam.so .
cp *.h $BUILD_DIR/arm64-v8a/

echo "Build arm64-v8a success"
