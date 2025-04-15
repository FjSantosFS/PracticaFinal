package com.example.practicaFinal.controllers;

import com.example.practicaFinal.entities.Empleado;
import com.example.practicaFinal.entities.EmpleadoDTO;
import com.example.practicaFinal.entities.Proyecto;
import com.example.practicaFinal.excepciones.ExcepcionPersonalizada;
import com.example.practicaFinal.services.EmpleadoService;
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
    @RequestMapping("/api/empleados")
    public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping()
    public List<EmpleadoDTO> empleadosFiltrados(@RequestParam(required = false) String nombre) {
        return empleadoService.getEmpleadosFiltrados(nombre);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable("id") Long id) {
        Optional<Empleado> empleado = empleadoService.getEmpleadoById(id);
        return empleado.map(ResponseEntity::ok)
        .orElseThrow(() -> new ExcepcionPersonalizada("Empleado no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmpleado(@RequestBody @Valid Empleado empleado) {
        // Intenta crear el empleado
        Empleado nuevoEmpleado = empleadoService.createEmpleado(empleado);

        Map<String, Object> response = new HashMap<>();
        // Añadir mensaje y objeto empleado al response
        response.put("mensaje", "Empleado creado con éxito");
        response.put("empleado", nuevoEmpleado);  // Agrega el objeto Empleado creado
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmpleado(@PathVariable Long id,
                                                              @Valid @RequestBody Empleado empleado) {
        // Buscar al empleado por su ID y actualizar
        Empleado updatedEmpleado = empleadoService.updateEmpleado(id, empleado);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proyecto asignado con éxito");
        response.put("empleado", updatedEmpleado);

        // Retorna el empleado actualizado
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteEmpleado(@PathVariable Long id,
                                                              @Valid @RequestBody Empleado empleado) {

        Empleado deletedEmpleado = empleadoService.deleteEmpleado(id, empleado);

        // Respuesta con código 200 y un mensaje en el cuerpo
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Empleado eliminado con éxito");
        response.put("empleado", deletedEmpleado);  // Retorna el objeto actualizado

        return ResponseEntity.ok((response)); // Código 200 OK
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<Map<String, Object>> asignarProyecto(@PathVariable Long id,
                                                               @RequestBody @Valid List<Proyecto> idProyectos) {
        // Llamada al servicio para asignar el proyecto al empleado
        Empleado updatedEmpleado = empleadoService.asignarProyectos(id, idProyectos);

        // Preparación de la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proyecto asignado con éxito");
        response.put("empleado", updatedEmpleado);  // Retorna el objeto actualizado

        return ResponseEntity.status(HttpStatus.OK).body(response); // Respuesta exitosa con código 200 OK
    }

}
