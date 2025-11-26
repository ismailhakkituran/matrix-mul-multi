matrix-mul-multi
=================

Bu proje, **tek thread** kullanarak matris çarpımı yapan ve belirli bir süre içinde kaç adet çarpım gerçekleştirebildiğini ölçen basit bir **Java / Maven** konsol uygulamasıdır.

Amaç, matris çarpma işlemini:
- Temiz bir sınıf hiyerarşisi (POJO + handler) ile göstermek,
- Zaman bazlı (örneğin 1 saniye) bir benchmark ile performansı ölçmektir.

---

## Teknolojiler

- Java 17
- Maven
- `exec-maven-plugin` (main sınıfını çalıştırmak için)

---

## Sınıf Hiyerarşisi

Namespace (package): `com.example.single`

```text
com.example.single
 ├─ Matrix
 ├─ SingleThreadMatrixMultiplicationHandler
 └─ SingleThreadBenchmarkApp  (main sınıfı)
````

### `Matrix`

* Basit bir **POJO** sınıfıdır.
* Özellikler:

  * `rows` : satır sayısı
  * `cols` : sütun sayısı
  * `double[][] data` : matris verisi
* Metotlar:

  * `getRows(), getCols(), getData()`
  * `get(int r, int c), set(int r, int c, double value)`
  * `static Matrix randomMatrix(int rows, int cols, double min, double max, Random random)`

    * Verilen aralıkta rastgele elemanlarla matris oluşturur.

### `SingleThreadMatrixMultiplicationHandler`

* Tek thread kullanarak **C = A x B** matris çarpımını yapar.
* Metot:

  * `Matrix multiply(Matrix a, Matrix b)`

    * `a.getCols() == b.getRows()` şartını kontrol eder.
    * Klasik üçlü döngü (O(n³)) ile sonucu hesaplar.
    * Sonuç olarak yeni bir `Matrix` döner.

### `SingleThreadBenchmarkApp` (main)

* Benchmark işlemini yürüten ana sınıftır.
* Çalışma prensibi:

  1. Verilen boyutta (NxN) iki rasgele matris üretir (`Matrix.randomMatrix`).
  2. Küçük bir **warm-up** (örneğin 5 kere çarpma) yapar.
  3. Verilen süre (örn. 1 saniye) boyunca sürekli `handler.multiply(a, b)` çağırır.
  4. Süre sonunda:

     * Toplam kaç çarpım yapıldığını,
     * Saniye başına çarpım sayısını ekrana yazar.

---

## Çalıştırma

Proje kök dizinindeyken:

```bash
mvn -q -DskipTests package
```

Ardından:

```bash
mvn -q exec:java -Dexec.args="N SURE"
```

* `N` : matris boyutu (NxN), varsayılan: `500`
* `SURE` : saniye cinsinden benchmark süresi, varsayılan: `1`

Örnekler:

```bash
# 500x500 matris, 1 saniye:
mvn -q exec:java -Dexec.args="500 1"

# 300x300 matris, 3 saniye:
mvn -q exec:java -Dexec.args="300 3"
```

---

## Örnek Çıktı

```text
=== Single-thread Benchmark ===
Matrix: 500x500
Mult/s: 42
```

(Not: Değerler işlemci gücüne ve matris boyutuna göre değişir.)

---

## Notlar

* Bu proje, aynı matrisler üzerinde tekrar tekrar çarpım yaptığı için **hafıza** kullanımını basit tutar.
* Thread kullanımını kıyaslamak için, bu projenin çoklu thread kullanan versiyonu olan
  [`matrix-mul-multi`](../matrix-mul-multi) ile birlikte çalıştırılabilir.

````

---

Amaç:
- Tek thread’li yaklaşıma kıyasla (bkz. `matrix-mul-single`),
- Satır bazında bölünmüş iş yükünü birden fazla thread’e dağıtarak,
- Aynı süre içinde daha fazla matris çarpımı yapıp yapamadığını gözlemlemektir.

---

## Teknolojiler

- Java 17
- Maven
- `exec-maven-plugin`

---

## Sınıf Hiyerarşisi

Namespace (package): `com.example.multi`

```text
com.example.multi
 ├─ Matrix
 ├─ MatrixMultiplicationTask
 ├─ MultiThreadMatrixMultiplicationHandler
 └─ MultiThreadBenchmarkApp  (main sınıfı)
