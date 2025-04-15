<template>
  <v-app>
    <!-- Menú lateral -->
    <v-navigation-drawer>
      <v-list>
        <v-list-item>
          <img
            src="@/assets/images/Logo sin tipografía blanco.png"
            style="display: block; margin: 0 auto; width: 48px"
          />
        </v-list-item>

        <v-divider></v-divider>

        <v-list-item @click="navigateTo('/')" prepend-icon="mdi-home">
          Inicio
        </v-list-item>

        <v-list-item
          @click="navigateTo('/empleados')"
          prepend-icon="mdi-account"
        >
          Empleados
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- Barra superior -->
    <v-app-bar color="#026fc1">
      <v-toolbar-title align="center">Gestión de Proyectos</v-toolbar-title>
    </v-app-bar>

    <!-- Contenedor principal -->
    <v-main>
      <v-container fluid class="pa-4">
        <!-- Barra de búsqueda -->
        <v-text-field
          v-model="searchQuery"
          label="Buscar por descripción"
          append-icon="mdi-magnify"
          @input="fetchProyectos(searchQuery)"
        ></v-text-field>
        <v-btn class="alta-btn" @click="openAddDialog(item)"
          >ALTA PROYECTO</v-btn
        >
        <!-- Data table -->
        <v-data-table
          :headers="headers"
          :items="proyectos"
          item-key="id"
          class="elevation-8"
        >
          <template v-slot:item="{ item, index }">
            <tr :class="index % 2 === 0 ? 'table-row-even' : 'table-row-odd'">
              <td>{{ item.descripcion }}</td>
              <td>{{ item.fechaInicio }}</td>
              <td>{{ item.fechaFin }}</td>
              <td>{{ item.lugar }}</td>
              <td class="center-buttons">
                <div v-if="item.empleados?.length" class="scroll-container">
                  <div
                    v-for="(empleado, index) in item.empleados.sort((a, b) =>
                      a.nif.localeCompare(b.nif)
                    )"
                    :key="index"
                    class="scroll-item"
                  >
                    {{
                      empleado.nombre +
                      " " +
                      empleado.apellido1 +
                      " " +
                      empleado.apellido2
                    }}
                  </div>
                </div>

                <div v-else class="scroll-container">
                  <span>Sin empleados</span>
                  <v-icon color="warning" class="mr-1">mdi-alert-circle</v-icon>
                </div>
              </td>

              <td class="center-buttons">
                <v-btn
                  icon
                  class="custom-btn"
                  color="grey"
                  @click="openEditDialog(item)"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn
                  icon
                  class="custom-btn"
                  @click="eliminarProyecto(item)"
                  color="red"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
                <v-btn
                  icon
                  class="custom-btn"
                  @click="openAsignarDialog(item)"
                  color="green"
                >
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
              </td>
            </tr>
          </template>
        </v-data-table>

        <!-- Mensaje de carga -->
        <v-progress-circular
          position="abslute"
          v-if="loading"
          indeterminate
          color="primary"
          size="100"
          class="centered-progress my-5"
        ></v-progress-circular>

        <!-- Overlay para bloquear interacción solo si NO es success -->
        <v-overlay
          :model-value="mostrarMensaje && tipoMensaje !== 'success'"
          persistent
          opacity="0.5"
          class="z-50"
        />

        <!-- Snackbar -->
        <v-snackbar
          v-model="mostrarMensaje"
          :color="tipoMensaje"
          :timeout="tipoMensaje === 'success' ? 3000 : -1"
          class="custom-snackbar z-50"
        >
          {{ mensaje }}

          <!-- Solo mostrar el botón si NO es success -->
          <template v-if="tipoMensaje !== 'success'" #actions>
            <v-btn icon @click="mostrarMensaje = false">
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </template>
        </v-snackbar>

        <!-- Modal de Edición -->
        <v-dialog v-model="dialog" max-width="600px">
          <v-card>
            <v-card-title class="text-h6">Editar Proyecto</v-card-title>
            <v-card-text>
              <v-text-field
                v-model="editedProyecto.descripcion"
                label="Descripción"
                required
              />
              <v-text-field
                v-model="editedProyecto.fechaInicio"
                label="F. Inicio"
                type="date"
                required
              />
              <v-text-field
                v-model="editedProyecto.fechaFin"
                label="F. Fin"
                type="date"
                required
              />
              <v-text-field
                v-model="editedProyecto.lugar"
                label="Lugar"
                required
              />
              <v-text-field
                v-model="editedProyecto.observaciones"
                label="Observaciones"
                required
              />
            </v-card-text>
            <v-card-actions>
              <v-btn @click="closeEditDialog" class="cancelar-btn"
                >Cancelar</v-btn
              >
              <v-btn @click="updateProyecto" class="aceptar-btn">Aceptar</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- Modal de Asignación -->
        <v-dialog v-model="dialogEmpleado" max-width="600px">
          <v-card>
            <v-card-title class="text-h6">Asignar Empleado</v-card-title>
            <v-card-text>
              <v-text-field
                v-model="editedEmpleado.descripcion"
                label="Descripción del proyecto"
                required
                readonly
              />
              <v-select
                v-model="editedEmpleado.empleados"
                label="Asignar empleados"
                :items="empleados"
                :item-title="
                  (empleado) =>
                    `${empleado.nif} ${empleado.nombre} ${empleado.apellido1} ${empleado.apellido2}`
                "
                item-value="id"
                multiple
                chips
                required
              />
            </v-card-text>
            <v-card-actions>
              <v-btn @click="closeEmpleadoDialog" class="cancelar-btn"
                >Cancelar</v-btn
              >
              <v-btn @click="asignarEmpleado" class="aceptar-btn"
                >Aceptar</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- Modal de Creacion -->
        <v-dialog v-model="dialogNew" max-width="600px">
          <v-card>
            <v-card-title class="text-h6">Crear Proyecto</v-card-title>
            <v-card-text>
              <v-text-field
                v-model="newProyecto.descripcion"
                label="Descripción"
                required
              />
              <v-text-field
                v-model="newProyecto.fechaInicio"
                label="F. Inicio"
                type="date"
                required
              />
              <v-text-field
                v-model="newProyecto.fechaFin"
                label="F. Fin"
                type="date"
                required
              />
              <v-text-field
                v-model="newProyecto.lugar"
                label="Lugar"
                required
              />
              <v-text-field
                v-model="newProyecto.observaciones"
                label="Observaciones"
                required
              />
            </v-card-text>
            <v-card-actions>
              <v-btn @click="closeAddDialog" class="cancelar-btn"
                >Cancelar</v-btn
              >
              <v-btn @click="crearProyecto" class="aceptar-btn">Aceptar</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useProyectoStore, useEmpleadoStore } from "../stores/store";
