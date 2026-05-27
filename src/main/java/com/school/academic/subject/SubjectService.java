package com.school.academic.subject;

import com.school.common.exception.BadRequestException;
import com.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository repo;

    public List<Subject> getAll() { return repo.findAll(); }
    public Subject getById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
    }
    public Subject create(Subject subject) {
        if (repo.existsByCode(subject.getCode()))
            throw new BadRequestException("Subject code already exists: " + subject.getCode());
        return repo.save(subject);
    }
    public Subject update(UUID id, Subject subject) {
        Subject existing = getById(id);
        existing.setName(subject.getName());
        existing.setCode(subject.getCode());
        existing.setDepartment(subject.getDepartment());
        return repo.save(existing);
    }
    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Subject", "id", id);
        repo.deleteById(id);
    }
}
