package com.example.practicaFinal.repositories;

import com.example.practicaFinal.entities.Empleado;
import com.example.practicaFinal.entities.EmpleadoProyecto;
import com.example.practicaFinal.entities.EmpleadoProyectoId;
import com.example.practicaFinal.entities.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmplProRepository extends JpaRepository<EmpleadoProyecto, EmpleadoProyectoId> {

    boolean existsByEmpleadoAndProyecto(Empleado empleado, Proyecto proyecto);
}