import { useRouter } from "vue-router";

export default {
  setup() {
    ///Estados///
    const router = useRouter();
    const dialog = ref(false);
    const dialogEmpleado = ref(false);
    const dialogNew = ref(false);
    const searchQuery = ref("");
    const mensaje = ref("");
    const tipoMensaje = ref("success");
    const mostrarMensaje = ref(false);

    ///Store///
    const proyectoStore = useProyectoStore();
    const empleadoStore = useEmpleadoStore();
    const empleados = computed(() => empleadoStore.empleados);
    const proyectos = computed(() => proyectoStore.proyectos);
    const loading = computed(() => proyectoStore.loading);
    const error = computed(() => proyectoStore.error);
    const fetchProyectos = proyectoStore.fetchProyectos;

    ///Menu lateral///
    const menuItems = ref([
      { title: "Inicio", icon: "mdi-home", path: "/" },
      {
        title: "Empleados",
        icon: "mdi-account-group",
        path: "/empleados",
      },
    ]);

    ///Headers datatable///
    const headers = [
      {
        title: "DESCRIPCION",
        value: "descripcion",
        sortable: true,
        align: "start",
      },
      {
        title: "F_INICIO",
        value: "fechaInicio",
        sortable: true,
        align: "start",
      },
      {
        title: "F_FIN",
        value: "fechaFin",
        sortable: true,
        align: "start",
      },
      {
        title: "LUGAR",
        value: "lugar",
        sortable: true,
        align: "start",
      },
      {
        title: "EMPLEADOS ASIGNADOS",
        value: "empleados",
        sortable: true,
        align: "start",
      },
      {
        value: "acciones",
        sortable: false,
        align: "start",
      },
    ];

    ///Funcion cargar los datos cuando se monta el componente///
    onMounted(() => {
      proyectoStore.fetchProyectos();
      empleadoStore.fetchEmpleados();
    });

    ///Datos para los formularios///
    const editedProyecto = ref({
      descripcion: "",
      fechaInicio: "",
      fechaFin: "",
      fechaBaja: "",
      lugar: "",
      observaciones: "",
    });
    const newProyecto = ref({
      descripcion: "",
      fechaInicio: "",
      fechaFin: "",
      fechaBaja: "",
      lugar: "",
      observaciones: "",
      empleados: [],
    });
    const resetNewProyecto = () => {
      newProyecto.value = {
        descripcion: "",
        fechaInicio: "",
        fechaFin: "",
        fechaBaja: "",
        lugar: "",
        observaciones: "",
        empleados: [],
      };
    };
    const editedEmpleado = ref({
      descripcion: "",
      id: "",
      empleados: [],
    });

    ///Abrir dialogs///
    const openEditDialog = (item) => {
      console.log("Proyecto seleccionado para editar:", item);
      editedProyecto.value = { ...item }; // Cargar los datos del empleado
      dialog.value = true; // Abrir el modal
    };
    const openAsignarDialog = (item) => {
      editedEmpleado.value = { ...item }; // Cargar los datos del empleado
      dialogEmpleado.value = true; // Abrir el modal
      console.log("Añadir empleado al proyecto", editedEmpleado.value.id);
    };
    const openAddDialog = () => {
      dialogNew.value = true; // Abrir el modal
    };
    const closeEditDialog = () => {
      dialog.value = false;
    };

    ///Cerrar dialogs///
    const closeEmpleadoDialog = () => {
      dialogEmpleado.value = false;
    };
    const closeAddDialog = () => {
      dialogNew.value = false;
    };

    ///Funciones CRUD///
    //Crear
    const crearProyecto = async () => {
      try {
        await proyectoStore.addProyecto(newProyecto.value);
        await proyectoStore.fetchProyectos();

        // Mostrar mensaje de éxito
        mensaje.value = "Creado con éxito";
        tipoMensaje.value = "success";
        mostrarMensaje.value = true;
        closeAddDialog();
        resetNewProyecto();
      } catch (error) {
        if (error.response) {
          // Crear una lista de los campos posibles para errores
          const camposDeError = [
            "descripcion",
            "fechaInicio",
            "fechaFin",
            "lugar",
            "observaciones", // Mensaje de error general
            "error",
          ];

          // Recorremos la lista de campos para buscar el primer campo de error
          for (const campo of camposDeError) {
            if (error.response.data && error.response.data[campo]) {
              mensaje.value = error.response.data[campo]; // Mensaje de error específico
              break; // Salimos del bucle después de encontrar el primer error
            }
          }
        }
        tipoMensaje.value = "error"; // Establecer el tipo de mensaje como error
        mostrarMensaje.value = true; // Mostrar el mensaje en el snackbar
      }
    };

    //Update
    const updateProyecto = async () => {
      try {
        if (!editedProyecto.value.id) {
          console.error("El ID del pryecto no está presente.");
          return;
        }
        // Capturar mensaje de la API
        await proyectoStore.updateProyecto(editedProyecto.value);
        await proyectoStore.fetchProyectos();
        // Mostrar mensaje de éxito
        mensaje.value = "Actualizado con éxito";
        tipoMensaje.value = "success";
        mostrarMensaje.value = true;
        closeEditDialog();
      } catch (error) {
        if (error.response) {
          // Crear una lista de los campos posibles para errores
          const camposDeError = [
            "descripcion",
            "fechaInicio",
            "fechaFin",
            "lugar",
            "observaciones",
            "error", // Mensaje de error general
          ];

          // Recorremos la lista de campos para buscar el primer campo de error
          for (const campo of camposDeError) {
            if (error.response.data && error.response.data[campo]) {
              mensaje.value = error.response.data[campo]; // Mensaje de error específico
              break; // Salimos del bucle después de encontrar el primer error
            }
          }
        }
        tipoMensaje.value = "error"; // Establecer el tipo de mensaje como error
        mostrarMensaje.value = true; // Mostrar el mensaje en el snackbar
      }
    };

    //Update-Delete
    const eliminarProyecto = async (item) => {
      // Confirmar con el proyecto antes de eliminar
      const confirmDelete = confirm(
        `¿Seguro que quieres eliminar al proyecto ${item.descripcion} con ID ${item.id}?`
      );

      if (confirmDelete) {
        try {
          console.log(item);

          // Llamar al método de la store para eliminar el empleado
          await proyectoStore.eliminarProyecto(item);
          await proyectoStore.fetchProyectos();

          // Mostrar mensaje de éxito
          mensaje.value = "Eliminado con éxito";
          tipoMensaje.value = "success";
          mostrarMensaje.value = true;
        } catch (error) {
          if (error.response) {
            // Si existe una respuesta de error (respuesta HTTP)
            if (error.response.data && error.response.data.error) {
              mensaje.value = error.response.data.error; // Mensaje de error específico
            }
          }
          tipoMensaje.value = "error"; // Establecer el tipo de mensaje como error
          mostrarMensaje.value = true; // Mostrar el mensaje en el snackbar
        }
      }
    };

    //Update-asignar
    const asignarEmpleado = async () => {
      try {
        const empleadosSeleccionados = editedEmpleado.value.empleados
          .map((id) => {
            return empleados.value.find((empleado) => empleado.id === id);
          })
          .filter(Boolean);

        await proyectoStore.asignarEmpleado(
          editedEmpleado.value.id, // ID del empleado
          empleadosSeleccionados // Los proyectos completos que estamos asignando
        );

        await proyectoStore.fetchProyectos();

        mensaje.value = "Asignaciones modificadas";
        tipoMensaje.value = "success";
        mostrarMensaje.value = true;

        // Cerrar el diálogo de selección de proyectos
        closeEmpleadoDialog();
      } catch (error) {
        console.error("Error al asignar el proyecto:", error.message);
        mensaje.value = error.message; // Mensaje de error para el usuario
        tipoMensaje.value = "error";
        mostrarMensaje.value = true;
      }
    };

    // Función para la navegación
    const navigateTo = (path) => {
      router.push(path);
    };

    return {
      fetchProyectos,
      searchQuery,
      empleadoStore,
      menuItems,
      headers,
      empleados,
      proyectos,
      loading,
      error,
      dialog,
      dialogEmpleado,
      editedEmpleado,
      openEditDialog,
      closeEditDialog,
      closeEmpleadoDialog,
      updateProyecto,
      eliminarProyecto,
      mensaje,
      tipoMensaje,
      mostrarMensaje,
      editedProyecto,
      asignarEmpleado,
      openAsignarDialog,
      navigateTo,
      router,
      dialogNew,
      openAddDialog,
      closeAddDialog,
      newProyecto,
      crearProyecto,
      resetNewProyecto,
    };
  },
};
</script>

