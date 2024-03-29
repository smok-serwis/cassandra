DESC="Cassandra"
NAME=cassandra
PIDFILE=/var/run/$NAME/$NAME.pid
SCRIPTNAME=/etc/init.d/$NAME
CONFDIR=/etc/cassandra
WAIT_FOR_START=10
CASSANDRA_HOME=/usr/share/cassandra
CASSANDRA_CONF=$CONFDIR
FD_LIMIT=100000


# Determine the sort of JVM we'll be running on.
java_ver_output=`"${JAVA:-java}" -version 2>&1`
jvmver=`echo "$java_ver_output" | grep '[openjdk|java] version' | awk -F'"' 'NR==1 {print $2}'`
JVM_VERSION=${jvmver%_*}
JVM_PATCH_VERSION=${jvmver#*_}
jvm=`echo "$java_ver_output" | grep -A 1 'java version' | awk 'NR==2 {print $1}'`
JVM_VENDOR=OpenJDK
# this will be "64-Bit" or "32-Bit"
JVM_ARCH=`echo "$java_ver_output" | awk 'NR==3 {print $3}'`


if [ "x$MALLOC_ARENA_MAX" = "x" ] ; then
    export MALLOC_ARENA_MAX=4
fi

# Here we create the arguments that will get passed to the jvm when
# starting cassandra.

# Read user-defined JVM options from jvm.options file
JVM_OPTS_FILE=$CASSANDRA_CONF/jvm.options
for opt in `grep "^-" $JVM_OPTS_FILE`
do
  JVM_OPTS="$JVM_OPTS $opt"
done

# Make sure all memory is faulted and zeroed on startup.
# This helps prevent soft faults in containers and makes
# transparent hugepage allocation more effective.
JVM_OPTS="$JVM_OPTS -XX:+AlwaysPreTouch"

# Biased locking does not benefit Cassandra.
JVM_OPTS="$JVM_OPTS -XX:-UseBiasedLocking"

# Larger interned string table, for gossip's benefit (CASSANDRA-6410)
JVM_OPTS="$JVM_OPTS -XX:StringTableSize=1000003"

# Enable thread-local allocation blocks and allow the JVM to automatically
# resize them at runtime.
JVM_OPTS="$JVM_OPTS -XX:+UseTLAB -XX:+ResizeTLAB"

# http://www.evanjones.ca/jvm-mmap-pause.html
JVM_OPTS="$JVM_OPTS -XX:+PerfDisableSharedMem"

# provides hints to the JIT compiler
JVM_OPTS="$JVM_OPTS -XX:CompileCommandFile=$CASSANDRA_CONF/hotspot_compiler"

# add the jamm javaagent
JVM_OPTS="$JVM_OPTS -javaagent:$CASSANDRA_HOME/lib/jamm-0.3.2.jar"

# add Prometheus JMX agent
JVM_OPTS="$JVM_OPTS -javaagent:$CASSANDRA_HOME/lib/jmx_prometheus_javaagent-0.12.0.jar=0.0.0.0:7198:/etc/cassandra/jmx-exporter.yaml"

if [ -z "$DISABLE_JOLOKIA" ]; then
  # add Jolokia agent
  JVM_OPTS="$JVM_OPTS -javaagent:/usr/share/cassandra/lib/jolokia-jvm-1.6.2-agent.jar=port=8080,host=0.0.0.0"
fi

# enable thread priorities, primarily so we can give periodic tasks
# a lower priority to avoid interfering with client workload
JVM_OPTS="$JVM_OPTS -XX:+UseThreadPriorities"
# allows lowering thread priority without being root.  see
# http://tech.stolsvik.com/2010/01/linux-java-thread-priorities-workaround.html
JVM_OPTS="$JVM_OPTS -XX:+ExitOnOutOfMemoryError"
# set jvm HeapDumpPath with CASSANDRA_HEAPDUMP_DIR
JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError"
if [ "x$CASSANDRA_HEAPDUMP_DIR" != "x" ]; then
    JVM_OPTS="$JVM_OPTS -XX:HeapDumpPath=$CASSANDRA_HEAPDUMP_DIR/cassandra-`date +%s`-pid$$.hprof"
fi

# uncomment to have Cassandra JVM listen for remote debuggers/profilers on port 1414
# JVM_OPTS="$JVM_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1414"

# uncomment to have Cassandra JVM log internal method compilation (developers only)
# JVM_OPTS="$JVM_OPTS -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation"
# JVM_OPTS="$JVM_OPTS -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"

# Prefer binding to IPv4 network intefaces (when net.ipv6.bindv6only=1). See
# http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6342561 (short version:
# comment out this entry to enable IPv6 support).
JVM_OPTS="$JVM_OPTS -Djava.net.preferIPv4Stack=true"

# jmx: metrics and administration interface
#
# add this if you're having trouble connecting:
# JVM_OPTS="$JVM_OPTS -Djava.rmi.server.hostname=<public name>"
#
# see
# https://blogs.oracle.com/jmxetc/entry/troubleshooting_connection_problems_in_jconsole
# for more on configuring JMX through firewalls, etc. (Short version:
# get it working with no firewall first.)
#
# Cassandra ships with JMX accessible *only* from localhost.
# To enable remote JMX connections, uncomment lines below
# with authentication and/or ssl enabled. See https://wiki.apache.org/cassandra/JmxSecurity
#
if [ "x$LOCAL_JMX" = "x" ]; then
    LOCAL_JMX=yes
fi

if [ ! -z "$JAEGER_AGENT_HOST" ]; then
  JVM_OPTS="$JVM_OPTS -Dcassandra.custom_tracing_class=io.infracloud.cassandra.tracing.JaegerTracing"
  CLASSPATH="$CLASSPATH:/usr/share/cassandra/lib/cassandra-jaeger-tracing-4.1.0.jar"
fi

# Specifies the default port over which Cassandra will be available for
# JMX connections.
# For security reasons, you should not expose this port to the internet.  Firewall it if needed.
JMX_PORT="7199"

if [ "$LOCAL_JMX" = "yes" ]; then
  JVM_OPTS="$JVM_OPTS -Dcassandra.jmx.local.port=$JMX_PORT"
else
  JVM_OPTS="$JVM_OPTS -Dcassandra.jmx.remote.port=$JMX_PORT"
  JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT"
  JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.ssl=false"
  if [ "$JMX_AUTH" = "yes" ]; then
    JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.authenticate=true"
    JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.password.file=/etc/cassandra/jmxremote.password"
    JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.access.file=/etc/cassandra/jmxremote.access"
  fi
fi

CASSANDRA_HOME=/usr/share/cassandra
chmod ugo+rx /usr/share/cassandra/*.jar /usr/share/cassandra/lib/*.jar
# The java classpath (required)
if [ -n "$CLASSPATH" ]; then
    CLASSPATH=$CLASSPATH:$CASSANDRA_CONF
else
    CLASSPATH=$CASSANDRA_CONF
fi

JVM_OPTS="$JVM_OPTS "
# Cassandra uses SIGAR to capture OS metrics CASSANDRA-7838
# for SIGAR we have to set the java.library.path
# to the location of the native libraries.
JVM_OPTS="$JVM_OPTS -Djava.library.path=$CASSANDRA_HOME/lib/sigar-bin"

JVM_OPTS="$JVM_OPTS $JVM_EXTRA_OPTS"
$$$EXTRA_ARGS
