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

        <v-list-item
          @click="navigateTo('/empleados')"
          prepend-icon="mdi-account"
        >
          Empleados
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
      <v-toolbar-title align="center">Inicio</v-toolbar-title>
    </v-app-bar>

    <!-- Contenido principal -->
    <v-main>
      <v-container fluid>
        <v-row class="fill-height" align="center" justify="center">
          <v-col cols="12" md="8" class="text-center title-image-wrapper">
            <h1>Bienvenidos!</h1>
            <img
              src="@/assets/images/Castilla_La_Mancha_-(blanco).png"
              alt="Logo"
              class="title-image"
            />
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();
const navigateTo = (path) => {
  router.push(path);
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

/* Contenedor principal */
.v-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #001021;
  padding: 24px;
}

/* Estilo para la imagen del título */
.title-image {
  max-width: 300px;
  width: 100%;
  height: auto;
}

/* Estilo para los elementos del menú */
.v-list-item {
  color: white;
  font-weight: bold;
}

/* Estilo para los botones del menú al pasar el ratón */
.v-list-item:hover {
  background-color: #026fc1;
  cursor: pointer;
}

.v-toolbar-title {
  font-size: 2rem;
}

.v-navigation-drawer {
  background-color: #026fc1;
}

.v-navigation-drawer .v-list-item-title {
  font-size: 2rem;
}

.v-navigation-drawer .v-list-item {
  font-size: 20px;
}

/* Cambios para que el título en la imagen se ajuste mejor */
.title-image-wrapper {
  text-align: center;
}
</style>
