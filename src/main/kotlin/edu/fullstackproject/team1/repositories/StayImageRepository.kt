package edu.fullstackproject.team1.repositories

import edu.fullstackproject.team1.models.StayImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StayImageRepository : JpaRepository<StayImage, Long> {
	fun findAllByStayId(stayId: Long): List<StayImage>
}
