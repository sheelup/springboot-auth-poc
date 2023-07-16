package com.sheelu.spring.auth.dao;

import com.sheelu.spring.auth.models.Timezone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimezoneRepository extends JpaRepository<Timezone, Long> {
    Optional<Timezone> findByExternalId(String externalId);
}
