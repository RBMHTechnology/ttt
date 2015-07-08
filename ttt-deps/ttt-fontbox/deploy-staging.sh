#!/bin/bash

mvn gpg:sign-and-deploy-file \
  -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
  -DrepositoryId=ossrh \
  -DpomFile=pom.xml \
  -Dfile=ttt-fontbox-1.0.jar \
  -Dsources=ttt-fontbox-1.0-sources.jar \
  -Djavadoc=ttt-fontbox-1.0-javadoc.jar
