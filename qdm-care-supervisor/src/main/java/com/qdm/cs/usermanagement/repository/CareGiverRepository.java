package com.qdm.cs.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.CareGiver;

@Repository
public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {

	Optional<CareGiver> findByCareGiverId(long careGiverId);

}
