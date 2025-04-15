package com.example.practicaFinal.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PROYECTOS", schema = "PRACTICA")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PROYECTO", nullable = false)
    private Long id;

    @NotNull(message = "La descripción no puede ser nula")
    @Size(min = 5, max = 125, message = "La descripción debe tener entre 5 y 125 caracteres")
    @Column(name = "DESCRIPCION", length = 125, nullable = false, unique = true)
    private String descripcion;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    @PastOrPresent(message = "La fecha de inicio no puede ser posterior a la fecha actual")
    @Column(name = "F_INICIO", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    @Column(name = "F_FIN", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @Column(name = "F_BAJA")
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;

    @Size(min = 2, max = 30, message = "El lugar debe tener entre 2 y 30 caracteres") // [hecho por mí] invertido min/max por orden lógico
    @Column(name = "LUGAR", length = 30)
    private String lugar;

    @NotBlank(message = "La observación no se puede dejar en blanco")
    @Size(max = 300, message = "Las observaciones no pueden tener más de 300 caracteres")
    @Column(name = "OBSERVACIONES", length = 300)
    private String observaciones;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "EMPLEADOS_PROYECTO",
            joinColumns = @JoinColumn(name = "ID_PROYECTO"),
            inverseJoinColumns = @JoinColumn(name = "ID_EMPLEADO")
    )
    private Set<Empleado> empleados = new HashSet<>();

    // Getters y Setters
    public Long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    public Date getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(Date fechaBaja) { this.fechaBaja = fechaBaja; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Set<Empleado> getEmpleados() { return empleados; }
    public void setEmpleados(Set<Empleado> empleados) { this.empleados = empleados; }
}
