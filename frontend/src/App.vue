<template>
  <el-container style="height: 100vh">
    <el-header style="background: #409EFF; color: white; font-size: 24px; font-weight: bold; display: flex; align-items: center; justify-content: space-between;">
      <span>🏆 赛事报名分组系统</span>
      <el-button type="warning" @click="checkWarnings">
        系统提醒 ({{ warnings.length }})
      </el-button>
    </el-header>
    <el-container>
      <el-aside width="200px" style="background: #f5f5f5; padding-top: 20px;">
        <el-menu :default-active="activeMenu" router mode="vertical">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/players">选手管理</el-menu-item>
          <el-menu-item index="/events">赛事管理</el-menu-item>
          <el-menu-item index="/registration">报名管理</el-menu-item>
          <el-menu-item index="/groups">分组管理</el-menu-item>
          <el-menu-item index="/results">成绩录入</el-menu-item>
          <el-menu-item index="/advancement">晋级名单</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main style="padding: 20px; background: #fff;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <el-dialog v-model="warningDialogVisible" title="系统提醒" width="50%">
    <el-alert
      v-for="(warning, index) in warnings"
      :key="index"
      :title="warning"
      type="warning"
      :closable="false"
      style="margin-bottom: 10px;"
    />
  </el-dialog>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

export default {
  name: 'App',
  setup() {
    const route = useRoute()
    const activeMenu = ref('/')
    const warnings = ref([])
    const warningDialogVisible = ref(false)

    const fetchWarnings = async () => {
      try {
        const res = await axios.get('/api/matches/warnings')
        if (res.data.success) {
          warnings.value = res.data.data
        }
      } catch (e) {
        console.error(e)
      }
    }

    const checkWarnings = () => {
      warningDialogVisible.value = true
    }

    onMounted(() => {
      activeMenu.value = route.path
      fetchWarnings()
      setInterval(fetchWarnings, 30000)
    })

    return {
      activeMenu,
      warnings,
      warningDialogVisible,
      checkWarnings
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body {
  height: 100%;
}
</style>
