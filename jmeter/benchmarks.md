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
| AVG |   76   |   75   |   75   |
| MED |   73   |   72   |   72   |
| 90% |  100   |   99   |  100   |
| 95% |  112   |  110   |  111   |
| 99% |  139   |  136   |  137   |
| MIN |   40   |   40   |   41   |
| MAX | 1644   |  502   | 1054   |
| ERR |    0%  |    0%  |    0%  |
| R/S |  129.5 |  130.9 |  130.8 |
| MEM | 1161   | 1226   | 1271   |
|  LD |   16   |   16   |   16   |

R/S = requests per second
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
| AVG |        |        |        |
| MED |        |        |        |
| 90% |        |        |        |
| 95% |        |        |        |
| 99% |        |        |        |
| MIN |        |        |        |
| MAX |        |        |        |
| ERR |    0%  |    0%  |    0%  |
| R/S |        |        |        |
| MEM |        |        |        |
|  LD |        |        |        |

R/S = requests per second
MEM = max. memory in MB
LD  = average system load

