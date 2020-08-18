package com.qdm.cs.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.UploadProfile;

@Repository
public interface UploadProfileRepository extends JpaRepository<UploadProfile, Integer> {

}
