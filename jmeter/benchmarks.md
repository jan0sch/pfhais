# Benchmarks

## Conditions

Service and testing software (Apache jmeter) were run on different 
workstations connected via 100 MBit/s network connection.

### Service workstation

CPU : Core i5-9600K, 6 Cores, 3,7 GHz
RAM : 32 GB
HDD : 2x Samsung SSD 860 PRO 512GB, SATA
OS  : FreeBSD 12 (HT disabled)
JDK : 11.0.4+11-2
DB  : PostgreSQL 11.3

### Client workstation

CPU : AMD Ryzen Threadripper 2950X
RAM : 32 GB
HDD : 2x Samsung SSD 970 PRO 512GB, M.2
OS  : FreeBSD 12 (HT disabled)
JDK : 11.0.4+11-2

Apache JMeter 5.1.1 was used to run the benchmark.

If not noted otherwise 10 threads were used with a 10 seconds ramp up time.

## Impure runs

### 1 - Create 100.000 products

#### Measurements

|     |        |        |        |
|-----|--------|--------|--------|
| AVG |   98   |   98   |   98   |
| MED |   95   |   95   |   95   |
| 90% |  129   |  129   |  129   |
| 95% |  143   |  143   |  142   |
| 99% |  172   |  172   |  171   |
| MIN |   52   |   54   |   53   |
| MAX | 2451   |  329   | 1084   |
| ERR |    0%  |    0%  |    0%  |
| R/S |  100.2 |  100.7 |  100.8 |
| MEM | 1135   | 1161   | 1177   |
|  LD |   16   |   16   |   16   |

R/S = requests per second
MEM = max. memory in MB
LD  = average system load

### 2 - Load 100.000 products

#### Measurements

|     |        |        |        |
|-----|--------|--------|--------|
| AVG |    7   |    7   |    7   |
| MED |    8   |    8   |    8   |
| 90% |   10   |   10   |   10   |
| 95% |   11   |   11   |   11   |
| 99% |   14   |   14   |   14   |
| MIN |    4   |    4   |    4   |
| MAX |  864   |  109   |   68   |
| ERR |    0%  |    0%  |    0%  |
| R/S | 1151.4 | 1165.4 | 1169.8 |
| MEM | 1329   | 1509   | 1509   |
|  LD |   13   |   13   |   13   |

R/S = requests per second
MEM = max. memory in MB
LD  = average system load

### 3 - Update 100.000 products

#### Measurements

|     |        |        |        |
|-----|--------|--------|--------|
| AVG |   78   |   78   |   78   |
| MED |   76   |   75   |   75   |
| 90% |  104   |  104   |  104   |
| 95% |  115   |  116   |  115   |
| 99% |  140   |  141   |  140   |
| MIN |   42   |   42   |   42   |
| MAX |  941   | 1074   |  380   |
| ERR |    0%  |    0%  |    0%  |
| R/S |  125.5 |  125.5 |  126.0 |
| MEM | 1107   | 1180   | 1240   |
| LD  |   16   |   16   |   16   |

R/S = requests per second
MEM = max. memory in MB
LD  = average system load

### 4 - Bulk load all 100.000 products

* 2 threads were used with a 10 seconds ramp up time.
* RAM had to be increased to 8 GB to avoid out of memory exceptions.

#### Measurements

|     |        |        |        |
|-----|--------|--------|--------|
| AVG | 19233  | 19008  | 18942  |
| MED | 19099  | 18961  | 18962  |
| 90% | 19611  | 19562  | 19399  |
| 95% | 19895  | 19918  | 19812  |
| 99% | 20401  | 19995  | 20684  |
| MIN | 18620  | 17591  | 17334  |
| MAX | 22994  | 20086  | 20864  |
| ERR |    0%  |    0%  |    0%  |
| R/S |    6.2 |    6.3 |    6.3 |
| MEM | 7854   | 7909   | 7904   |
| LD  |    5   |    5   |    5   |

R/S = requests per minute
MEM = max. memory in MB
LD  = average system load

## Pure runs

### 1 - Create 100.000 products

#### Measurements

|     |        |        |        |
|-----|--------|--------|--------|
| AVG |   12   |   12   |   12   |
| MED |   11   |   11   |   12   |
| 90% |   15   |   15   |   15   |
| 95% |   18   |   18   |   18   |
| 99% |   30   |   30   |   30   |
| MIN |    5   |    5   |    5   |
| MAX | 1799   |   94   |  132   |
| ERR |    0%  |    0%  |    0%  |
| R/S |  755.7 |  772.9 |  767.4 |
| MEM | 1235   | 1309   | 1381   |
|  LD |    9   |    9   |    9   |

R/S = requests per second
MEM = max. memory in MB
LD  = average system load

### 2 - Load 100.000 products

#### Response times (ms)

|     |        |        |        |
|-----|--------|--------|--------|
| AVG |    7   |    7   |    7   |
| MED |    7   |    7   |    7   |
| 90% |    9   |    9   |    9   |
| 95% |   10   |   10   |   10   |
| 99% |   21   |   18   |   18   |
| MIN |    2   |    2   |    2   |
| MAX |  159   |   92   |  102   |
| ERR |    0%  |    0%  |    0%  |
| R/S | 1248.0 | 1250.6 | 1247.3 |
| MEM | 1542   | 1538   | 1534   |
|  LD |    8   |    8   |    8   |

