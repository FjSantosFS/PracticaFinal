package com.example.practicaFinal.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "EMPLEADOS_PROYECTO", schema = "PRACTICA")
@IdClass(EmpleadoProyectoId.class)
public class EmpleadoProyecto {

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_EMPLEADO", nullable = false)
    private Empleado empleado;

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @Column(name = "F_ALTA")
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;

    // Getters y Setters
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }
    public Date getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Date fechaAlta) { this.fechaAlta = fechaAlta; }
}

