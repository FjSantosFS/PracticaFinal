package com.example.practicaFinal.services;

import com.example.practicaFinal.entities.Empleado;
import com.example.practicaFinal.entities.EmpleadoProyecto;
import com.example.practicaFinal.entities.Proyecto;
import com.example.practicaFinal.excepciones.ExcepcionPersonalizada;
import com.example.practicaFinal.repositories.EmplProRepository;
import com.example.practicaFinal.repositories.EmpleadoRepository;
import com.example.practicaFinal.repositories.ProyectoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EmplProRepository emplProRepository;

    // Validaciones
    private void validarFechas(Proyecto proyectoDetails) {
        if (proyectoDetails.getFechaFin() != null && proyectoDetails.getFechaInicio() != null) {
            if (proyectoDetails.getFechaFin().before(proyectoDetails.getFechaInicio())) {
                throw new ExcepcionPersonalizada("La fecha de fin no puede ser anterior a la fecha de inicio");
            }
        }
    }

    private void validarDescripcion(Proyecto proyecto){
        if (proyectoRepository.existsByDescripcionIgnoreCase(proyecto.getDescripcion())) {
            throw new ExcepcionPersonalizada("Ya existe un proyecto con esa descripcion: " + proyecto.getDescripcion());
        }
    }

    private void validarDescripcionActualizar(Proyecto proyecto, Proyecto details){
        if (!proyecto.getDescripcion().trim().equalsIgnoreCase(details.getDescripcion().trim()) && proyectoRepository.existsByDescripcionIgnoreCase(details.getDescripcion().trim())) {
            throw new ExcepcionPersonalizada("Ya existe un proyecto con la descripcion: " + details.getDescripcion());
        }
    }

    ////Metodos para actualizaciones
    private void actualizarCamposProyecto(Proyecto proyecto, Proyecto proyectoDetails) {
        proyecto.setDescripcion(proyectoDetails.getDescripcion());
        proyecto.setLugar(proyectoDetails.getLugar());
        proyecto.setFechaInicio(proyectoDetails.getFechaInicio());
        proyecto.setFechaFin(proyectoDetails.getFechaFin());
        proyecto.setFechaBaja(proyectoDetails.getFechaBaja());
        proyecto.setObservaciones(proyectoDetails.getObservaciones());
    }

    private void actualizarBajaProyecto(Proyecto proyecto, Proyecto proyectoDetails) {
        proyecto.setDescripcion(proyectoDetails.getDescripcion());
        proyecto.setLugar(proyectoDetails.getLugar());
        proyecto.setFechaInicio(proyectoDetails.getFechaInicio());
        proyecto.setFechaFin(proyectoDetails.getFechaFin());
        proyecto.setFechaBaja(new Date());
        proyecto.setObservaciones(proyectoDetails.getObservaciones());
    }

    private Set<Empleado> actualizarEmpleadosDelProyecto(Proyecto proyecto, Proyecto proyectoDetails) {
        Set<Empleado> empleadosActualizados = new HashSet<>();

        // Asignar empleados nuevos al proyecto
        if (proyectoDetails.getEmpleados() != null && !proyectoDetails.getEmpleados().isEmpty()) {
            for (Empleado empleado : proyectoDetails.getEmpleados()) {
                Empleado empleadoExistente = empleadoRepository.findById(empleado.getId())
                        .orElseThrow(() -> new ExcepcionPersonalizada("Empleado no encontrado con ID: " + empleado.getId()));

                // Añadir el proyecto al empleado si no está ya asignado
                if (!empleadoExistente.getProyectos().contains(proyecto)) {
                    empleadoExistente.getProyectos().add(proyecto);
                }

                // Añadir el empleado al conjunto de empleados del proyecto
                empleadosActualizados.add(empleadoExistente);
            }
        }

        // Eliminar empleados que ya no están en la lista
        for (Empleado empleado : proyecto.getEmpleados()) {
            if (!empleadosActualizados.contains(empleado)) {
                empleado.getProyectos().remove(proyecto); // Eliminar la relación bidireccional
            }
        }

        return empleadosActualizados;
    }

    ////////////Metodos Transactional y GET
    // Obtener todos los proyectos o los filtados
    public List<Proyecto> getProyectosFiltrados(String descripcion) {
        List<Proyecto> proyectos = descripcion != null && !descripcion.isEmpty()
                ? proyectoRepository.findByDescripcionContainingIgnoreCaseAndFechaBajaIsNull(descripcion)
                : proyectoRepository.findProyectos();

        return proyectos;
    }

    // Obtener un proyecto por ID
    public Optional<Proyecto> getProyectoById(Long id) {
        return proyectoRepository.findById(id);
    }

    @Transactional
    public Proyecto createProyecto(Proyecto proyectoDetails) {

        validarDescripcion(proyectoDetails);
        validarFechas(proyectoDetails);

        proyectoDetails = proyectoRepository.save(proyectoDetails);

        return (proyectoDetails);
    }

    @Transactional
    public Proyecto updateProyecto(Long id, Proyecto proyectoDetails) {

        return proyectoRepository.findById(id)
                .map(proyecto -> {

                    //Validaciones
                    validarDescripcionActualizar(proyecto, proyectoDetails);
                    validarFechas(proyectoDetails);

                    // Actualizar los campos del proyecto
                    actualizarCamposProyecto(proyecto, proyectoDetails);

                    // Gestionar los empleados del proyecto
                    Set<Empleado> empleadosActualizados = actualizarEmpleadosDelProyecto(proyecto, proyectoDetails);

                    // Actualizar la lista de empleados del proyecto
                    proyecto.setEmpleados(empleadosActualizados);

                    // Guardar el proyecto actualizado
                    return proyectoRepository.save(proyecto);
                })
                .orElseThrow(() -> new ExcepcionPersonalizada("Proyecto no encontrado con ID: " + id));
    }
    @Transactional
    public Proyecto asignarEmpleados(Long idProyecto, List<Empleado> empleados) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new ExcepcionPersonalizada("Proyecto no encontrado con id: " + idProyecto));

        if(!proyecto.getEmpleados().isEmpty()){
            // Eliminar el proyecto de los empleados actuales
            for (Empleado empleado : proyecto.getEmpleados()) {
                empleado.getProyectos().remove(proyecto);  // Esto elimina la relación bidireccional
            }
            proyecto.getEmpleados().clear(); // Limpiar la lista de empleados del proyecto
        }

        // Asignar nuevos empleados y sincronizar
        for (Empleado emp : empleados) {
            Empleado empleado = empleadoRepository.findById(emp.getId())
                    .orElseThrow(() -> new ExcepcionPersonalizada("Empleado no encontrado con id: " + emp.getId()));

            // Añadir la relación bidireccional
            proyecto.getEmpleados().add(empleado);
            empleado.getProyectos().add(proyecto);
            proyectoRepository.save(proyecto);

            EmpleadoProyecto empPro = new EmpleadoProyecto();
            if(emplProRepository.existsByEmpleadoAndProyecto(emp, proyecto)){
                empPro.setEmpleado(emp);
                empPro.setProyecto(proyecto);
                empPro.setFechaAlta(new Date());
                emplProRepository.save(empPro);
            }

        }

        return proyecto;
    }

    @Transactional
    public Proyecto deleteProyecto(Long id, Proyecto proyectoDetails) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ExcepcionPersonalizada("Proyecto no encontrado con id: " + id));

        // Validar que no tenga empleados asignados antes de eliminar
        if (proyecto.getEmpleados() != null && !proyecto.getEmpleados().isEmpty()) {
            throw new ExcepcionPersonalizada("No se puede eliminar un proyecto con empleados asignados.");
        }

        actualizarBajaProyecto(proyecto, proyectoDetails);

        // Guardar el proyecto actualizado
        return proyectoRepository.save(proyecto);
    }
}
