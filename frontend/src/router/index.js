import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Players from '../views/Players.vue'
import Events from '../views/Events.vue'
import Registration from '../views/Registration.vue'
import Groups from '../views/Groups.vue'
import Results from '../views/Results.vue'
import Advancement from '../views/Advancement.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/players', component: Players },
  { path: '/events', component: Events },
  { path: '/registration', component: Registration },
  { path: '/groups', component: Groups },
  { path: '/results', component: Results },
  { path: '/advancement', component: Advancement }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
