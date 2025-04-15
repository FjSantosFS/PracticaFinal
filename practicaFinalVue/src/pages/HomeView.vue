<template>
  <!-- Renderiza el componente HomeComponent -->
  <HomeComponent />
</template>

<script>
import HomeComponent from "../components/HomeComponent.vue"; // Importación del componente

export default {
  // Definición del componente Vue
  components: {
    HomeComponent, // Registro del componente para su uso en el template
  },
};
</script>