<style scoped>
html,
body,
#app {
  height: 100%;
  margin: 0;
  padding: 0;
}

.v-container {
  align-content: space-between;
  height: 100%;
  background-color: #001021;
}

.v-navigation-drawer {
  background-color: #026fc1;
}
.v-data-table {
  display: block; /* Fuerza que se comporte como bloque scrolleable */
  overflow-x: auto; /* Activa el scroll horizontal */
  white-space: nowrap; /* Evita que los textos se rompan en varias líneas */
  background-color: #022a51;
  color: white;
  border-radius: 5px;
}

.scroll-container {
  max-height: 50px; /* Altura máxima del contenedor para que el contenido haga scroll */
  overflow-y: auto; /* Permite el scroll vertical si el contenido es más grande que el contenedor */
  padding: 8px;
}
.scroll-item {
  margin-bottom: 8px; /* Espaciado entre los proyectos */
}

.center-buttons {
  text-align: start;
}

.center-buttons .custom-btn {
  width: auto;
  height: auto;
  margin: 5px;
  border-radius: 4px;
}

.v-alert {
  margin-top: 20px;
}

.alta-btn {
  margin-bottom: 10px;
  background-color: #026fc1;
}

.alta-btn:hover {
  background-color: #028cf5;
}
.aceptar-btn {
  margin-bottom: 10px;
  background-color: #026fc1;
}