R/S = requests per second
MEM = max. memory in MB
LD  = average system load

### 3 - Update 100.000 products

#### Measurements

|     |        |        |        |
|-----|--------|--------|--------|
| AVG |   12   |   12   |   12   |
| MED |   11   |   11   |   11   |
| 90% |   16   |   16   |   16   |
| 95% |   19   |   21   |   21   |
| 99% |   34   |   34   |   34   |
| MIN |    5   |    5   |    5   |
| MAX | 1899   |  108   |  114   |
| ERR |    0%  |    0%  |    0%  |
| R/S |  771.8 |  763.7 |  760.3 |
| MEM | 1237   | 1274   | 1327   |
|  LD |    9   |    9   |    9   |

R/S = requests per second
MEM = max. memory in MB
LD  = average system load

Stacktrace 1:
```text
ERROR NIO1SocketServerGroup - Error handling client channel. Closing.
java.util.concurrent.RejectedExecutionException: This SelectorLoop is closed.
        at org.http4s.blaze.channel.nio1.SelectorLoop.enqueueTask(SelectorLoop.scala:118)
        at org.http4s.blaze.channel.nio1.SelectorLoop.initChannel(SelectorLoop.scala:139)
        at org.http4s.blaze.channel.nio1.NIO1SocketServerGroup.org$http4s$blaze$channel$nio1$NIO1SocketServerGroup$$handleClientChannel(NIO1SocketServerGroup.scala:290)
        at org.http4s.blaze.channel.nio1.NIO1SocketServerGroup$SocketAcceptor.acceptNewConnections(NIO1SocketServerGroup.scala:148)
        at org.http4s.blaze.channel.nio1.NIO1SocketServerGroup$SocketAcceptor.opsReady(NIO1SocketServerGroup.scala:119)
        at org.http4s.blaze.channel.nio1.SelectorLoop.processKeys(SelectorLoop.scala:200)
        at org.http4s.blaze.channel.nio1.SelectorLoop.org$http4s$blaze$channel$nio1$SelectorLoop$$runLoop(SelectorLoop.scala:171)
        at org.http4s.blaze.channel.nio1.SelectorLoop$$anon$1.run(SelectorLoop.scala:68)
        at java.base/java.lang.Thread.run(Thread.java:834)
```

Stacktrace 2:
```text
ERROR SelectorLoop - Unhandled exception in selector loop
java.io.IOException: Connection reset by peer
        at java.base/sun.nio.ch.FileDispatcherImpl.close0(Native Method)
        at java.base/sun.nio.ch.SocketDispatcher.close(SocketDispatcher.java:55)
        at java.base/sun.nio.ch.SocketChannelImpl.kill(SocketChannelImpl.java:907)
        at java.base/sun.nio.ch.SelectorImpl.processDeregisterQueue(SelectorImpl.java:267)
        at java.base/sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:116)
        at java.base/sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:124)
        at java.base/sun.nio.ch.SelectorImpl.select(SelectorImpl.java:141)
        at org.http4s.blaze.channel.nio1.SelectorLoop.org$http4s$blaze$channel$nio1$SelectorLoop$$runLoop(
SelectorLoop.scala:163)
        at org.http4s.blaze.channel.nio1.SelectorLoop$$anon$1.run(SelectorLoop.scala:68)
        at java.base/java.lang.Thread.run(Thread.java:834)
ERROR NIO1SocketServerGroup - Listening socket(/0.0.0.0:53248) closed forcibly.
java.nio.channels.ShutdownChannelGroupException: null
        at org.http4s.blaze.channel.nio1.SelectorLoop.killSelector(SelectorLoop.scala:225)
        at org.http4s.blaze.channel.nio1.SelectorLoop.org$http4s$blaze$channel$nio1$SelectorLoop$$runLoop(
SelectorLoop.scala:186)
        at org.http4s.blaze.channel.nio1.SelectorLoop$$anon$1.run(SelectorLoop.scala:68)
        at java.base/java.lang.Thread.run(Thread.java:834)
ERROR NIO1HeadStage - Abnormal NIO1HeadStage termination
java.nio.channels.ShutdownChannelGroupException: null
        at org.http4s.blaze.channel.nio1.SelectorLoop.killSelector(SelectorLoop.scala:225)
        at org.http4s.blaze.channel.nio1.SelectorLoop.org$http4s$blaze$channel$nio1$SelectorLoop$$runLoop(
SelectorLoop.scala:186)
        at org.http4s.blaze.channel.nio1.SelectorLoop$$anon$1.run(SelectorLoop.scala:68)
        at java.base/java.lang.Thread.run(Thread.java:834)
```

### 4 - Bulk load all 100.000 products

* 2 threads were used with a 10 seconds ramp up time.

#### Measurements

|     |        |        |        |
|-----|--------|--------|--------|
| AVG | 14672  | 14414  | 14402  |
| MED | 14640  | 14394  | 14371  |
| 90% | 14913  | 14582  | 14573  |
| 95% | 14950  | 14754  | 14620  |
| 99% | 14959  | 14896  | 15122  |
| MIN | 14221  | 13975  | 13828  |
| MAX | 17241  | 15427  | 15677  |
| ERR |    0%  |    0%  |    0%  |
| R/S |    8.2 |    8.3 |    8.3 |
| MEM | 1177   | 1187   | 1205   |
| LD  |    4   |    4   |    4   |

R/S = requests per minute
MEM = max. memory in MB
LD  = average system load

