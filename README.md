
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
### Desarrollado por: Daniel Santiago Gómez Zabala
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch


### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.
2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].

	  * Creacion de los hilos en la clase CountThreadsmain
	  ![](img/CreacionHilos.png)

	2. Inicie los tres hilos con 'start()'.
	3. Ejecute y revise la salida por pantalla. 
	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.
      
	  * __Ejecución con Start__
	  ![](img/EjecucionStart.png)

	  * __Ejecución con Run__
	  ![](img/EjecucionRun.png)

	  * __Explicación__<br>
	  Cuando se ejecuta con __Run()__ tenemos que los número estan en orden ya que este codigo existe en nuestro paquete y fue escrito por nosotros y se ejecutará en el orden en el que lo escribimos, mientras que al ejecutar con __Start()__ es un método que se encuentra en una libreria y este no tiene en cuenta el orden y acá se introduce un concepto llamado: time sliding. Este nos explica porqué la impresión con dicho método, ya que los hilos al encontrar un x tiempo en el procesador procede a realizar su tarea sin importar el orden de ejecución.


**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.

		* __IP: 202.24.34.55__<br>
		![](img/IPnoconfiable.png)

		* __IP: 202.24.24.55__<br>
		![](img/IPnoaparece.png)


**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

**Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

1. Un solo hilo.

    * Resultado al ejecutar el programa con un solo hilo<br>
	![](img/Con1hilo.png) 
	* Vistazo a VisualVM mientras se ejecutaba el programa<br>
	![](img/visualVMhilo1.png)

2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).

    * Resultado al ejecutar el programa con los n núcleos que tiene el procesador del equipo en el que se esta realizando la prueba<br>
	![](img/Connucleos.png)
	NOTA: Para sacar el valor de la cantidad de núcleos del procesador se utilizo la funcion (Runtime.getRuntime().availableProcessors())

	* Vistazo con VisualVM mientras se ejecutaba el programa<br>
	![](img/visualVMnucleos.png)

3. Tantos hilos como el doble de núcleos de procesamiento.

	* Resultado al ejecutar el programa con el doble de nucleos del procesador del equipo en el que se esta realizando la prueba<br>
	![](img/Condoblenucleos.png)

	* Vistazo con VisualVM mientras se ejecutaba el programa<br>
	![](img/visualVMdoblenucleos.png)

4. 50 hilos.

    * Resultado al ejecutar el programa con 50 hilos<br>
	![](img/Con50hilos.png) 
	* Vistazo a VisualVM mientras se ejecutaba el programa<br>
	![](img/visualVM50hilos.png)

5. 100 hilos.

	* Resultado al ejecutar el programa con 100 hilos<br>
	![](img/Con100hilos.png) 
	* Vistazo a VisualVM mientras se ejecutaba el programa<br>
	![](img/visualVM100hilos.png)



Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. ![](img/jvisualvm.png)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):

![](img/GraficaComparativa.png)

**Parte IV - Ejercicio Black List Search**

1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png), donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?. 

	__RESPUESTA:__ al ejecutar el programa con 500 hilos no tendriamos una mejora significativa en los tiempos de ejecución, de hecho, al ser un cantidad considerable de hilos la creación de los mismo provocario una demora al ejecutar el programa. Mientras que al comparar el desempeño con 200 hilos la diferencia en tiempos, es decir, a mayor cantidad de hilos mayor sera el tiempo de ejecución por la creación de los n hilos. 

2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.

	__RESPUESTA:__ Al ver los resultados al ejecutar el problema vemos que con 8 núcleos tenemos un tiempo de 18581 milisegundos mientras que al ejecutar con el doble de los núcleos tenemos un tiempo de 10160 milisegundos, esto nos dice que aunque la cantidad de hilos sea el doble no significa que va a realizar la ejecución del problema, se acerca pero no es correcto afirmar lo que se menciono anteriormente.

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.

	__RESPUESTA:__

	* __100 hilos en una sola CP__ en este caso se aplica la ley de Amdahl la capacidad de un solo núcleo de procesar los 100 hilos que se quieren ejecutar, seguira viendose afectado el rendimiento del programa por la capacidad de procesamiento de la CPU.
	* __100 hilos en 100 máquinas__ en este caso se aprovechan al máximos los recursos. Sin embargo, la ley de Amdahl se seguira aplicando ya que seguiran existiendo partes del código que no se podran paralelizar y esto a la larga, limitara el rendimiento del programa.
	* __C hilos en 100/c máquinas__ aunque es una solución viable en terminos de rendimiento si se tienen determinadas condiciones, como el tamaño de C y que una parte significativa del código sea paralelizable. Sin embargo, la comuncicación entre las máquinas seguiran afectando el rendimineto del progama.



