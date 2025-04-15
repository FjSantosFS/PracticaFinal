<template>
  <!-- Renderiza el componente ArtistList -->
  <EmpleadosComp />
</template>

<script>
import EmpleadosComp from "../components/EmpleadosComp.vue"; // Importación del componente ArtistList

export default {
  // Definición del componente Vue
  components: {
    EmpleadosComp, // Registro del componente para su uso en el template
  },
};
</script>
