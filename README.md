# harenkaren
adaptacion de https://github.com/gustavomarcelonunez/fishing-app para monitoreo de mamiferos, en especial censo de elefantes marinos

## Resumen
habitualmente los trabajos en campo implican el uso de papel y lápiz, lo más portable por mucho. esta app pretende (con exagerado optimismo) ser un digno reemplazo de aquello. en este sentido, la navegabilidad de pantallas busca guiar al censista en la definición de los datos,  
desde lo general a lo particular. asi el usuario primeramente dará de alta un **Dia** (como unidad de tiempo en que sucede un acto de censar), dentro de este definirá **Recorridos** (como descriptor de áreas geográficas donde sucede el acto de censar) y finalmente agregará tantos **Registros** como unidades sociales observe en el campo.

## Implementación

### Datos
la persistencia de datos se basa en SQLite, pero no mediante su API sino a través de la biblioteca nativa ROOM, la cual incorpora verificación de consultas en tiempo de compilación y uso de anotaciones (paradigma declarativo)

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
||`celularId`            				|String|
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
||`photoPath`            				|String|
||`comentario`            			|String|
|-|-|-|
