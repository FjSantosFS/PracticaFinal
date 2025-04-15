package com.example.practicaFinal.entities;

import java.util.Date;

/* Esta es una entidad DTO que hice para pobrar, solo hago una por que no me daba tiempo a hacer todas las entidades DTO necesarias
y con esta ya me sirve de prueba para mandar el objeto mapeado al forntend y asi no mandar todos los campos ni objetos tan grandes
para que sea mas seguro y más óptimo
 */
public record EmpleadoDTO(
        Long id,
        String nif,
        String nombre,
        String apellido1,
        String apellido2,
        Date fechaNacimiento,
        Date fechaAlta,
        String telefono1,
        String telefono2,
        String email,
        String estadoCivil,
        String formacionUniversitaria,
        Date fechaBaja
) {}
