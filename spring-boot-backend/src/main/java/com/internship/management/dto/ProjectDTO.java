package com.internship.management.dto;

import com.internship.management.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer progress;
    private String department;
    private Long encadreurId;
    private String encadreurName;

    public static ProjectDTO fromEntity(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus().name())
                .progress(project.getProgress())
                .department(project.getDepartment())
                .encadreurId(project.getEncadreur() != null ? project.getEncadreur().getId() : null)
                .encadreurName(project.getEncadreur() != null ?
                    project.getEncadreur().getUser().getFirstName() + " " +
                    project.getEncadreur().getUser().getLastName() : null)
                .build();
    }
}
