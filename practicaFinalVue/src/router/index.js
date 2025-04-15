// Composables
import { createRouter, createWebHashHistory } from 'vue-router/auto'
import { setupLayouts } from 'virtual:generated-layouts'
import { routes } from 'vue-router/auto-routes'

// Importación manual de las vistas
import HomeView from '../pages/HomeView.vue';
import EmpleadosView from '../pages/EmpleadosView.vue';
import ProyectosView from '../pages/ProyectosView.vue';

// Definir las rutas manualmente
const manualRoutes = [
  {
    path: '/', // Ruta para la página de inicio
    name: 'Home',
    component: HomeView, // Componente que se renderiza para esta ruta
  },
  {
    path: '/empleados', // Ruta para la lista de artistas
    name: 'Empleados',
    component: EmpleadosView,
  },
  {
    path: '/proyectos', // Ruta para la lista de álbumes
    name: 'Proyectos',
    component: ProyectosView,
  },
];

// Combinar las rutas automáticas generadas con las manualmente definidas
const combinedRoutes = [...setupLayouts(routes), ...manualRoutes];

// Crear la instancia del router con la historia de navegación en hash
const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL), // Usando WebHashHistory para las URL
  routes: combinedRoutes, // Las rutas combinadas se pasan al router
});

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
  if (err?.message?.includes?.('Failed to fetch dynamically imported module')) {
    if (!localStorage.getItem('vuetify:dynamic-reload')) {
      console.log('Reloading page to fix dynamic import error')
      localStorage.setItem('vuetify:dynamic-reload', 'true')
      location.assign(to.fullPath)
    } else {
      console.error('Dynamic import error, reloading page did not fix it', err)
    }
  } else {
    console.error(err)
  }
})

router.isReady().then(() => {
  localStorage.removeItem('vuetify:dynamic-reload')
})

export default router
