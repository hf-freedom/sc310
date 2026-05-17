<template>
  <div>
    <h2 style="margin-bottom: 20px;">📊 分组管理</h2>

    <el-card style="margin-bottom: 20px;">
      <template #header>生成分组</template>
      <el-form :inline="true">
        <el-form-item label="选择赛事">
          <el-select v-model="selectedEvent" placeholder="请选择赛事" style="width: 250px;">
            <el-option
              v-for="event in events"
              :key="event.id"
              :label="event.name + ' (已报名: ' + event.currentSlots + '人)'"
              :value="event.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="轮次">
          <el-input-number v-model="round" :min="1" :max="10" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generateGroups" :disabled="!selectedEvent || confirmedPlayers.length === 0">
            智能生成分组
          </el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="selectedEvent && confirmedPlayers.length === 0"
        title="该赛事暂无已报名选手，请先在报名管理中添加选手"
        type="warning"
        show-icon
        style="margin-top: 15px;"
      />

      <el-divider v-if="selectedEvent && confirmedPlayers.length > 0" />

      <div v-if="selectedEvent && confirmedPlayers.length > 0" style="margin-top: 15px;">
        <h4 style="margin-bottom: 15px; color: #409EFF;">📋 分组依据说明</h4>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card :body-style="{ padding: '15px' }" style="text-align: center;">
              <div style="font-size: 28px; margin-bottom: 8px;">🏅</div>
              <div style="font-weight: bold; margin-bottom: 5px;">等级排序</div>
              <div style="font-size: 12px; color: #606266;">
                按选手等级从高到低排序<br/>
                (一级 > 二级 > 三级 > 四级)
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card :body-style="{ padding: '15px' }" style="text-align: center;">
              <div style="font-size: 28px; margin-bottom: 8px;">📈</div>
              <div style="font-weight: bold; margin-bottom: 5px;">历史成绩</div>
              <div style="font-size: 12px; color: #606266;">
                同等级选手按历史成绩排序<br/>
                成绩高者优先
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card :body-style="{ padding: '15px' }" style="text-align: center;">
              <div style="font-size: 28px; margin-bottom: 8px;">📍</div>
              <div style="font-weight: bold; margin-bottom: 5px;">地区均衡</div>
              <div style="font-size: 12px; color: #606266;">
                按地区均匀分配选手<br/>
                避免同地区聚集
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-alert
          title="同队选手避让原则：同一队伍的选手将尽量分配到不同小组，避免第一轮相遇"
          type="info"
          show-icon
          style="margin-top: 15px;"
        />
      </div>
    </el-card>

    <div v-if="groups.length > 0" style="margin-bottom: 20px;">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>📊 分组统计</span>
            </template>
            <el-row :gutter="20">
              <el-col :span="6">
                <div style="text-align: center;">
                  <div style="font-size: 28px; font-weight: bold; color: #409EFF;">{{ groups.length }}</div>
                  <div style="color: #909399; font-size: 13px;">小组数量</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div style="text-align: center;">
                  <div style="font-size: 28px; font-weight: bold; color: #67C23A;">{{ totalPlayers }}</div>
                  <div style="color: #909399; font-size: 13px;">参赛人数</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div style="text-align: center;">
                  <div style="font-size: 28px; font-weight: bold; color: #E6A23C;">{{ teamCount }}</div>
                  <div style="color: #909399; font-size: 13px;">涉及队伍</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div style="text-align: center;">
                  <div style="font-size: 28px; font-weight: bold; color: #909399;">{{ regionCount }}</div>
                  <div style="color: #909399; font-size: 13px;">覆盖地区</div>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>🤝 同队选手避让分析</span>
            </template>
            <div v-if="teamAvoidanceResult.teamsWithMultiplePlayers.length === 0">
              <el-empty description="暂无多选手队伍，无需避让" :image-size="60" />
            </div>
            <div v-else>
              <el-row :gutter="20" style="margin-bottom: 15px;">
                <el-col :span="8">
                  <div style="text-align: center;">
                    <div style="font-size: 28px; font-weight: bold; color: #67C23A;">{{ teamAvoidanceResult.successCount }}</div>
                    <div style="color: #909399; font-size: 13px;">成功避让</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div style="text-align: center;">
                    <div style="font-size: 28px; font-weight: bold; color: #F56C6C;">{{ teamAvoidanceResult.failedCount }}</div>
                    <div style="color: #909399; font-size: 13px;">未能避让</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div style="text-align: center;">
                    <div style="font-size: 28px; font-weight: bold; color: #409EFF;">{{ teamAvoidanceResult.successRate }}%</div>
                    <div style="color: #909399; font-size: 13px;">避让成功率</div>
                  </div>
                </el-col>
              </el-row>
              <el-divider style="margin: 10px 0;" />
              <div style="max-height: 120px; overflow-y: auto;">
                <div v-for="team in teamAvoidanceResult.teamsWithMultiplePlayers" :key="team.teamId" style="margin-bottom: 8px;">
                  <el-tag size="small" type="warning" style="margin-right: 10px;">{{ team.teamId }}</el-tag>
                  <span v-if="team.avoided" style="color: #67C23A; font-size: 13px;">
                    ✅ {{ team.playerCount }}名选手已分配到不同小组
                  </span>
                  <span v-else style="color: #F56C6C; font-size: 13px;">
                    ❌ {{ team.playerCount }}名选手在同一小组（无法避让）
                  </span>
                  <div style="margin-left: 70px; margin-top: 3px; font-size: 12px; color: #909399;">
                    选手: {{ team.playerNames.join(', ') }}
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-collapse v-model="activeNames">
      <el-collapse-item v-for="group in groups" :key="group.id" :name="group.id">
        <template #title>
          <strong>{{ group.name }}</strong>
          <el-tag type="info" style="margin-left: 10px;">第 {{ group.round }} 轮</el-tag>
          <span style="margin-left: 20px; color: #909399;">共 {{ group.playerIds.length }} 人</span>
          <el-tag v-if="getGroupStats(group).hasSameTeam" type="warning" size="small" style="margin-left: 10px;">
            含同队选手
          </el-tag>
        </template>
        
        <el-descriptions :column="3" border size="small" style="margin-bottom: 15px;">
          <el-descriptions-item label="等级分布">
            <el-tag v-for="(count, level) in getGroupStats(group).levelStats" :key="level" size="small" style="margin-right: 5px;">
              {{ level }}: {{ count }}人
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="地区分布">
            <el-tag v-for="(count, region) in getGroupStats(group).regionStats" :key="region" size="small" type="info" style="margin-right: 5px;">
              {{ region }}: {{ count }}人
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="队伍分布">
            <template v-if="Object.keys(getGroupStats(group).teamStats).length > 0">
              <el-tag v-for="(count, team) in getGroupStats(group).teamStats" :key="team" size="small" type="warning" style="margin-right: 5px;">
                {{ team }}: {{ count }}人
              </el-tag>
            </template>
            <span v-else style="color: #909399;">无</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-table :data="getGroupPlayers(group.playerIds)" border stripe>
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="level" label="等级" width="90">
            <template #default="{ row }">
              <el-tag :type="getLevelType(row.level)" size="small">{{ row.level }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="historicalScore" label="历史成绩" width="100" sortable />
          <el-table-column prop="age" label="年龄" width="70" />
          <el-table-column prop="region" label="地区" width="100" />
          <el-table-column prop="teamId" label="队伍" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.teamId" type="warning" size="small">{{ row.teamId }}</el-tag>
              <span v-else style="color: #c0c4cc;">-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
    </el-collapse>

    <el-empty v-if="groups.length === 0" description="暂无分组数据，请先生成分组" />
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'Groups',
  setup() {
    const events = ref([])
    const groups = ref([])
    const players = ref([])
    const registrations = ref([])
    const selectedEvent = ref('')
    const round = ref(1)
    const activeNames = ref([])

    const confirmedPlayers = computed(() => {
      if (!selectedEvent.value) return []
      return registrations.value
        .filter(r => r.status === 'CONFIRMED')
        .map(r => players.value.find(p => p.id === r.playerId))
        .filter(Boolean)
    })

    const totalPlayers = computed(() => {
      return groups.value.reduce((sum, g) => sum + g.playerIds.length, 0)
    })

    const teamCount = computed(() => {
      const teams = new Set()
      groups.value.forEach(g => {
        g.playerIds.forEach(pid => {
          const player = players.value.find(p => p.id === pid)
          if (player && player.teamId) teams.add(player.teamId)
        })
      })
      return teams.size
    })

    const regionCount = computed(() => {
      const regions = new Set()
      groups.value.forEach(g => {
        g.playerIds.forEach(pid => {
          const player = players.value.find(p => p.id === pid)
          if (player && player.region) regions.add(player.region)
        })
      })
      return regions.size
    })

    const teamAvoidanceResult = computed(() => {
      const teamPlayerMap = {}
      
      groups.value.forEach((group, groupIndex) => {
        group.playerIds.forEach(pid => {
          const player = players.value.find(p => p.id === pid)
          if (player && player.teamId) {
            if (!teamPlayerMap[player.teamId]) {
              teamPlayerMap[player.teamId] = []
            }
            teamPlayerMap[player.teamId].push({
              playerId: pid,
              name: player.name,
              groupIndex: groupIndex
            })
          }
        })
      })

      const teamsWithMultiplePlayers = []
      let successCount = 0
      let failedCount = 0

      Object.keys(teamPlayerMap).forEach(teamId => {
        const teamPlayers = teamPlayerMap[teamId]
        if (teamPlayers.length >= 2) {
          const groupIndices = new Set(teamPlayers.map(p => p.groupIndex))
          const avoided = groupIndices.size === teamPlayers.length
          
          if (avoided) {
            successCount++
          } else {
            failedCount++
          }

          teamsWithMultiplePlayers.push({
            teamId,
            playerCount: teamPlayers.length,
            playerNames: teamPlayers.map(p => p.name),
            avoided
          })
        }
      })

      const total = successCount + failedCount
      const successRate = total > 0 ? Math.round((successCount / total) * 100) : 100

      return {
        teamsWithMultiplePlayers,
        successCount,
        failedCount,
        successRate
      }
    })

    const getLevelType = (level) => {
      const types = {
        '一级': 'danger',
        '二级': 'warning',
        '三级': 'success',
        '四级': 'info'
      }
      return types[level] || 'info'
    }

    const getGroupStats = (group) => {
      const groupPlayers = group.playerIds
        .map(id => players.value.find(p => p.id === id))
        .filter(Boolean)

      const levelStats = {}
      const regionStats = {}
      const teamStats = {}
      let hasSameTeam = false

      groupPlayers.forEach(p => {
        levelStats[p.level] = (levelStats[p.level] || 0) + 1
        regionStats[p.region] = (regionStats[p.region] || 0) + 1
        if (p.teamId) {
          teamStats[p.teamId] = (teamStats[p.teamId] || 0) + 1
          if (teamStats[p.teamId] > 1) hasSameTeam = true
        }
      })

      return { levelStats, regionStats, teamStats, hasSameTeam }
    }

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

    const fetchRegistrations = async () => {
      if (!selectedEvent.value) return
      try {
        const res = await axios.get(`/api/events/${selectedEvent.value}/registrations`)
        if (res.data.success) {
          registrations.value = res.data.data
        }
      } catch (e) {
        console.error('获取报名列表失败', e)
      }
    }

    const fetchGroups = async () => {
      if (!selectedEvent.value) return
      try {
        const res = await axios.get('/api/groups', { params: { eventId: selectedEvent.value } })
        if (res.data.success) {
          groups.value = res.data.data
          if (groups.value.length > 0) {
            activeNames.value = groups.value.map(g => g.id)
          }
        }
      } catch (e) {
        ElMessage.error('获取分组列表失败')
      }
    }

    const generateGroups = async () => {
      try {
        const res = await axios.post('/api/groups/generate', {
          eventId: selectedEvent.value,
          round: round.value
        })
        if (res.data.success) {
          ElMessage.success('分组生成成功！分组时已考虑同队选手避让原则')
          groups.value = res.data.data
          activeNames.value = groups.value.map(g => g.id)
        }
      } catch (e) {
        ElMessage.error('生成分组失败')
      }
    }

    const getGroupPlayers = (playerIds) => {
      return playerIds.map(id => players.value.find(p => p.id === id)).filter(Boolean)
    }

    watch(selectedEvent, () => {
      fetchRegistrations()
      fetchGroups()
    })

    onMounted(() => {
      fetchEvents()
      fetchPlayers()
    })

    return {
      events,
      groups,
      players,
      selectedEvent,
      round,
      activeNames,
      confirmedPlayers,
      totalPlayers,
      teamCount,
      regionCount,
      teamAvoidanceResult,
      generateGroups,
      fetchGroups,
      getGroupPlayers,
      getLevelType,
      getGroupStats
    }
  }
}
</script>
