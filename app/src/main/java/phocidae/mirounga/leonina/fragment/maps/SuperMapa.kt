package phocidae.mirounga.leonina.fragment.maps

import phocidae.mirounga.leonina.model.UnidSocial
import org.osmdroid.util.GeoPoint
import kotlin.math.abs

abstract class SuperMapa {

    internal lateinit var geoPoint: GeoPoint

    abstract fun resolverVisibilidad(unSocList: List<UnidSocial>, atribString: String)

    internal fun puntoMedioPosiciones(unSocList: List<UnidSocial>): GeoPoint {
        val minLatitud = unSocList.minOf { it.latitud }
        val maxLatitud = unSocList.maxOf { it.latitud }
        val minLongitud = unSocList.minOf { it.longitud }
        val maxLongitud = unSocList.maxOf { it.longitud }

        // Calcular los puntos medios respecto a los valores extremos
        val puntoMedioLatitud = (minLatitud + maxLatitud) / 2.0
        val puntoMedioLongitud = (minLongitud + maxLongitud) / 2.0

        val altitud = -1.9481 * abs(minLatitud - maxLatitud) + 12.0
        /*
        para 0.78 dif lat --> 8 altitud
        para 1.55 dif lat --> 6.5 altitud
         */
        return GeoPoint(puntoMedioLatitud, puntoMedioLongitud, altitud)
    }
}
