package com.example.practicaFinal.controllers;

import com.example.practicaFinal.entities.Empleado;
import com.example.practicaFinal.entities.Proyecto;
import com.example.practicaFinal.excepciones.ExcepcionPersonalizada;
import com.example.practicaFinal.services.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    // Obtener los proyectos filtrados
    @GetMapping
    public List<Proyecto> proyectosFiltrados(@RequestParam(required = false) String descripcion) {
        return proyectoService.getProyectosFiltrados(descripcion);
    }

    // Obtener un proyecto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> getProyectoById(@PathVariable Long id) {
        Optional<Proyecto> proyecto = proyectoService.getProyectoById(id);
        return proyecto.map(ResponseEntity::ok)
                .orElseThrow(() -> new ExcepcionPersonalizada("Proyecto con id: " + id + " no encontrado"));
    }

    // Crear un nuevo proyecto con validación
    @PostMapping
    public ResponseEntity<Map<String, Object>>createProyecto(@Valid @RequestBody Proyecto proyecto) {
        // Crea el proyecto
        Proyecto proyectoCreado = proyectoService.createProyecto(proyecto);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proyecto creado con éxito");
        response.put("proyecto", proyectoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Actualizar un proyecto existente con validación
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProyecto(@PathVariable Long id, @RequestBody @Valid Proyecto proyectoDetails) {
        Proyecto updatedProyecto = proyectoService.updateProyecto(id, proyectoDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proyecto actualizado con éxito");
        response.put("proyecto", updatedProyecto);

        return ResponseEntity.ok(response);
    }

    // Eliminar un proyecto
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteProyecto(@PathVariable Long id,
                                                              @Valid @RequestBody Proyecto proyecto) {
        Proyecto deletedProyecto =  proyectoService.deleteProyecto(id, proyecto);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proyecto eliminado con éxito");
        response.put("proyecto", deletedProyecto);  // Retorna el objeto actualizado

        return ResponseEntity.ok(response); // Código 200 OK
    }

    //Asignar empleados a proyectos
    @PutMapping("/{id}/asignar")
    public ResponseEntity<Map<String, Object>> asignarEmpleado(@PathVariable Long id,
                                                               @Valid @RequestBody List<Empleado> empleados) {
        // Llamada al servicio para asignar el proyecto a los empleados
        Proyecto updatedProyecto = proyectoService.asignarEmpleados(id, empleados);

        // Preparación de la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Empleado asignado con éxito");
        response.put("proyecto", updatedProyecto);  // Devuelves el proyecto con los empleados asignados

        return ResponseEntity.status(HttpStatus.OK).body(response); // Respuesta exitosa con código 200 OK
    }
}
