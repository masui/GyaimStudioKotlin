.PHONY: build
build:
	./gradlew
install:
	adb install -r app/build/outputs/apk/app-debug.apk
clean:
	/bin/rm -r -f bin
