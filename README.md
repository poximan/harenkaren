# harenkaren
adaptacion de https://github.com/gustavomarcelonunez/fishing-app para monitoreo de mamiferos, en especial censo de elefantes marinos

## Resumen
habitualmente los trabajos en campo implican el uso de papel y lápiz, lo más portable por mucho. esta app pretende (con exagerado optimismo) ser un digno reemplazo de aquello. en este sentido la navegabilidad de pantallas busca guiar al censista en la definición de los datos, desde lo general a lo particular.
asi el usuario primeramente dará de alta un **Dia** (como unidad de tiempo en que sucede un acto de censar), dentro de este definirá **Recorridos** (como descriptor de áreas geográficas donde sucede el acto de censar) y finalmente agregará tantos **Registros** como unidades sociales observe en el campo.

esta primer actividad constituye lo que bien puede llamarse generacion del **dato primario**, ya que a partir de él explotarán una serie de modestos pero contundentes instrumentos de interpretacion de datos.
entre ellos podemos -y debemos- nombrar:
- generacion de graficos de barras apiladas: con esto es posible comparar visualmente registros entre si, tener informacion relativa, tanto en proporciones como existencia ya que las barras respetan el criterio `mismo color, misma categoria`
- despliegue de mapas geoposicionados: con esta app queda bien claro por qué es necesario compartir la ubicacion, ya que dispone de un mapa con todos los puntos registrados, con informacion agregada que ¿sale de donde...? ¡si, del dato primario!
- generacion de mapas de calor: usando de soporte los mapas geoposicionados, se puede acceder a un mapa de calor autoajustable en x/y/z, para analizar donde se observaron las muestras mas densas.

## Implementación
### Bibliotecas
se utilizó el `conjunto de bibliotecas Android Jetpack` para seguir las prácticas recomendadas y escribir código que funcione de manera coherente entre dispositivos y versiones de Android.
es retrocompatible y gestiona el ciclo de vida de los objetos. dentro de jetpack, este proyecto hace uso de las bibliotecas activity, fragment, databinding, navigation, room, constraintlayout, recyclerview, viewpager, entre otras

es de especial mencion la biblioteca navigation, que ofrece al usuario una navegacion entre pantallas consistente, regular y que favorece el abordaje heuristico; aprendemos usando, porque respeta cierta naturalidad en la navegacion. 
### Datos
la persistencia de datos se basa en SQLite, pero no mediante su API sino a través de la biblioteca ROOM como parte de androix Jetpack, la cual incorpora verificación de consultas en tiempo de compilación y uso de anotaciones (paradigma declarativo)

### ID's
se utilizó un modelo de UUID para distinguir unívocamente cada entidad, pensando en que se trata de datos distribuidos, y que deben ser distinguibles incluso fuera del ámbito donde fueron creados.

### Modelo datos

|Entidad			|Atributo                       |Tipo                         |
|-------------|-------------------------------|-----------------------------|
|Dia					|`celularId`			|String|
||`id`(PK)											|UUID|
||`orden`            						|Int|
||`fecha`            						|String|
||`celularId`            				|String|
|-|-|-|
|Recorrido|`id`(PK)        			|UUID|
||`diaId`(FK)										|UUID|
||`orden`            						|Int|
||`observador`            			|String|
||`fechaIni`            				|String|
||`fechaFin`            				|String|
||`latitudIni`            			|Double|
||`longitudIni`            			|Double|
||`latitudFin`            			|Double|
||`longitudFin`            			|Double|
||`areaRecorrida`            		|String|
||`meteo`            						|String|
||`marea`            						|String|
|-|-|-|
|UnidSocial|`id`(PK)         		|UUID|
||`recorrId`(FK)								|UUID|
||`orden`            						|Int|
||`ptoObsUnSoc`            			|String|
||`ctxSocial`            				|String|
||`tpoSustrato`            			|String|
||`vAlfaS4Ad`            				|Int|
||`vAlfaSams`            				|Int|
||`vHembrasAd`            			|Int|
||`vCrias`            					|Int|
||`vDestetados`            			|Int|
||`vJuveniles`            			|Int|
||`vS4AdPerif`            			|Int|
||`vS4AdCerca`            			|Int|
||`vS4AdLejos`            			|Int|
||`vOtrosSamsPerif`            	|Int|
||`vOtrosSamsCerca`            	|Int|
||`vOtrosSamsLejos`            	|Int|
||`mAlfaS4Ad`            				|Int|
||`mAlfaSams`            				|Int|
||`mHembrasAd`            			|Int|
||`mCrias`            					|Int|
||`mDestetados`            			|Int|
||`mJuveniles`            			|Int|
||`mS4AdPerif`            			|Int|
||`mS4AdCerca`            			|Int|
||`mS4AdLejos`            			|Int|
||`mOtrosSamsPerif`            	|Int|
||`mOtrosSamsCerca`            	|Int|
||`mOtrosSamsLejos`            	|Int|
||`date`          							|String|
||`latitud`            					|Double|
||`longitud`            				|Double|
||`comentario`            			|String|
|-|-|-|
