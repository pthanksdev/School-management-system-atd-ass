package com.school.academic.academicyear;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
    Optional<AcademicYear> findByIsCurrentTrue();

    @Modifying
    @Query("UPDATE AcademicYear a SET a.isCurrent = false WHERE a.isCurrent = true")
    void clearCurrentYear();
}
