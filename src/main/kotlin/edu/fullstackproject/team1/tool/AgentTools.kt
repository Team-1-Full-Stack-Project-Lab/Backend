package edu.fullstackproject.team1.tool

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core. tools.annotations.Tool
import edu.fullstackproject.team1.services.CityService
import edu. fullstackproject.team1. services.StayService
import java.time.LocalDate
import kotlinx.serialization.Serializable
import org.springframework.data. domain.PageRequest
import org. springframework.stereotype.Component

@Component
class AgentTools(
	private val cityService: CityService,
	private val stayService: StayService
) {
	@Serializable
	data class CityInfo(
		val name: String,
		val latitude: Double,
		val longitude: Double,
		val isCapital: Boolean,
		val population: Int?
	)

	@Serializable
	data class StayInfo(
		val name: String,
		val address: String,
		val cityName: String,
		val cityLatitude: Double,
		val cityLongitude: Double,
		val cityIsCapital: Boolean,
		val cityPopulation: Int?
	)

	@Tool
	@LLMDescription("""
		Obtiene todas las ciudades disponibles en la base de datos con información geográfica detallada.

		INFORMACIÓN RETORNADA:
		- name: Nombre de la ciudad
		- country: Nombre del país
		- latitude: Latitud de la ciudad (útil para determinar clima y hemisferio)
		- longitude: Longitud de la ciudad
		- isCapital: Si es ciudad capital
		- population: Población de la ciudad

		CÓMO INTERPRETAR LOS DATOS:
		- Latitud negativa: Hemisferio Sur (verano: dic-feb, invierno: jun-ago)
		- Latitud positiva: Hemisferio Norte (verano: jun-ago, invierno: dic-feb)
		- Latitud entre -23.5 y 23.5: Zona Tropical (cálido todo el año)
		- Latitud entre ±23.5 y ±35: Zona Subtropical
		- Latitud entre ±35 y ±50: Zona Templada
		- Latitud entre ±50 y ±66. 5: Zona Fría
		- Latitud mayor a ±66.5: Zona Polar
		- Ciudades con nombres que contienen "viña", "valparaíso", "mar", "costa", "beach", "bay", "puerto", "port", "playa" suelen ser costeras
		- Ciudades cercanas a océanos (longitud cerca de costas continentales) pueden ser costeras

		Usa esta información para responder preguntas sobre ubicaciones, climas y tipos de ciudades.
	""")
	fun getCities(): List<CityInfo> {
		val pageable = PageRequest. of(0, 50)
		return cityService.getAllCities(pageable).map { city ->
			CityInfo(
				name = city.name,
				latitude = city. latitude,
				longitude = city. longitude,
				isCapital = city.isCapital,
				population = city.population
			)
		}.content
	}

	@Tool
	@LLMDescription("""
		Obtiene todos los hoteles (stays) disponibles en el sistema con información detallada de su ubicación.

		INFORMACIÓN RETORNADA:
		- name: Nombre del hotel
		- address: Dirección del hotel
		- cityName: Ciudad donde se encuentra
		- countryName: País donde se encuentra
		- cityLatitude: Latitud de la ciudad (para análisis climático)
		- cityLongitude: Longitud de la ciudad
		- cityIsCapital: Si está en una ciudad capital
		- cityPopulation: Población de la ciudad

		CÓMO USAR ESTA INFORMACIÓN:
		1. Para filtrar por ubicación geográfica, usa latitude/longitude
		2. Para determinar clima y estación, usa latitude y la fecha consultada
		3. Para identificar ciudades costeras, analiza el nombre de la ciudad y coordenadas
		4. Para ciudades grandes vs pequeñas, usa cityPopulation
		5. Para destinos urbanos importantes, verifica cityIsCapital

		NOTA: Esta función devuelve TODOS los hoteles.  El agente debe aplicar filtros según:
		- Preferencias climáticas del usuario
		- Tipo de destino solicitado (playa, montaña, ciudad, etc.)
		- Estación del año basada en la fecha y hemisferio
		- Cualquier otro criterio mencionado por el usuario
	""")
	fun getAllHotels(): List<StayInfo> {
		val pageable = PageRequest.of(0, 50)
		return stayService.getAllStays(pageable). content.map { stay ->
			StayInfo(
				name = stay.name,
				address = stay.address,
				cityName = stay.city?.name ?: "N/A",
				cityLatitude = stay. city?.latitude ?: 0.0,
				cityLongitude = stay.city?.longitude ?: 0.0,
				cityIsCapital = stay.city?. isCapital ?: false,
				cityPopulation = stay.city?.population
			)
		}
	}

	@Tool
	@LLMDescription("""
		Obtiene todos los hoteles de una ciudad específica.

		PARÁMETROS:
		- cityName: Nombre exacto o aproximado de la ciudad

		INFORMACIÓN RETORNADA:
		- Lista de hoteles con su información completa (misma estructura que getAllHotels)

		USA ESTA FUNCIÓN cuando:
		- El usuario pregunta específicamente por hoteles en una ciudad conocida
		- Ya identificaste una ciudad de interés y necesitas sus hoteles
		- Quieres información detallada de hoteles en una ubicación específica
	""")
	fun getHotelsByCity(
		@LLMDescription("Nombre de la ciudad para la cual se desea obtener los hoteles")
		cityName: String
	): List<StayInfo> {
		val pageable = PageRequest.of(0, 50)
		val cities = cityService. getAllCities(pageable). filter {
			it.name.equals(cityName, ignoreCase = true)
		}

		if (cities.isEmpty()) { return emptyList() }

		val city = cities.first()
		val cityId = city.id ?: return emptyList()
		val stays = stayService.getStaysByCity(cityId, pageable)

		return stays.content.map { stay ->
			StayInfo(
				name = stay.name,
				address = stay.address,
				cityName = stay.city?.name ?: cityName,
				cityLatitude = stay.city?. latitude ?: 0.0,
				cityLongitude = stay.city?.longitude ?: 0.0,
				cityIsCapital = stay.city?.isCapital ?: false,
				cityPopulation = stay.city?.population
			)
		}
	}

	@Tool
	@LLMDescription("""
		Obtiene la fecha actual del sistema para cálculos y comparaciones.

		RETORNA:
		- Fecha actual en formato YYYY-MM-DD

		USA ESTA FUNCIÓN para:
		- Saber qué fecha es hoy
		- Calcular fechas relativas (ej: "en 2 semanas" = hoy + 14 días)
		- Determinar la estación actual en diferentes hemisferios
		- Interpretar fechas festivas del año en curso

		FECHAS FESTIVAS COMUNES:
		- Navidad: 25 de diciembre
		- Año Nuevo: 1 de enero
		- Nochebuena: 24 de diciembre
		- Nochevieja: 31 de diciembre
		- Día de Reyes: 6 de enero
		- San Valentín: 14 de febrero
		- Halloween: 31 de octubre
		- Semana Santa: variable (marzo/abril, buscar año específico)
		- Fiestas Patrias Chile: 18 de septiembre

		NOTA: Combina esta fecha con la latitud de las ciudades para determinar:
		- Qué estación del año será en esa ubicación
		- Si será temporada alta/baja turística
		- Clima esperado en esa fecha y ubicación
	""")
	fun getCurrentDate(): String {
		return LocalDate.now().toString()
	}
}
