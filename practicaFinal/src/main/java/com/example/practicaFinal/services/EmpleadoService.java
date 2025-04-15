package com.example.practicaFinal.services;

import com.example.practicaFinal.entities.Empleado;
import com.example.practicaFinal.entities.EmpleadoDTO;
import com.example.practicaFinal.entities.EmpleadoProyecto;
import com.example.practicaFinal.entities.Proyecto;
import com.example.practicaFinal.excepciones.ExcepcionPersonalizada;
import com.example.practicaFinal.repositories.EmplProRepository;
import com.example.practicaFinal.repositories.EmpleadoRepository;
import com.example.practicaFinal.repositories.ProyectoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EmplProRepository emplProRepository;

    /////////VALIDACIONES//////////////////
    private void validarFechasCrear(Empleado empleado) {
        Date fechaNacimiento = empleado.getFechaNacimiento();
        Date fechaActual = new Date();

        // Validar que no sea null antes de calcular edad
        if (fechaNacimiento != null) {
            long milisegundosPorAnio = 1000L * 60 * 60 * 24 * 365;
            long edad = (fechaActual.getTime() - fechaNacimiento.getTime()) / milisegundosPorAnio;

            if (empleado.getFechaAlta() != null) {
                if (empleado.getFechaAlta().before(fechaNacimiento)) {
                    throw new ExcepcionPersonalizada("No puedes darte de alta antes de nacer");
                }

                if (edad < 16) {
                    throw new ExcepcionPersonalizada("No puedes dar de alta a un menor de 16 años");
                }
            }
        }
    }

    private void validarFechasActualizar(Empleado empleado, Empleado details) {
        Date fechaNacimiento = details.getFechaNacimiento();
        Date fechaAlta = details.getFechaAlta();

        if (fechaNacimiento != null && fechaAlta != null) {
            // Validar que la fecha de alta no sea antes del nacimiento
            if (fechaAlta.before(fechaNacimiento)) {
                throw new ExcepcionPersonalizada("No puedes darte de alta antes de nacer");
            }

            // Calcular edad aproximada en años
            Date fechaActual = new Date();
            long milisegundosPorAnio = 1000L * 60 * 60 * 24 * 365;
            long edad = (fechaActual.getTime() - fechaNacimiento.getTime()) / milisegundosPorAnio;

            if (edad < 16) {
                throw new ExcepcionPersonalizada("No puedes dar de alta a un menor de 16 años");
            }
        }
    }

    private void verificarExistenciaNif(Empleado empleado, Empleado empleadoDetails) {
        // Solo verificamos si el NIF ha cambiado
        if (!empleado.getNif().equals(empleadoDetails.getNif()) && empleadoRepository.existsByNif(empleadoDetails.getNif())) {
            throw new ExcepcionPersonalizada("Ya existe un empleado con el DNI " + empleadoDetails.getNif());
        }
    }

    private void verificarExistenciaEmail(Empleado empleado, Empleado empleadoDetails) {
        // Solo verificamos si el NIF ha cambiado
        if (!empleado.getEmail().equals(empleadoDetails.getEmail()) && empleadoRepository.existsByEmail(empleadoDetails.getEmail())) {
            throw new ExcepcionPersonalizada("Ya existe un empleado con el email " + empleadoDetails.getEmail());
        }
    }

    private void verificarExistenciaTel(Empleado empleado, Empleado empleadoDetails) {
        // Verificamos si telefono1 ha cambiado y no es nulo ni vacío
        if (empleadoDetails.getTelefono1() != null
                && !empleadoDetails.getTelefono1().trim().isEmpty() // Verificamos si no es vacío
                && !empleado.getTelefono1().equals(empleadoDetails.getTelefono1())) {

            // Comprobamos si ya existe el teléfono en otros empleados
            if (empleadoRepository.existsByTelefono1(empleadoDetails.getTelefono1())
                    || (empleadoDetails.getTelefono2() != null && empleadoRepository.existsByTelefono2(empleadoDetails.getTelefono1()))) {
                throw new ExcepcionPersonalizada("Ya existe un empleado con el teléfono " + empleadoDetails.getTelefono1());
            }
        }

        // Lo mismo para telefono2, si existe y ha cambiado
        if (empleadoDetails.getTelefono2() != null
                && !empleadoDetails.getTelefono2().trim().isEmpty() // Verificamos si no es vacío
                && (empleado.getTelefono2() == null || !empleado.getTelefono2().equals(empleadoDetails.getTelefono2()))) {

            // Comprobamos si ya existe el teléfono en otros empleados
            if (empleadoRepository.existsByTelefono1(empleadoDetails.getTelefono2())
                    || empleadoRepository.existsByTelefono2(empleadoDetails.getTelefono2())) {
                throw new ExcepcionPersonalizada("Ya existe un empleado con el teléfono " + empleadoDetails.getTelefono2());
            }
        }
    }

    private void actualizarCamposEmpleado(Empleado empleado, Empleado empleadoDetails) {
        empleado.setNif(empleadoDetails.getNif());
        empleado.setNombre(empleadoDetails.getNombre());
        empleado.setApellido1(empleadoDetails.getApellido1());
        empleado.setApellido2(empleadoDetails.getApellido2());
        empleado.setFechaNacimiento(empleadoDetails.getFechaNacimiento());
        empleado.setTelefono1(empleadoDetails.getTelefono1());
        empleado.setTelefono2(empleadoDetails.getTelefono2());
        empleado.setEmail(empleadoDetails.getEmail());
        empleado.setFechaAlta(empleadoDetails.getFechaAlta());
        empleado.setFechaBaja(empleadoDetails.getFechaBaja());
        empleado.setEstadoCivil(empleadoDetails.getEstadoCivil());
        empleado.setFormacionUniversitaria(empleadoDetails.getFormacionUniversitaria());
    }

    private void actualizarFechaBajaEmpleado(Empleado empleado, Empleado empleadoDetails) {
        empleado.setNif(empleadoDetails.getNif());
        empleado.setNombre(empleadoDetails.getNombre());
        empleado.setApellido1(empleadoDetails.getApellido1());
        empleado.setApellido2(empleadoDetails.getApellido2());
        empleado.setFechaNacimiento(empleadoDetails.getFechaNacimiento());
        empleado.setTelefono1(empleadoDetails.getTelefono1());
        empleado.setTelefono2(empleadoDetails.getTelefono2());
        empleado.setEmail(empleadoDetails.getEmail());
        empleado.setFechaAlta(empleadoDetails.getFechaAlta());
        empleado.setFechaBaja(new Date());
        empleado.setEstadoCivil(empleadoDetails.getEstadoCivil());
        empleado.setFormacionUniversitaria(empleadoDetails.getFormacionUniversitaria());
    }

    private void actualizarProyectosEmpleado(Empleado empleado, Empleado empleadoDetails) {
        Set<Proyecto> proyectosActualizados = new HashSet<>();

        if (empleadoDetails.getProyectos() != null && !empleadoDetails.getProyectos().isEmpty()) {
            for (Proyecto proyecto : empleadoDetails.getProyectos()) {
                Proyecto proyectoExistente = proyectoRepository.findById(proyecto.getId())
                        .orElseThrow(() -> new ExcepcionPersonalizada("Proyecto no encontrado con ID: " + proyecto.getId()));

                // Añadir este empleado al proyecto si no está ya asociado
                if (!proyectoExistente.getEmpleados().contains(empleado)) {
                    proyectoExistente.getEmpleados().add(empleado);
                }

                proyectosActualizados.add(proyectoExistente);
            }
        }

        // Eliminar proyectos que ya no están asignados al empleado
        for (Proyecto proyecto : empleado.getProyectos()) {
            if (!proyectosActualizados.contains(proyecto)) {
                proyecto.getEmpleados().remove(empleado);  // Eliminar la relación bidireccional
            }
        }

        // Actualizar los proyectos del empleado
        empleado.setProyectos(proyectosActualizados);
    }

    //////Metodos transactional y GET
    public List<EmpleadoDTO> getEmpleadosFiltrados(String nombre) {
        List<Empleado> empleados = nombre != null && !nombre.isEmpty()
                ? empleadoRepository.findByNombreContainingIgnoreCaseAndFechaBajaIsNull(nombre)
                : empleadoRepository.findEmpleados();

        return empleados.stream()
                .map(empleado -> new EmpleadoDTO(
                        empleado.getId(),
                        empleado.getNif(),
                        empleado.getNombre(),
                        empleado.getApellido1(),
                        empleado.getApellido2(),
                        empleado.getFechaNacimiento(),
                        empleado.getFechaAlta(),
                        empleado.getTelefono1(),
                        empleado.getTelefono2(),
                        empleado.getEmail(),
                        empleado.getEstadoCivil(),
                        empleado.getFormacionUniversitaria(),
                        empleado.getFechaBaja()
                ))
                .collect(Collectors.toList());
    }

    public Optional<Empleado> getEmpleadoById(Long id) {
        return empleadoRepository.findById(id);

    }
    @Transactional
    public Empleado createEmpleado(Empleado empleado) {

        // Verifica si el NIF ya existe en la base de datos
        if (empleadoRepository.existsByNif(empleado.getNif())) {
            throw new ExcepcionPersonalizada("Ya existe un empleado con el DNI " + empleado.getNif());
        }

        validarFechasCrear(empleado);

        // Verificar si el teléfono1 y teléfono2 son iguales
        if (empleado.getTelefono1() != null && empleado.getTelefono2() != null
                && empleado.getTelefono1().equals(empleado.getTelefono2())) {
            throw new ExcepcionPersonalizada("El teléfono 1 no puede ser igual al teléfono 2.");
        }

        // Verificar si el teléfono1 no es nulo y si ya existe en otro empleado
        if (empleado.getTelefono1() != null && !empleado.getTelefono1().trim().isEmpty()) {
            if (empleadoRepository.existsByTelefono1(empleado.getTelefono1()) || empleadoRepository.existsByTelefono2(empleado.getTelefono1())) {
                throw new ExcepcionPersonalizada("Ya existe un empleado con el telefono " + empleado.getTelefono1());
            }
        }

        // Verificar si el teléfono2 no es nulo y si ya existe en otro empleado
        if (empleado.getTelefono2() != null && !empleado.getTelefono2().trim().isEmpty()) {
            if (empleadoRepository.existsByTelefono1(empleado.getTelefono2()) || empleadoRepository.existsByTelefono2(empleado.getTelefono2())) {
                throw new ExcepcionPersonalizada("Ya existe un empleado con el telefono " + empleado.getTelefono2());
            }
        }

        // Verificar si el email ya existe en la base de datos
        if (empleado.getEmail() != null && empleadoRepository.existsByEmail(empleado.getEmail())) {
            throw new ExcepcionPersonalizada("Ya existe un empleado con el email " + empleado.getEmail());
        }

        // Guardar el empleado si todas las validaciones son correctas
        return empleadoRepository.save(empleado);
    }


    @Transactional
    public Empleado updateEmpleado(Long id, Empleado empleadoDetails) {

        // Buscar al empleado en la base de datos
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionPersonalizada("Empleado no encontrado con id: " + id));

        // Validaciones
        verificarExistenciaNif(empleado, empleadoDetails);

        verificarExistenciaEmail(empleado, empleadoDetails);

        verificarExistenciaTel(empleado, empleadoDetails);

        validarFechasActualizar(empleado, empleadoDetails);

        // Actualizar los campos del empleado
        actualizarCamposEmpleado(empleado, empleadoDetails);

        // Asignar proyectos al empleado si sigue activo
        actualizarProyectosEmpleado(empleado, empleadoDetails);

        // Guardar el empleado actualizado
        return empleadoRepository.save(empleado);
    }

    @Transactional
    public Empleado deleteEmpleado(Long id, Empleado empleadoDetails) {
        // Buscar el empleado y lanzar excepción si no existe
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionPersonalizada("Empleado no encontrado con id: " + id));

        // Validar que no tenga proyectos activos antes de darlo de baja
        if (empleado.getProyectos() != null && !empleado.getProyectos().isEmpty()) {
            throw new ExcepcionPersonalizada(
                    "No se puede dar de baja al empleado " + empleado.getNombre() + " "
                            + empleado.getApellido1() + " "
                            + empleado.getApellido2() +
                            " porque está asignado a el/los proyecto/s: "
                            + empleado.getProyectos().stream()
                            .map(Proyecto::getDescripcion)
                            .collect(Collectors.joining(", "))
            );
        }

        actualizarFechaBajaEmpleado(empleado, empleadoDetails);

        // Asignar proyectos al empleado si sigue activo
        actualizarProyectosEmpleado(empleado, empleadoDetails);

        // Guardar los cambios
        return empleadoRepository.save(empleado);
    }

    @Transactional
    public Empleado asignarProyectos(Long idEmpleado, List<Proyecto> proyectos) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new ExcepcionPersonalizada("Empleado no encontrado con id: " + idEmpleado));

        // Asignar nuevos proyectos y sincronizar
        for (Proyecto pro : proyectos) {
            Proyecto proyecto = proyectoRepository.findById(pro.getId())
                    .orElseThrow(() -> new ExcepcionPersonalizada("Proyecto no encontrado con id: " + pro.getId()));

            // Añadir la relación bidireccional
            empleado.getProyectos().add(proyecto);
            proyecto.getEmpleados().add(empleado);
            empleadoRepository.save(empleado);

            EmpleadoProyecto empPro = new EmpleadoProyecto();
            if(emplProRepository.existsByEmpleadoAndProyecto(empleado, pro)){
                empPro.setEmpleado(empleado);
                empPro.setProyecto(pro);
                empPro.setFechaAlta(new Date());
                emplProRepository.save(empPro);
            }
        }

        return empleado;
    }

}
