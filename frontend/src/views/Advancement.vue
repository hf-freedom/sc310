<template>
  <div>
    <h2 style="margin-bottom: 20px;">🎖 晋级名单</h2>

    <el-form :inline="true" style="margin-bottom: 20px;">
      <el-form-item label="选择赛事">
        <el-select v-model="selectedEvent" placeholder="请选择赛事" style="width: 250px;">
          <el-option
            v-for="event in events"
            :key="event.id"
            :label="event.name"
            :value="event.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchAdvancement" :disabled="!selectedEvent">
          查看晋级名单
        </el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="20" v-if="advancementData">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>🏆 晋级选手 (共 {{ advancementData.advancedPlayers.length }} 人)</span>
          </template>
          <el-table :data="advancedPlayersDetail" border stripe>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="level" label="等级" width="100" />
            <el-table-column prop="region" label="地区" width="120" />
            <el-table-column prop="teamId" label="队伍" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.teamId" type="warning" size="small">{{ row.teamId }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>📊 赛事统计</span>
          </template>
          <el-descriptions border :column="1">
            <el-descriptions-item label="已完成比赛">
              {{ advancementData.totalCompletedMatches }} 场
            </el-descriptions-item>
            <el-descriptions-item label="晋级人数">
              {{ advancementData.advancedPlayers.length }} 人
            </el-descriptions-item>
            <el-descriptions-item label="晋级比例">
              {{ totalPlayers > 0 ? Math.round((advancementData.advancedPlayers.length / totalPlayers) * 100) : 0 }}%
            </el-descriptions-item>
          </el-descriptions>

          <div style="margin-top: 20px; padding: 15px; background: #f5f7fa; border-radius: 4px;">
            <p style="margin: 0; color: #606266;">
              <strong>💡 说明：</strong>
            </p>
            <ul style="margin: 10px 0 0 20px; color: #909399;">
              <li>系统根据获胜场次数自动计算晋级名单</li>
              <li>违规选手将被自动排除</li>
              <li>晋级选手可进入下一轮比赛</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!advancementData" description="请选择赛事查看晋级名单" />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'Advancement',
  setup() {
    const events = ref([])
    const players = ref([])
    const selectedEvent = ref('')
    const advancementData = ref(null)

    const totalPlayers = computed(() => {
      // 估算总参赛人数
      return 16
    })

    const advancedPlayersDetail = computed(() => {
      if (!advancementData.value) return []
      return advancementData.value.advancedPlayers.map(id => {
        const player = players.value.find(p => p.id === id)
        return player || { id, name: id, level: '-', region: '-', teamId: '' }
      })
    })

    const fetchEvents = async () => {
      try {
        const res = await axios.get('/api/events')
        if (res.data.success) {
          events.value = res.data.data
        }
      } catch (e) {
        ElMessage.error('获取赛事列表失败')
      }
    }

    const fetchPlayers = async () => {
      try {
        const res = await axios.get('/api/players')
        if (res.data.success) {
          players.value = res.data.data
        }
      } catch (e) {
        ElMessage.error('获取选手列表失败')
      }
    }

    const fetchAdvancement = async () => {
      if (!selectedEvent.value) return
      try {
        const res = await axios.get(`/api/matches/${selectedEvent.value}/advancement`)
        if (res.data.success) {
          advancementData.value = res.data.data
        }
      } catch (e) {
        ElMessage.error('获取晋级名单失败')
      }
    }

    onMounted(() => {
      fetchEvents()
      fetchPlayers()
    })

    return {
      events,
      players,
      selectedEvent,
      advancementData,
      totalPlayers,
      advancedPlayersDetail,
      fetchAdvancement
    }
  }
}
</script>
