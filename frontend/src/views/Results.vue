<template>
  <div>
    <h2 style="margin-bottom: 20px;">🏆 成绩录入</h2>

    <el-card style="margin-bottom: 20px;">
      <template #header>录入比赛成绩</template>
      <el-form :inline="true" :model="resultForm">
        <el-form-item label="选择赛事">
          <el-select v-model="resultForm.eventId" placeholder="请选择赛事" style="width: 200px;">
            <el-option
              v-for="event in events"
              :key="event.id"
              :label="event.name"
              :value="event.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择分组">
          <el-select v-model="resultForm.groupId" placeholder="请选择分组" style="width: 150px;">
            <el-option
              v-for="group in groups"
              :key="group.id"
              :label="group.name"
              :value="group.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选手1">
          <el-select v-model="resultForm.player1Id" placeholder="选手1" style="width: 130px;">
            <el-option
              v-for="player in currentGroupPlayers"
              :key="player.id"
              :label="player.name"
              :value="player.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="比分">
          <el-input-number v-model="resultForm.player1Score" :min="0" :max="100" size="small" />
          :
          <el-input-number v-model="resultForm.player2Score" :min="0" :max="100" size="small" />
        </el-form-item>
        <el-form-item label="选手2">
          <el-select v-model="resultForm.player2Id" placeholder="选手2" style="width: 130px;">
            <el-option
              v-for="player in currentGroupPlayers"
              :key="player.id"
              :label="player.name"
              :value="player.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitResult">录入成绩</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <h3 style="margin-bottom: 15px;">已录入成绩</h3>
    <el-table :data="results" border stripe style="width: 100%">
      <el-table-column prop="eventId" label="赛事ID" width="120" />
      <el-table-column prop="groupId" label="分组" width="120" />
      <el-table-column label="对阵" width="250">
        <template #default="{ row }">
          {{ getPlayerName(row.player1Id) }} vs {{ getPlayerName(row.player2Id) }}
        </template>
      </el-table-column>
      <el-table-column label="比分" width="120">
        <template #default="{ row }">
          <strong :style="{ color: row.player1Score > row.player2Score ? '#67C23A' : '#F56C6C' }">
            {{ row.player1Score }}
          </strong>
          :
          <strong :style="{ color: row.player2Score > row.player1Score ? '#67C23A' : '#F56C6C' }">
            {{ row.player2Score }}
          </strong>
        </template>
      </el-table-column>
      <el-table-column label="获胜者" width="120">
        <template #default="{ row }">
          <el-tag type="success">{{ getPlayerName(row.winnerId) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'Results',
  setup() {
    const events = ref([])
    const groups = ref([])
    const players = ref([])
    const results = ref([])
    const resultForm = ref({
      eventId: '',
      groupId: '',
      player1Id: '',
      player2Id: '',
      player1Score: 0,
      player2Score: 0
    })

    const currentGroupPlayers = computed(() => {
      const group = groups.value.find(g => g.id === resultForm.value.groupId)
      if (!group) return []
      return group.playerIds.map(id => players.value.find(p => p.id === id)).filter(Boolean)
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

    const fetchGroups = async () => {
      if (!resultForm.value.eventId) return
      try {
        const res = await axios.get('/api/groups', { params: { eventId: resultForm.value.eventId } })
        if (res.data.success) {
          groups.value = res.data.data
        }
      } catch (e) {
        ElMessage.error('获取分组列表失败')
      }
    }

    const fetchResults = async () => {
      try {
        const res = await axios.get('/api/matches/results')
        if (res.data.success) {
          results.value = res.data.data
        }
      } catch (e) {
        ElMessage.error('获取成绩列表失败')
      }
    }

    const submitResult = async () => {
      if (!resultForm.value.eventId || !resultForm.value.groupId) {
        ElMessage.warning('请选择赛事和分组')
        return
      }
      if (!resultForm.value.player1Id || !resultForm.value.player2Id) {
        ElMessage.warning('请选择两位选手')
        return
      }
      if (resultForm.value.player1Id === resultForm.value.player2Id) {
        ElMessage.warning('两位选手不能相同')
        return
      }
      if (resultForm.value.player1Score === resultForm.value.player2Score) {
        ElMessage.warning('比分不能相同，必须分出胜负')
        return
      }

      try {
        const res = await axios.post('/api/matches/result', resultForm.value)
        if (res.data.success) {
          ElMessage.success('成绩录入成功！晋级名单已更新')
          fetchResults()
          resultForm.value.player1Score = 0
          resultForm.value.player2Score = 0
        } else {
          ElMessage.error(res.data.message)
        }
      } catch (e) {
        ElMessage.error(e.response?.data?.message || '录入失败')
      }
    }

    const getPlayerName = (id) => {
      const player = players.value.find(p => p.id === id)
      return player ? player.name : id
    }

    onMounted(() => {
      fetchEvents()
      fetchPlayers()
      fetchResults()
    })

    return {
      events,
      groups,
      players,
      results,
      resultForm,
      currentGroupPlayers,
      fetchGroups,
      submitResult,
      getPlayerName
    }
  }
}
</script>
