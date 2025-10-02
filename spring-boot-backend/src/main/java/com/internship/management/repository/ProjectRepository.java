package com.internship.management.repository;

import com.internship.management.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByEncadreurId(Long encadreurId);
    List<Project> findByDepartment(String department);
}
