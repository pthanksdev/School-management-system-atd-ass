package com.school.academic.academicyear;

import com.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    public List<AcademicYear> getAll() {
        return academicYearRepository.findAll();
    }

    public AcademicYear getById(UUID id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic Year", "id", id));
    }

    public AcademicYear getCurrent() {
        return academicYearRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No current academic year set"));
    }

    @Transactional
    public AcademicYear create(AcademicYear year) {
        if (year.isCurrent()) {
            academicYearRepository.clearCurrentYear();
        }
        return academicYearRepository.save(year);
    }

    @Transactional
    public AcademicYear setCurrent(UUID id) {
        AcademicYear year = getById(id);
        academicYearRepository.clearCurrentYear();
        year.setCurrent(true);
        return academicYearRepository.save(year);
    }
}
