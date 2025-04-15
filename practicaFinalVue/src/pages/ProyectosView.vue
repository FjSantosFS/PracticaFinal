<template>
  <!-- Renderiza el componente ArtistList -->
  <ProyectosComp />
</template>

<script>
import ProyectosComp from "../components/ProyectosComp.vue"; // Importación del componente ArtistList

export default {
  // Definición del componente Vue
  components: {
    ProyectosComp, // Registro del componente para su uso en el template
  },
};
</script>
