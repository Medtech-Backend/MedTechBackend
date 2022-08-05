package com.project.medtech.model;

import com.project.medtech.dto.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`user`")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq"
    )
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;

    private String phoneNumber;

    private boolean otpUsed;

    private String resetCode;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(
            name = "role_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FKUSERROLE")
    )
    private RoleEntity roleEntity;

    @OneToOne(mappedBy = "userEntity")
    private PatientEntity patientEntity;

    @OneToOne(mappedBy = "userEntity")
    private DoctorEntity doctorEntity;

    @OneToOne(
            cascade = CascadeType.ALL,
            mappedBy = "userEntity"
    )
    private ImageEntity imageEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleEntity.getAuthorities();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status.equals(Status.ACTIVE);
    }

    public static UserDetails getUserDetails(UserEntity userEntity) {
        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(), userEntity.getPassword(),
                userEntity.getStatus().equals(Status.ACTIVE),
                true,
                true,
                true,
                userEntity.roleEntity.getAuthorities()
        );
    }

}
