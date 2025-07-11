package org.example.sba.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_role")
public class Role extends AbstractEntity<Integer> {
    private String name;
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<RoleHasPermission> roles = new HashSet<>();
}
