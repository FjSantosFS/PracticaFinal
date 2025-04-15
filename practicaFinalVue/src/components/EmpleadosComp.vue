<template>
  <v-app>
    <!-- Menú lateral -->
    <v-navigation-drawer>
      <v-list>
        <v-list-item>
          <img
            src="@/assets/images/Logo_blanco_Future_Space_sin_fondo.png"
            style="width: 170px; height: 38px"
          />
        </v-list-item>

        <v-divider></v-divider>

        <v-list-item @click="navigateTo('/')" prepend-icon="mdi-home">
          Inicio
        </v-list-item>

        <v-list-item
          @click="navigateTo('/proyectos')"
          prepend-icon="mdi-folder"
        >
          Proyectos
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- Barra superior -->
    <v-app-bar color="#026fc1">
      <v-toolbar-title align="center">Gestión de Empleados</v-toolbar-title>
    </v-app-bar>

    <!-- Contenido principal -->
    <v-main>
      <v-container fluid class="pa-4">
        <!-- Barra de búsqueda -->
        <v-text-field
          v-model="searchQuery"
          label="Buscar por nombre"
          append-icon="mdi-magnify"
          @input="fetchEmpleados(searchQuery)"
          style="max-width: 400px"
        ></v-text-field>
        <v-btn class="alta-btn" @click="openAddDialog(item)"
          >ALTA EMPLEADO</v-btn
        >
        <v-data-table
          :headers="headers"
          :items="empleadosDTO"
          item-key="id"
          class="elevation-8"
        >
          <template v-slot:item="{ item, index }">
            <tr :class="index % 2 === 0 ? 'table-row-even' : 'table-row-odd'">
              <td>{{ item.nif }}</td>
              <td>{{ item.nombre }}</td>
              <td>{{ item.apellido1 }}</td>
              <td>{{ item.apellido2 }}</td>
              <td>{{ item.fechaNacimiento }}</td>
              <td>{{ item.telefono1 }}</td>
              <td>{{ item.telefono2 || "Sin telefono" }}</td>
              <td>{{ item.email }}</td>

              <td class="center-buttons">{{ item.estadoCivil }}</td>
              <td class="center-buttons">{{ item.formacionUniversitaria }}</td>

              <td class="center-buttons">
                <v-btn
                  icon
                  color="grey"
                  class="custom-btn"
                  @click="openEditDialog(item)"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn
                  icon
                  color="red"
                  class="custom-btn"
                  @click="eliminarEmpleado(item)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
                <v-btn
                  icon
                  color="green"
                  class="custom-btn"
                  @click="openAsignarDialog(item)"
                >
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
              </td>
            </tr>
          </template>
        </v-data-table>

        <!-- Imagen de carga -->
        <v-progress-circular
          position="abslute"
          v-if="loading"
          indeterminate
          color="primary"
          size="100"
          class="centered-progress my-5"
        ></v-progress-circular>

        <!-- Overlay para hacer modal un snackbar con mensaje de error -->
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

        <!-- Modal Crear Empleado -->
        <v-dialog v-model="dialogAdd" max-width="600px" persistent>
          <v-card>
            <v-card-title class="text-h6">Crear Empleado</v-card-title>
            <v-card-text>
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.nif"
                    label="DNI"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.nombre"
                    label="Nombre"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.apellido1"
                    label="Primer Apellido"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.apellido2"
                    label="Segundo Apellido"
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.telefono1"
                    label="Teléfono 1"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.telefono2"
                    label="Teléfono 2"
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.fechaNacimiento"
                    label="Fecha de Nacimiento"
                    type="date"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.fechaAlta"
                    label="Fecha de Alta"
                    type="date"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="newEmpleado.email"
                    label="Correo Electrónico"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-select
                    v-model="newEmpleado.estadoCivil"
                    label="Estado Civil"
                    :items="['S', 'C']"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-radio-group
                    v-model="newEmpleado.formacionUniversitaria"
                    label="Formación Universitaria"
                    row
                  >
                    <v-radio label="Sí" value="Si" />
                    <v-radio label="No" value="No" />
                  </v-radio-group>
                </v-col>
              </v-row>
            </v-card-text>
            <v-card-actions>
              <v-spacer />
              <v-btn class="cancelar-btn" @click="closeNewDialog"
                >Cancelar</v-btn
              >
              <v-btn class="aceptar-btn" @click="agregarEmpleado"
                >Aceptar</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- Modal Editar Empleado -->
        <v-dialog v-model="dialog" max-width="600px" persistent>
          <v-card>
            <v-card-title class="text-h6">Editar Empleado</v-card-title>
            <v-card-text>
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.nif"
                    label="DNI"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.nombre"
                    label="Nombre"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.apellido1"
                    label="Primer Apellido"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.apellido2"
                    label="Segundo Apellido"
                  />
                </v-col>

                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.telefono1"
                    label="Teléfono 1"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.telefono2"
                    label="Teléfono 2"
                  />
                </v-col>

                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.fechaNacimiento"
                    label="Fecha de Nacimiento"
                    type="date"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.fechaAlta"
                    label="Fecha de Alta"
                    type="date"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedEmpleado.email"
                    label="Correo Electrónico"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-select
                    v-model="editedEmpleado.estadoCivil"
                    label="Estado Civil"
                    :items="['S', 'C']"
                    required
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-radio-group
                    v-model="editedEmpleado.formacionUniversitaria"
                    label="Formación Universitaria"
                    row
                  >
                    <v-radio label="Sí" value="Si" />
                    <v-radio label="No" value="No" />
                  </v-radio-group>
                </v-col>
              </v-row>
            </v-card-text>
            <v-card-actions>
              <v-spacer />
              <v-btn class="cancelar-btn" @click="closeEditDialog"
                >Cancelar</v-btn
              >
              <v-btn class="aceptar-btn" @click="updateEmpleado">Aceptar</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- Modal Asignar Proyecto -->
        <v-dialog v-model="dialogProyecto" max-width="500px" persistent>
          <v-card>
            <v-card-title class="text-h6">Asignar Proyecto</v-card-title>
            <v-card-text>
              <v-text-field
                v-model="editedProyectos.nombre"
                label="Nombre"
                required
                outlined
                readonly
              />
              <v-text-field
                v-model="editedProyectos.nif"
                label="DNI"
                required
                outlined
                readonly
              />
              <v-select
                v-model="editedProyectos.proyectos"
                label="Proyectos"
                :items="proyectos"
                item-title="descripcion"
                item-value="id"
                multiple
                chips
                outlined
                required
              />
            </v-card-text>
            <v-card-actions>
              <v-spacer />
              <v-btn class="cancelar-btn" @click="closeProyectosDialog"
                >Cancelar</v-btn
              >
              <v-btn class="aceptar-btn" @click="asignarProyecto"
                >Aceptar</v-btn
              >
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useEmpleadoStore, useProyectoStore } from "../stores/store"; // Asegúrate de tener la store configurada
import { useRouter } from "vue-router";

