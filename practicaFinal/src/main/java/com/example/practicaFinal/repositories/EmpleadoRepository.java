package com.example.practicaFinal.repositories;

import com.example.practicaFinal.entities.Empleado;
import com.example.practicaFinal.entities.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    boolean existsByNif(String nif);
    boolean existsByTelefono1(String telefono);
    boolean existsByTelefono2(String telefono);
    boolean existsByEmail(String email);

    List<Empleado> findByNombreContainingIgnoreCaseAndFechaBajaIsNull(String nombre);
    @Query(value = "SELECT * FROM empleados WHERE F_BAJA IS NULL", nativeQuery = true)
    List<Empleado> findEmpleados();
}
