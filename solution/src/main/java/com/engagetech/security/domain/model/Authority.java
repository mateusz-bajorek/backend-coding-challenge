package com.engagetech.security.domain.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authorities")
@Data
public class Authority implements GrantedAuthority {
    private static final long serialVersionUID = 1L;

    @Id
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
