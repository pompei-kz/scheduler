.PHONY: tst-app

publishToMavenLocal:
	./gradlew publishToMavenLocal

tst-app:
	./gradlew :tst-app:tst-app

goto-maven:
	./gradlew clean :publication:goto-maven
