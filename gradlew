#!/bin/sh
#
# Gradle start up script for POSIX
#

APP_BASE_NAME=`basename "$0"`
APP_HOME_CANDIDATE="`dirname "$0"`"
APP_HOME="`cd "$APP_HOME_CANDIDATE" > /dev/null && pwd`"

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

warn () { echo "$*"; } >&2
die ()  { echo; echo "$*"; echo; exit 1; } >&2

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD="java"
    java -version >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' found in PATH."
fi

case "$( uname )" in
  Darwin* )
    MAX_FD_LIMIT=`ulimit -H -n`
    [ $? -eq 0 ] && ulimit -n "$MAX_FD_LIMIT"
    ;;
  * )
    MAX_FD_LIMIT=`ulimit -H -n`
    [ $? -eq 0 ] && ulimit -n "$MAX_FD_LIMIT"
    ;;
esac

exec "$JAVACMD" \
    -Xmx64m -Xms64m \
    $JAVA_OPTS \
    $GRADLE_OPTS \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