.aceptar-btn:hover {
  background-color: #028cf5;
  color: green;
}
.cancelar-btn {
  margin-bottom: 10px;
  background-color: #026fc1;
}

.cancelar-btn:hover {
  background-color: #028cf5;
  color: red;
}

/* Estilo para cada item del scroll */
.scroll-item {
  margin-bottom: 8px; /* Espaciado entre los proyectos */
}
.table-row-even {
  background-color: #094c8f; /* Color de fondo */
}

/* Clase para las filas con índices impares */
.table-row-odd {
  background-color: #0c67c2; /* Color de fondo */
}

.v-dialog .v-card {
  background-color: #001021;
  color: white;
}

.v-navigation-drawer .v-list-item-title {
  font-size: 2rem;
}

.v-navigation-drawer .v-list-item {
  font-size: 20px;
}

.v-toolbar-title {
  font-size: 2rem;
}

.v-list-item {
  color: white;
  font-weight: bold;
}
.custom-snackbar {
  position: absolute !important; /* Estático en la pantalla */
  top: 72% !important;
  left: 40% !important;
  bottom: 28% !important;
  right: 60% !important;
  text-align: center; /* Centra el texto dentro del snackbar */
}

.centered-progress {
  position: absolute !important; /* Estático en la pantalla */
  top: 50% !important;
  left: 50% !important;
  bottom: 50% !important;
  right: 50% !important;
}
</style>