````

### `Matrix`

* Tek thread’li projedeki `Matrix` ile aynı yapıdadır.
* POJO özellikler:

  * `rows`, `cols`, `double[][] data`
* Metotlar:

  * `getRows(), getCols(), getData()`
  * `get(int r, int c), set(int r, int c, double value)`
  * `static Matrix randomMatrix(...)` ile rasgele matris üretimi.

### `MatrixMultiplicationTask`

* `Runnable` arayüzünü implemente eden, **çarpımın belirli bir satır aralığını** hesaplayan görev sınıfı.
* Özellikler:

  * `Matrix a, b, result`
  * `int start, end` → başlangıç ve bitiş satır indeksleri (end hariç).
* `run()` metodu:

  * `i = start` … `end-1` satırları için klasik çarpımı uygular.
  * Sonuçları `result` matrisine yazar.

### `MultiThreadMatrixMultiplicationHandler`

* Çok thread’li çarpım işini yöneten handler sınıfı.
* Özellik:

  * `threadCount` : kullanılacak thread sayısı.
* Metot:

  * `Matrix multiply(Matrix a, Matrix b)`:

    * Satırları `threadCount` adet parçaya böler.
    * Her parça için bir `MatrixMultiplicationTask` ve bir `Thread` oluşturur.
    * Bütün thread’leri `start()` ile başlatır.
    * Ardından join ederek hepsinin bitmesini bekler.
    * Sonuç olarak `result` matrisini döner.

### `MultiThreadBenchmarkApp` (main)

* Çok thread’li benchmark’ı koşturan ana sınıf.
* Adımlar:

  1. İstenen boyutta (NxN) `a` ve `b` matrislerini rasgele üretir.
  2. `MultiThreadMatrixMultiplicationHandler` örneğini, istenen thread sayısıyla oluşturur.
  3. Küçük bir warm-up (örneğin 5 tur çarpım) yapar.
  4. Verilen süre boyunca `handler.multiply(a, b)` çağırmaya devam eder.
  5. Süre bitince:

     * Toplam çarpım sayısını,
     * Saniye başına çarpım sayısını ekrana yazar.

---

## Çalıştırma

Proje kök dizinindeyken:

```bash
mvn -q -DskipTests package
```

Ardından:

```bash
mvn -q exec:java -Dexec.args="N SURE THREAD_SAYISI"
```

* `N` : matris boyutu (NxN), varsayılan: `500`
* `SURE` : saniye cinsinden benchmark süresi, varsayılan: `1`
* `THREAD_SAYISI` : kullanılacak thread sayısı, varsayılan: `Runtime.getRuntime().availableProcessors()`

Örnekler:

```bash
# 500x500 matris, 1 saniye, CPU çekirdek sayısı kadar thread:
mvn -q exec:java -Dexec.args="500 1"

# 400x400 matris, 2 saniye, 8 thread:
mvn -q exec:java -Dexec.args=\"400 2 8\"

# 300x300 matris, 5 saniye, 4 thread:
mvn -q exec:java -Dexec.args="300 5 4"
```


---

## Örnek Çıktı

```text
=== Multi-thread Benchmark ===
Matrix: 500x500
Threads: 8
Mult/s: 120
```

Gerçek değerler:

* CPU çekirdek sayısı,
* İşlemcinin hızına,
* Matris boyutuna,
* JVM’in JIT optimizasyonlarına göre değişecektir.

---

## Tek Thread İle Karşılaştırma

Bu projeyi, `matrix-mul-single` ile karşılaştırarak:

* Aynı parametrelerle (örneğin `N=500`, `SURE=1`) çalıştırıp,
* **Single-thread** ve **multi-thread** için saniyedeki çarpım sayılarını kıyaslayabilirsin.

Teorik olarak:

* Çok çekirdekli bir işlemcide,
* Thread sayısı çekirdek sayısına yakın bir değerde seçildiğinde
* Multi-thread sürümünün daha yüksek “multiplications per second” değeri vermesi beklenir.

---

## Notlar

* Çok fazla thread seçmek her zaman daha hızlı anlamına gelmez (context switch maliyetleri).
* Matris boyutu çok küçükse, thread açma-kapama overhead’i sebebiyle tek thread’li uygulama bazen daha performanslı bile olabilir.





