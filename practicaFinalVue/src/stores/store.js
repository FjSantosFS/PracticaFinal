
import { defineStore } from 'pinia';
import axios from 'axios';

const API_EMPLEADOS = 'http://localhost:8080/api/empleados';
const API_PROYECTOS = 'http://localhost:8080/api/proyectos';

//store empleados
export const useEmpleadoStore = defineStore('empleados', {
  state: () => ({
    empleadosDTO: [],
    empleados:[],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchEmpleados(searchQuery = '') {
      this.loading = true;
      try {
        const response = await axios.get(`${API_EMPLEADOS}?nombre=${searchQuery}`);
        this.empleadosDTO = response.data;
        this.empleados = response.data;
      } catch (error) {
        this.error = "Error al obtener los empleados";
      } finally {
        this.loading = false;
      }
    },

    async getEmpleado(id) {
      this.loading = true;
      try {
        const response = await axios.get(`${API_EMPLEADOS}/${id}`);
        return response.data;
      } catch (error) {
        this.error = "Error al obtener el empleado";
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // Método para agregar un nuevo empleado
    async addEmpleado(newEmpleado) {
      try {
        this.loading = true;
        this.error = null;

        // Realizar la solicitud POST a la API para agregar el nuevo empleado
        const response = await axios.post(API_EMPLEADOS, newEmpleado);

        // Si la respuesta es exitosa, añadir el nuevo empleado a la lista local
        this.empleados.push(response.data);

        this.loading = false;
        return response.data;  // Devuelve el empleado agregado si lo necesitas
      } catch (error) {
        this.loading = false;
        console.error(error);
        throw error;  // Lanza el error si quieres manejarlo de forma global
      }
    },
    
    //Actualizar un empleado
    async updateEmpleado(empleado) {  
      this.loading = true;
      try {
        const response = await axios.put(`${API_EMPLEADOS}/${empleado.id}`, empleado);

        console.log("Respuesta de la API:", response.data);

        // Actualizar la lista de empleados en la store
        const index = this.empleados.findIndex((e) => e.id === empleado.id);
        if (index !== -1) {
          this.empleados[index] = { ...response.data };
        } else {
          console.warn("Empleado no encontrado en la store.");
        }

        this.loading = false;
        return response.data;

        
      } catch (error) {
        this.loading = false;
        throw error;
      }
    },

    //Eliminar empleado(actualizar)
    async eliminarEmpleado(empleadoDel) {
      this.loading = true;
      try {
        const response = await axios.put(`${API_EMPLEADOS}/${empleadoDel.id}/delete`, empleadoDel);
    
        console.log("Respuesta de la API:", response.data);
    
        // Actualizar la lista de empleados en la store
        const index = this.empleados.findIndex((e) => e.id === empleadoDel.id);
        if (index !== -1) {
          this.empleados[index] = { ...response.data };
        } 
        return response.data;
      } catch (error) {
        console.error("Error al eliminar:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    //Asgnar proyectos a empleados(actualizar)
    async asignarProyecto(idEmp, listaProyectos) {
      this.loading = true;
      try {
        // Realizas la solicitud PUT solo con los IDs correspondientes
        const response = await axios.put(`${API_EMPLEADOS}/${idEmp}/asignar`, listaProyectos);
    
        console.log("Respuesta de la API:", response.data);
    
        // Actualizas el empleado en la store con los datos que devuelve la API
        const index = this.empleados.findIndex((e) => e.id === idEmp);
        if (index !== -1) {
          this.empleados[index] = response.data;  // Actualiza el empleado con los datos devueltos por la API
        } 
    
        this.loading = false;
        return response.data;  // Retorna la respuesta con el empleado actualizado
    
      } catch (error) {
        this.loading = false;
        console.error("Error al asignar el proyecto:", error);
        throw error;
      }
    },
  }
});

//store proyectos
export const useProyectoStore = defineStore('proyectos', {
  state: () => ({
    proyectos: [],
    loading: false,
    error: null,
  }),

  actions: {

    //GET proyectos
    async fetchProyectos(searchQuery = '') {
      this.loading = true;
      try {
        const response = await axios.get(`${API_PROYECTOS}?descripcion=${searchQuery}`);
        this.proyectos = response.data;
      } catch (error) {
        this.error = "Error al obtener los proyectos";
      } finally {
        this.loading = false;
      }
    },
    
    // Método para agregar un nuevo proyecto
    async addProyecto(newProyecto) {
      try {
        this.loading = true;
        this.error = null;

        // Realizar la solicitud POST a la API para agregar el nuevo empleado
        const response = await axios.post(API_PROYECTOS, newProyecto);

        // Si la respuesta es exitosa, añadir el nuevo empleado a la lista local
        this.proyectos.push(response.data);

        this.loading = false;
        return response.data;  // Devuelve el empleado agregado si lo necesitas
      } catch (error) {
        this.loading = false;
        console.error(error);
        throw error;  // Lanza el error si quieres manejarlo de forma global
      }
    },

    //Actualizar un proyecto
    async updateProyecto(proyecto) { 
      this.loading = true;
      try {
        const response = await axios.put(`${API_PROYECTOS}/${proyecto.id}`, proyecto);

        console.log("Respuesta de la API:", response.data);

        // Actualizar la lista de proyectos en la store
        const index = this.proyectos.findIndex((e) => e.id === proyecto.id);
        if (index !== -1) {
          this.proyectos[index] = { ...response.data };
        } else {
          console.warn("Proyecto no encontrado en la store.");
        }

        this.loading = false;
        return response.data;

        
      } catch (error) {
        this.loading = false;
        throw error;
      }
    },

    //Eliminar proyecto(update)
    async eliminarProyecto(proyecto) {
      this.loading = true;
      try {
    
        const response = await axios.put(`${API_PROYECTOS}/${proyecto.id}/delete`, proyecto);
        console.log("Proyecto eliminado:", response.data);
    
        // Actualizar la lista de proyectos en la store
        const index = this.proyectos.findIndex((e) => e.id === proyecto.id);
        if (index !== -1) {
          this.proyectos[index] = { ...response.data };
        } else {
          console.warn("Proyecto no encontrado en la store.");
        }

        this.loading = false;
        return response.data;

        
      } catch (error) {
        this.loading = false;
        throw error;
      }
    },

    //Asignar empleados a proyectos (actualizar)
    async asignarEmpleado(idPro, listaEmpl) {
      this.loading = true;
      try {
        const response = await axios.put(`${API_PROYECTOS}/${idPro}/asignar`, listaEmpl);

        console.log("Respuesta de la API:", response.data);

        // Actualizar la lista de proyectos en la store
        const index = this.proyectos.findIndex((e) => e.id === idPro);
        if (index !== -1) {
          this.proyectos[index] = { ...response.data };
        } else {
          console.warn("Proyecto no encontrado en la store.");
        }

        this.loading = false;
        return response.data;

        
      } catch (error) {
        this.loading = false;
        throw error;
      }
    },

  }
});

