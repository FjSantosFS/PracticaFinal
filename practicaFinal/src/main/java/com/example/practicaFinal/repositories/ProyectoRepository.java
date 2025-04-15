package com.example.practicaFinal.repositories;

import com.example.practicaFinal.entities.Empleado;
import com.example.practicaFinal.entities.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    boolean existsByDescripcionIgnoreCase(String descripcion);

    List<Proyecto> findByDescripcionContainingIgnoreCaseAndFechaBajaIsNull(String descripcion);

    @Query(value = "SELECT * FROM proyectos WHERE F_BAJA IS NULL", nativeQuery = true)
    List<Proyecto> findProyectos();

}
