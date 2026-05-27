package com.school.academic.department;

import com.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repo;

    public List<Department> getAll() { return repo.findAll(); }
    public Department getById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }
    public Department create(Department dept) { return repo.save(dept); }
    public Department update(UUID id, Department dept) {
        Department existing = getById(id);
        existing.setName(dept.getName());
        existing.setHeadTeacher(dept.getHeadTeacher());
        return repo.save(existing);
    }
    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Department", "id", id);
        repo.deleteById(id);
    }
}
