package com.qdm.cs.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.CareCoordinator;

@Repository
public interface CareCoordinatorRepository extends JpaRepository<CareCoordinator, Long> {

	Optional<CareCoordinator> findByCareCoordinatorId(long careCoordinatorId);

}
