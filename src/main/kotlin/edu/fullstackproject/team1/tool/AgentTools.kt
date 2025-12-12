package edu.fullstackproject.team1.tool

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core. tools.annotations.Tool
import edu.fullstackproject.team1.services.CityService
import edu.fullstackproject.team1.services.StayImageService
import edu. fullstackproject.team1. services.StayService
import java.time.LocalDate
import kotlinx.serialization.Serializable
import org.springframework.data. domain.PageRequest
import org. springframework.stereotype.Component

@Component
class AgentTools(
	private val cityService: CityService,
	private val stayService: StayService,
	private val stayImageService: StayImageService
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
		val id: Long,
		val name: String,
		val address: String,
		val latitude: Double,
		val longitude: Double,
		val imageUrl: String?,
		val cityName: String,
		val cityLatitude: Double,
		val cityLongitude: Double,
		val cityIsCapital: Boolean,
		val cityPopulation: Int?
	)

	@Tool
	@LLMDescription("""
		Retrieves all available cities in the database with detailed geographic information.

		RETURNED INFORMATION:
		- name: City name
		- latitude: City latitude (useful for determining climate and hemisphere)
		- longitude: City longitude
		- isCapital: Whether it is a capital city
		- population: City population

		HOW TO INTERPRET THE DATA:
		- Negative latitude: Southern Hemisphere (summer: Dec–Feb, winter: Jun–Aug)
		- Positive latitude: Northern Hemisphere (summer: Jun–Aug, winter: Dec–Feb)
		- Latitude between -23.5 and 23.5: Tropical Zone (warm year-round)
		- Latitude between ±23.5 and ±35: Subtropical Zone
		- Latitude between ±35 and ±50: Temperate Zone
		- Latitude between ±50 and ±66.5: Cold Zone
		- Latitude greater than ±66.5: Polar Zone
		- Cities with names containing “viña”, “valparaíso”, “mar”, “costa”, “beach”, “bay”, “puerto”, “port”, “playa” are often coastal
		- Cities near oceans (longitude close to continental coasts) may be coastal

		Use this information to answer questions about locations, climates, and types of cities.
	""")
	fun getCities(): List<CityInfo> {
		val pageable = PageRequest.of(0, 50)
		return cityService.getAllCities(pageable).map { city ->
			CityInfo(
				name = city.name,
				latitude = city.latitude,
				longitude = city.longitude,
				isCapital = city.isCapital,
				population = city.population
			)
		}.content
	}

	@Tool
	@LLMDescription("""
		Obtains all hotels (stays) available in the system with detailed information about their location.

		RETURNED INFORMATION:
		- id: Unique ID of the hotel
		- name: Hotel name
		- address: Hotel address
		- latitude: Exact latitude of the hotel
		- longitude: Exact longitude of the hotel
		- imageUrl: URL of the hotel's first image (may be null)
		- cityName: City where it is located
		- countryName: Country where it is located
		- cityLatitude: Latitude of the city (for climate analysis)
		- cityLongitude: Longitude of the city
		- cityIsCapital: Whether it is in a capital city
		- cityPopulation: Population of the city

		HOW TO USE THIS INFORMATION:
		1. To filter by geographic location, use latitude/longitude
		2. To determine climate and season, use latitude and the queried date
		3. To identify coastal cities, analyze the city name and coordinates
		4. For large vs. small cities, use cityPopulation
		5. For major urban destinations, check cityIsCapital

		NOTE: This function returns ALL hotels. The agent must apply filters based on:
		- User climate preferences
		- Type of destination requested (beach, mountain, city, etc.)
		- Season of the year based on date and hemisphere
		- Any other criteria mentioned by the user

		IMPORTANT: When returning hotels to the user, you must provide:
		1. A conversational message explaining the results
		2. A JSON array with the filtered hotels in this format:
		   [{"id": 1, "name": "...", "address": "...", "latitude": X, "longitude": Y, "imageUrl": "..."}, ...]

		Include ONLY the fields: id, name, address, latitude, longitude, imageUrl in the response JSON.
	""")
	fun getAllHotels(): List<StayInfo> {
		val pageable = PageRequest.of(0, 50)
		return stayService.getAllStays(pageable). content.map { stay ->
			val stayId = stay.id
			val firstImage = if (stayId != null) {
				try {
					stayImageService. getAllStayImages()
						.firstOrNull { it.stay?.id == stayId }
						?.link
				} catch (e: Exception) {
					null
				}
			} else null
			StayInfo(
				id = stay.id ?: 0,
				name = stay.name,
				address = stay.address,
				latitude = stay.latitude ?: stay.city?.latitude ?: 0.0,
				longitude = stay.longitude ?: stay.city?.longitude ?: 0.0,
				imageUrl = firstImage,
				cityName = stay.city?. name ?: "N/A",
				cityLatitude = stay.city?.latitude ?: 0.0,
				cityLongitude = stay.city?.longitude ?: 0.0,
				cityIsCapital = stay.city?.isCapital ?: false,
				cityPopulation = stay.city?. population
			)
		}
	}

	@Tool
	@LLMDescription("""
		Obtains all hotels from a specific city.

		PARAMETERS:
		- cityName: Exact or approximate name of the city

		RETURNED INFORMATION:
		- List of hotels with their complete information (same structure as getAllHotels)

		USE THIS FUNCTION when:
		- The user specifically asks for hotels in a known city
		- You have already identified a city of interest and need its hotels
		- You want detailed information about hotels in a specific location

		Returns the same information as getAllHotels() but filtered by city.
	""")
	fun getHotelsByCity(
		@LLMDescription("Name of the city for which the hotels are to be obtained")
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
			val stayId = stay.id
			val firstImage = if (stayId != null) {
				try {
					stayImageService. getAllStayImages()
						.firstOrNull { it.stay?.id == stayId }
						?.link
				} catch (e: Exception) {
					null
				}
			} else null
			StayInfo(
				id = stay.id ?: 0,
				name = stay.name,
				address = stay.address,
				latitude = stay.latitude ?: stay.city?.latitude ?: 0.0,
				longitude = stay.longitude ?: stay.city?.longitude ?: 0.0,
				imageUrl = firstImage,
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
		Gets the current system date for calculations and comparisons.

		RETURNS:
		- Current date in YYYY-MM-DD format

		USE THIS FUNCTION to:
		- Know what today’s date is
		- Calculate relative dates (e.g., “in 2 weeks” = today + 14 days)
		- Determine the current season in different hemispheres
		- Interpret holiday dates for the current year

		COMMON HOLIDAYS:
		- Christmas: December 25
		- New Year’s Day: January 1
		- Christmas Eve: December 24
		- New Year’s Eve: December 31
		- Epiphany (Three Kings’ Day): January 6
		- Valentine’s Day: February 14
		- Halloween: October 31
		- Holy Week: variable (March/April, depends on the specific year)
		- Chilean Independence Day: September 18

		NOTE: Combine this date with the latitude of the cities to determine:
		- What season it will be in that location
		- Whether it will be high/low tourist season
		- Expected weather for that date and location
	""")
	fun getCurrentDate(): String {
		return LocalDate.now().toString()
	}
}
