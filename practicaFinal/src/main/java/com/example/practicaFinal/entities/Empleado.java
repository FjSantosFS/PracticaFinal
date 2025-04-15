package com.example.practicaFinal.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EMPLEADOS", schema = "PRACTICA")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPLEADO", nullable = false)
    private Long id;

    @NotBlank(message = "El DNI no puede estar vacío ni contener solo espacios en blanco")
    @Size(min = 9, max = 9, message = "El DNI debe tener exactamente 9 caracteres")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe contener 8 números seguidos de una letra mayúscula")
    @Column(name = "NIF", length = 9, unique = true, nullable = false)
    private String nif;

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 2, max = 30, message = "El nombre debe tener entre 2 y 30 caracteres")
    @Column(name = "NOMBRE", length = 30, nullable = false)
    private String nombre;

    @NotNull(message = "El primer apellido no puede ser nulo")
    @Size(min = 2, max = 40, message = "El primer apellido debe tener entre 2 y 40 caracteres")
    @Column(name = "APELLIDO1", length = 40, nullable = false)
    private String apellido1;

    @NotNull(message = "El segundo apellido no puede ser nulo")
    @Size(min = 2, max = 40, message = "El segundo apellido debe tener entre 2 y 40 caracteres")
    @Column(name = "APELLIDO2", length = 40, nullable = false)
    private String apellido2;

    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    @Column(name = "F_NACIMIENTO", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @NotNull(message = "El teléfono no puede ser nulo")
    @Size(min = 9, max = 9, message = "El teléfono debe tener 9 caracteres")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos") // [hecho por mí] ajustado el regex para que valide solo 9 dígitos exactos
    @Column(name = "TELEFONO1", length = 9, nullable = false, unique = true) // [hecho por mí] cambiado length a 9 para que coincida con validación
    private String telefono1;

    @Pattern(
            regexp = "^$|^[0-9]{9}$",
            message = "El teléfono debe tener exactamente 9 números o estar vacío"
    )
    @Column(name = "TELEFONO2", length = 9) // [hecho por mí] añadido length para mantener consistencia
    private String telefono2;

    @NotBlank(message = "El correo electrónico no puede ser nulo")
    @Email(message = "El correo electrónico debe ser válido")
    @Column(name = "EMAIL", length = 40, nullable = false, unique = true)
    private String email;

    @NotNull(message = "La fecha de alta no puede ser nula")
    @PastOrPresent(message = "La fecha de alta no puede ser posterior a la fecha actual")
    @Column(name = "F_ALTA", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;

    @Column(name = "F_BAJA")
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;

    @NotNull(message = "El estado civil no puede ser nulo")
    @Pattern(regexp = "S|C", message = "El estado civil solo puede ser 'S' (Soltero) o 'C' (Casado)")
    @Column(name = "EDOCIVIL", length = 1, nullable = false)
    private String estadoCivil;

    @NotNull(message = "La formación universitaria no puede ser nula")
    @Pattern(regexp = "^(Si|No)$", message = "La formación universitaria debe ser 'Si' o 'No'")
    @Column(name = "FORMACION", length = 2, nullable = false)
    private String formacionUniversitaria;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "empleados")
    @JsonBackReference
    private Set<Proyecto> proyectos = new HashSet<>();

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido1() { return apellido1; }
    public void setApellido1(String apellido1) { this.apellido1 = apellido1; }
    public String getApellido2() { return apellido2; }
    public void setApellido2(String apellido2) { this.apellido2 = apellido2; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getTelefono1() { return telefono1; }
    public void setTelefono1(String telefono1) { this.telefono1 = telefono1; }
    public String getTelefono2() { return telefono2; }
    public void setTelefono2(String telefono2) { this.telefono2 = telefono2; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Date fechaAlta) { this.fechaAlta = fechaAlta; }
    public Date getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(Date fechaBaja) { this.fechaBaja = fechaBaja; }
    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }
    public String getFormacionUniversitaria() { return formacionUniversitaria; }
    public void setFormacionUniversitaria(String formacionUniversitaria) { this.formacionUniversitaria = formacionUniversitaria; }
    public Set<Proyecto> getProyectos() { return proyectos; }
    public void setProyectos(Set<Proyecto> proyectos) { this.proyectos = proyectos; }
}

