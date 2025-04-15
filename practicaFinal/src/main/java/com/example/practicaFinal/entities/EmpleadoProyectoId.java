package com.example.practicaFinal.entities;

import java.io.Serializable;
import java.util.Objects;

public class EmpleadoProyectoId implements Serializable {
    private Long empleado;
    private Long proyecto;

    public EmpleadoProyectoId() {}

    public EmpleadoProyectoId(Long empleado, Long proyecto) {
        this.empleado = empleado;
        this.proyecto = proyecto;
    }

    // Getters y Setters
    public Long getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Long empleado) {
        this.empleado = empleado;
    }

    public Long getProyecto() {
        return proyecto;
    }

    public void setProyecto(Long proyecto) {
        this.proyecto = proyecto;
    }

    // equals() y hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmpleadoProyectoId that = (EmpleadoProyectoId) o;
        return Objects.equals(empleado, that.empleado) &&
                Objects.equals(proyecto, that.proyecto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empleado, proyecto);
    }
}
