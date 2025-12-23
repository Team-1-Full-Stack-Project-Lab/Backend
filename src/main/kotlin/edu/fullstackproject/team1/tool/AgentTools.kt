package edu.fullstackproject.team1.tool

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core. tools.annotations.Tool
import edu.fullstackproject.team1.repositories.StayServiceRepository
import edu.fullstackproject.team1.services.CityService
import edu.fullstackproject.team1.services.ServiceService
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
	private val stayImageService: StayImageService,
	private val serviceService: ServiceService,
	private val stayServiceRepository: StayServiceRepository
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
		val cityPopulation: Int?,
		val services: List<String>?,
		val minPrice: Double?,
		val maxPrice: Double?
	)

	@Serializable
	data class ServiceInfo(
		val id: Long,
		val name: String
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
		Retrieves all available services/amenities that hotels can offer.

		RETURNED INFORMATION:
		- id:  Unique service ID (use this ID for filtering hotels)
		- name: Service name (e.g., "WiFi", "Breakfast", "Pool", "Parking")

		USAGE:
		- Use this tool first to get service IDs when users ask for hotels with specific amenities
		- Example: If user asks for "hotels with WiFi and breakfast", call this tool to get the IDs of those services
		- Then use searchHotelsWithFilters with the obtained service IDs

		COMMON SERVICE NAMES TO LOOK FOR:
		- WiFi, Internet, Wireless
		- Breakfast, Desayuno, Comida
		- Pool, Piscina, Swimming
		- Gym, Gimnasio, Fitness
		- Air Conditioning, Aire Acondicionado, AC
		- Pet Friendly, Mascotas
		- Beach Access, Acceso Playa
	""")
	fun getAvailableServices(): List<ServiceInfo> {
		return serviceService.getAllServices().map { service ->
			ServiceInfo(
				id = service.id!! ,
				name = service.name
			)
		}
	}

	@Tool
	@LLMDescription("""
		Searches for hotels with advanced filters including services, price range, and location.

		PARAMETERS:
		- cityId: (Optional) Filter by specific city ID.  Use getCities() to get city IDs first
		- serviceIds: (Optional) List of service IDs that hotels MUST have ALL of them.  Use getAvailableServices() to get service IDs first
		- minPrice: (Optional) Minimum price per night in the local currency
		- maxPrice: (Optional) Maximum price per night in the local currency
		- limit: (Optional) Maximum number of results to return (default: 20, max: 50)

		RETURNED INFORMATION:
		- id: Hotel unique ID
		- name: Hotel name
		- address: Hotel address
		- latitude, longitude: Exact location
		- imageUrl: First hotel image (may be null)
		- cityName, cityLatitude, cityLongitude: City information
		- cityIsCapital, cityPopulation: City details
		- services: List of service names the hotel offers
		- minPrice:  Cheapest room price per night
		- maxPrice: Most expensive room price per night

		USAGE WORKFLOW:

		1. FOR AMENITY-BASED SEARCHES:
		   User:  "Hotels with WiFi and breakfast in Santiago"
		   Step 1: Call getAvailableServices() → get WiFi ID (e.g., 1) and Breakfast ID (e.g., 3)
		   Step 2: Call getCities() → get Santiago ID (e.g., 5)
		   Step 3: Call searchHotelsWithFilters(cityId=5, serviceIds=[1, 3])

		2. FOR PRICE-BASED SEARCHES:
		   User: "Hotels under $100 per night"
		   Step 1: Call searchHotelsWithFilters(maxPrice=100.0)

		   User: "Hotels between $50 and $150"
		   Step 1: Call searchHotelsWithFilters(minPrice=50.0, maxPrice=150.0)

		3. FOR COMBINED FILTERS:
		   User: "Budget hotels with pool in coastal cities"
		   Step 1: Call getAvailableServices() → get Pool ID
		   Step 2: Call getCities() → identify coastal cities
		   Step 3: Call searchHotelsWithFilters for each coastal city with pool filter
		   OR:  Call searchHotelsWithFilters(serviceIds=[poolId], maxPrice=80.0)

		4. FOR LOCATION-ONLY SEARCHES:
		   User:  "Hotels in Valparaíso"
		   Step 1: Call getCities() → get Valparaíso ID
		   Step 2: Call searchHotelsWithFilters(cityId=valparaisoId)

		IMPORTANT NOTES:
		- If serviceIds is provided, hotels MUST have ALL specified services (AND logic)
		- Price filters apply to room units within each hotel
		- Hotels are returned with their ACTUAL service names and price ranges
		- Always call getAvailableServices() FIRST when filtering by amenities
		- Results are ordered by creation date (newest first)

		FILTER BEHAVIOR:
		- cityId: Exact match only
		- serviceIds: Hotel must have ALL services in the list
		- minPrice/maxPrice: At least one room unit must fall within the range
	""")
	fun searchHotelsWithFilters(
		cityId: Long?,
		serviceIds: List<Long>?,
		minPrice: Double?,
		maxPrice: Double?,
		limit: Int = 20
	): List<StayInfo> {
		val pageable = PageRequest.of(0, minOf(limit, 50))
		val stays = stayService.getAllStays(
			companyId = null,
			cityId = cityId,
			serviceIds = serviceIds,
			minPrice = minPrice,
			maxPrice = maxPrice,
			pageable = pageable
		)
		return stays.content.mapNotNull { stay ->
			try {
				val firstImage = if (stay.id != null) {
					try {
						stayImageService. getAllStayImages()
							.firstOrNull { it.stay?.id == stay.id }
							?.link
					} catch (e: Exception) {
						null
					}
				} else null
				val services = try {
					stayServiceRepository.findByStayIdWithService(stay.id!!)
						.map { it.service. name }
				} catch (e: Exception) {
					emptyList()
				}
				val prices = try {
					stay.stayUnits.map { it.pricePerNight.toDouble() }
				} catch (e: Exception) {
					emptyList()
				}
				StayInfo(
					id = stay.id!!,
					name = stay.name,
					address = stay.address,
					latitude = stay.latitude,
					longitude = stay.longitude,
					imageUrl = firstImage,
					cityName = stay.city.name,
					cityLatitude = stay.city.latitude,
					cityLongitude = stay.city.longitude,
					cityIsCapital = stay.city.isCapital,
					cityPopulation = stay.city.population,
					services = services,
					minPrice = prices.minOrNull(),
					maxPrice = prices.maxOrNull()
				)
			} catch (e: Exception) {
				null
			}
		}
	}

	@Tool
	@LLMDescription("""
		Obtains all hotels (stays) available in the system with detailed information about their location.

		NOTE: For filtering by services or price, use searchHotelsWithFilters() instead.
		This tool returns unfiltered results.

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
		- services: List of services/amenities the hotel offers
		- minPrice:  Cheapest room price per night
		- maxPrice: Most expensive room price per night

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
		- For filtered searches (by amenities, price, location), use searchHotelsWithFilters()
		- Any other criteria mentioned by the user

		IMPORTANT: When returning hotels to the user, you must provide:
		1. A conversational message explaining the results
		2. A JSON array with the filtered hotels in this format:
		   [{"id": 1, "name": "...", "address": "...", "latitude": X, "longitude": Y, "imageUrl": "..."}, ...]

		Include ONLY the fields: id, name, address, latitude, longitude, imageUrl in the response JSON.
	""")
	fun getAllHotels(): List<StayInfo> {
		val pageable = PageRequest.of(0, 50)
		val stays = stayService.getAllStays(
			companyId = null,
			cityId = null,
			serviceIds = null,
			minPrice = null,
			maxPrice = null,
			pageable = pageable
		)
		return stays. content.mapNotNull { stay ->
			try {
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
				val stayServices = stayServiceRepository.findByStayIdWithService(stay.id ?: 0)
				val services = stayServices.map { it. service.name }
				val prices = try {
					stay.stayUnits.map { it.pricePerNight.toDouble() }
				} catch (e: Exception) {
					emptyList()
				}

				StayInfo(
					id = stay.id ?: 0,
					name = stay.name,
					address = stay.address,
					latitude = stay.latitude,
					longitude = stay.longitude,
					imageUrl = firstImage,
					cityName = stay.city.name,
					cityLatitude = stay.city.latitude,
					cityLongitude = stay. city.longitude,
					cityIsCapital = stay.city.isCapital,
					cityPopulation = stay.city.population,
					services = services,
					minPrice = prices.minOrNull(),
					maxPrice = prices.maxOrNull()
				)
			} catch (e: Exception) {
				null
			}
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
			val stayServices = stayServiceRepository.findByStayIdWithService(stay.id ?: 0)
			val services = stayServices.map { it. service.name }
			val prices = try {
				stay.stayUnits.map { it.pricePerNight.toDouble() }
			} catch (e: Exception) {
				emptyList()
			}
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
				cityPopulation = stay.city?.population,
				services = services,
				minPrice = prices.minOrNull(),
				maxPrice = prices.maxOrNull()
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