export default {
  setup() {
    ///Estados///
    const router = useRouter();
    const dialog = ref(false);
    const dialogAdd = ref(false);
    const dialogProyecto = ref(false);
    const searchQuery = ref("");
    const mensaje = ref("");
    const tipoMensaje = ref("success");
    const mostrarMensaje = ref(false);

    ///Store///
    const empleadoStore = useEmpleadoStore();
    const proyectoStore = useProyectoStore();
    const proyectos = computed(() => proyectoStore.proyectos);
    const empleados = computed(() => empleadoStore.empleados);
    const empleadosDTO = computed(() => empleadoStore.empleadosDTO);
    const loading = computed(() => empleadoStore.loading);
    const error = computed(() => empleadoStore.error);
    const fetchEmpleados = empleadoStore.fetchEmpleados;

    ///Menu lateral///
    const menuItems = ref([
      { title: "Inicio", icon: "mdi-home", path: "/" },
      {
        title: "Proyectos",
        icon: "mdi-briefcase",
        path: "/proyectos",
      },
    ]);

    ///Headers del datatable///
    const headers = [
      {
        title: "DNI",
        value: "nif",
        sortable: true,
        align: "start",
      },
      {
        title: "NOMBRE",
        value: "nombre",
        sortable: true,
        align: "start",
      },
      {
        title: "PRIMER APELLIDO",
        value: "apellido1",
        sortable: true,
        align: "start",
      },
      {
        title: "SEGUNDO APELLIDO",
        value: "apellido2",
        sortable: true,
        align: "start",
      },
      {
        title: "F_NACIMINETO",
        value: "fechaNacimiento",
        sortable: true,
        align: "start",
      },
      {
        title: "TELEFONO",
        value: "telefono1",
        sortable: true,
        align: "start",
      },
      {
        title: "TELEFONO_2",
        value: "telefono2",
        sortable: true,
        align: "start",
      },
      { title: "EMAIL", value: "email", sortable: true, align: "start" },

      {
        title: "ESTADO CIVIL",
        value: "estadoCivil",
        sortable: true,
        align: "center",
      },
      {
        title: "ESTUDIOS",
        value: "formacionUniversitaria",
        sortable: true,
        align: "center",
      },
      {
        value: "acciones",
        sortable: false,
        align: "center",
      },
    ];

    ///Funcion cargar los datos cuando se monta el componente///
    onMounted(() => {
      empleadoStore.fetchEmpleados();
      proyectoStore.fetchProyectos();
    });

    ///Datos para los formularios///
    const editedEmpleado = ref({
      nif: "",
      nombre: "",
      apellido1: "",
      apellido2: "",
      fechaNacimiento: "",
      telefono1: "",
      telefono2: null,
      email: "",
      fechaAlta: "",
      fechaBaja: null,
      estadoCivil: "",
      formacionUniversitaria: "",
    });
    const newEmpleado = ref({
      nif: "",
      nombre: "",
      apellido1: "",
      apellido2: "",
      fechaNacimiento: "",
      telefono1: "",
      telefono2: null,
      email: "",
      fechaAlta: "",
      fechaBaja: null,
      estadoCivil: "",
      formacionUniversitaria: "",
      proyectos: [],
    });
    const resetNewEmpleado = () => {
      newEmpleado.value = {
        nif: "",
        nombre: "",
        apellido1: "",
        apellido2: "",
        fechaNacimiento: "",
        telefono1: "",
        telefono2: null,
        email: "",
        fechaAlta: "",
        fechaBaja: null,
        estadoCivil: "",
        formacionUniversitaria: "",
        proyectos: [],
      };
    };
    const editedProyectos = ref({
      nif: "",
      nombre: "",
      proyectos: [],
    });
    const editedDesasignar = ref({
      nif: "",
      nombre: "",
      proyectoEmp: null,
    });

    //Abrir dialogs
    const openEditDialog = (item) => {
      console.log("Empleado seleccionado para editar:", item);
      editedEmpleado.value = { ...item }; // Cargar los datos del empleado
      dialog.value = true; // Abrir el modal
    };
    const openAddDialog = () => {
      dialogAdd.value = true; // Abrir el modal
    };
    const openAsignarDialog = (item) => {
      editedProyectos.value = { ...item }; // Cargar los datos del empleado
      dialogProyecto.value = true; // Abrir el modal
    };

    //Cerrar dialogs
    const closeEditDialog = () => {
      dialog.value = false;
    };
    const closeNewDialog = () => {
      dialogAdd.value = false;
    };
    const closeProyectosDialog = () => {
      dialogProyecto.value = false;
    };

    //////Funciones CRUD//////
    //Crear//
    const agregarEmpleado = async () => {
      try {
        console.log(newEmpleado.value);
        await empleadoStore.addEmpleado(newEmpleado.value);
        await empleadoStore.fetchEmpleados();
        // Mostrar mensaje de éxito
        mensaje.value = "Creado con éxito";
        tipoMensaje.value = "success";
        mostrarMensaje.value = true;
        closeNewDialog();
        resetNewEmpleado();
      } catch (error) {
        if (error.response) {
          // Crear una lista de los campos posibles para errores
          const camposDeError = [
            "nif",
            "nombre",
            "apellido1",
            "apellido2",
            "telefono1",
            "telefono2",
            "fechaAlta",
            "fechaNacimiento",
            "email",
            "formacionUniversitaria",
            "estadoCivil",
            "mensaje",
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

    //Update//
    const updateEmpleado = async () => {
      try {
        await empleadoStore.updateEmpleado(editedEmpleado.value);
        await empleadoStore.fetchEmpleados();

        // Mostrar mensaje de éxito
        mensaje.value = "Actualizado con éxito";
        tipoMensaje.value = "success";
        mostrarMensaje.value = true;
        closeEditDialog();
      } catch (error) {
        if (error.response) {
          // Crear una lista de los campos posibles para errores
          const camposDeError = [
            "nif",
            "nombre",
            "apellido1",
            "apellido2",
            "telefono1",
            "telefono2",
            "fechaAlta",
            "fechaNacimiento",
            "email",
            "formacionUniversitaria",
            "estadoCivil",
            "mensaje", // Puedes añadir otros campos si los tienes
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

    //Update-Asignar//
    const asignarProyecto = async () => {
      try {
        const proyectosSeleccionados = editedProyectos.value.proyectos
          .map((id) => {
            return proyectos.value.find((proyecto) => proyecto.id === id);
          })
          .filter(Boolean);

        await empleadoStore.asignarProyecto(
          editedProyectos.value.id, // ID del empleado
          proyectosSeleccionados // Los proyectos completos que estamos asignando
        );

        await empleadoStore.fetchEmpleados();

        mensaje.value =
          "Asignad@ con éxito: " +
          editedProyectos.value.nombre +
          " " +
          editedProyectos.value.apellido1 +
          " " +
          editedProyectos.value.apellido2;
        tipoMensaje.value = "success";
        mostrarMensaje.value = true;

        closeProyectosDialog();
      } catch (error) {
        console.error("Error al asignar el proyecto:", error.message);
        mensaje.value = error.message;
        tipoMensaje.value = "error";
        mostrarMensaje.value = true;
      }
    };

    //Update-Eliminar//
    const eliminarEmpleado = async (item) => {
      // Confirmar con el usuario antes de eliminar
      const confirmDelete = confirm(
        `¿Seguro que quieres eliminar al empleado ${item.nombre} con DNI ${item.nif}?`
      );

      if (confirmDelete) {
        try {
          await empleadoStore.eliminarEmpleado(item);
          await empleadoStore.fetchEmpleados();

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

    /// Función para la navegación
    const navigateTo = (path) => {
      router.push(path);
    };

    return {
      empleadosDTO,
      fetchEmpleados,
      useProyectoStore,
      searchQuery,
      newEmpleado,
      proyectoStore,
      menuItems,
      headers,
      empleados,
      proyectos,
      loading,
      error,
      dialog,
      dialogProyecto,
      dialogAdd,
      editedEmpleado,
      openEditDialog,
      closeEditDialog,
      closeProyectosDialog,
      updateEmpleado,
      agregarEmpleado,
      eliminarEmpleado,
      mensaje,
      tipoMensaje,
      mostrarMensaje,
      editedProyectos,
      asignarProyecto,
      openAsignarDialog,
      openAddDialog,
      navigateTo,
      router,
      closeNewDialog,
      editedDesasignar,
      resetNewEmpleado,
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
  text-align: center;
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
  max-width: 170px;
  min-width: 170px;
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
  font-size: 25px;
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
