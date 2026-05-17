<template>
  <div>
    <h2 style="margin-bottom: 20px;">📝 报名管理</h2>

    <el-card style="margin-bottom: 20px;">
      <template #header>选手报名</template>
      <el-form :inline="true" :model="registrationForm">
        <el-form-item label="选择选手">
          <el-select v-model="registrationForm.playerId" placeholder="请选择选手" style="width: 200px;">
            <el-option
              v-for="player in availablePlayers"
              :key="player.id"
              :label="player.name + ' (' + player.level + ', ' + player.age + '岁)'"
              :value="player.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择赛事">
          <el-select v-model="registrationForm.eventId" placeholder="请选择赛事" style="width: 250px;">
            <el-option
              v-for="event in events"
              :key="event.id"
              :label="event.name + ' (年龄:' + event.minAge + '-' + event.maxAge + ', 等级:' + event.allowedLevels.join(',') + ')'"
              :value="event.id"
              :disabled="!event.isRegistrationOpen"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register" :disabled="!canRegister">立即报名</el-button>
        </el-form-item>
      </el-form>

      <el-divider v-if="selectedPlayer && selectedEvent" />

      <div v-if="selectedPlayer && selectedEvent" style="margin-top: 20px;">
        <h4 style="margin-bottom: 15px; color: #409EFF;">📋 报名资格校验结果</h4>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card :body-style="{ padding: '15px' }" style="text-align: center;">
              <div style="font-size: 32px; margin-bottom: 10px;">
                {{ validationResult.age.valid ? '✅' : '❌' }}
              </div>
              <div style="font-weight: bold; margin-bottom: 5px;">年龄校验</div>
              <div style="font-size: 13px; color: #606266;">
                选手: {{ selectedPlayer.age }} 岁
              </div>
              <div style="font-size: 13px; color: #606266;">
                要求: {{ selectedEvent.minAge }}-{{ selectedEvent.maxAge }} 岁
              </div>
              <el-tag :type="validationResult.age.valid ? 'success' : 'danger'" size="small" style="margin-top: 10px;">
                {{ validationResult.age.message }}
              </el-tag>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card :body-style="{ padding: '15px' }" style="text-align: center;">
              <div style="font-size: 32px; margin-bottom: 10px;">
                {{ validationResult.level.valid ? '✅' : '❌' }}
              </div>
              <div style="font-weight: bold; margin-bottom: 5px;">等级校验</div>
              <div style="font-size: 13px; color: #606266;">
                选手: {{ selectedPlayer.level }}
              </div>
              <div style="font-size: 13px; color: #606266;">
                允许: {{ selectedEvent.allowedLevels.join(', ') }}
              </div>
              <el-tag :type="validationResult.level.valid ? 'success' : 'danger'" size="small" style="margin-top: 10px;">
                {{ validationResult.level.message }}
              </el-tag>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card :body-style="{ padding: '15px' }" style="text-align: center;">
              <div style="font-size: 32px; margin-bottom: 10px;">
                {{ validationResult.availability.valid ? '✅' : (selectedEvent.currentSlots >= selectedEvent.maxSlots ? '⏳' : '❌') }}
              </div>
              <div style="font-weight: bold; margin-bottom: 5px;">名额状态</div>
              <div style="font-size: 13px; color: #606266;">
                已报名: 
                <strong :style="{ color: validationResult.availability.valid ? '#67C23A' : '#E6A23C', fontSize: '16px' }">
                  {{ selectedEvent.currentSlots }}
                </strong> 人
              </div>
              <div style="font-size: 13px; color: #606266;">
                剩余名额: 
                <strong :style="{ color: validationResult.availability.valid ? '#67C23A' : '#F56C6C', fontSize: '18px', fontWeight: 'bold' }">
                  {{ selectedEvent.maxSlots - selectedEvent.currentSlots }}
                </strong> 个
              </div>
              <el-progress
                :percentage="Math.round((selectedEvent.currentSlots / selectedEvent.maxSlots) * 100)"
                :status="validationResult.availability.valid ? '' : 'warning'"
                style="margin-top: 10px;"
              />
            </el-card>
          </el-col>
        </el-row>

        <el-alert
          v-if="!validationResult.canRegister && validationResult.message"
          :title="validationResult.message"
          type="error"
          show-icon
          style="margin-top: 20px;"
        />
        <el-alert
          v-else-if="validationResult.canRegister && !validationResult.availability.valid"
          title="名额已满，报名成功后将进入候补名单"
          type="warning"
          show-icon
          style="margin-top: 20px;"
        />
        <el-alert
          v-else-if="validationResult.canRegister"
          title="资格校验通过，可以报名！"
          type="success"
          show-icon
          style="margin-top: 20px;"
        />
      </div>
    </el-card>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="已报名选手" name="confirmed">
        <el-table :data="confirmedRegistrations" border stripe style="width: 100%">
          <el-table-column prop="id" label="报名ID" width="150" />
          <el-table-column prop="playerId" label="选手ID" width="120" />
          <el-table-column prop="eventId" label="赛事ID" width="120" />
          <el-table-column prop="registrationTime" label="报名时间" width="180" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="cancelRegistration(row)">
                退赛
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="候补名单" name="waitlist">
        <el-table :data="waitlistRegistrations" border stripe style="width: 100%">
          <el-table-column prop="id" label="报名ID" width="150" />
          <el-table-column prop="playerId" label="选手ID" width="120" />
          <el-table-column prop="eventId" label="赛事ID" width="120" />
          <el-table-column prop="waitlistPosition" label="候补位置" width="100" />
          <el-table-column prop="registrationTime" label="报名时间" width="180" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="cancelRegistration(row)">
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'Registration',
  setup() {
    const players = ref([])
    const events = ref([])
    const registrations = ref([])
    const activeTab = ref('confirmed')
    const registrationForm = ref({
      playerId: '',
      eventId: ''
    })

    const availablePlayers = computed(() => {
      return players.value.filter(p => !p.isViolated)
    })

    const selectedPlayer = computed(() => {
      if (!registrationForm.value.playerId) return null
      return players.value.find(p => p.id === registrationForm.value.playerId)
    })

    const selectedEvent = computed(() => {
      if (!registrationForm.value.eventId) return null
      return events.value.find(e => e.id === registrationForm.value.eventId)
    })

    const validationResult = computed(() => {
      if (!selectedPlayer.value || !selectedEvent.value) {
        return {
          age: { valid: false, message: '请选择选手和赛事' },
          level: { valid: false, message: '请选择选手和赛事' },
          availability: { valid: false, message: '请选择选手和赛事' },
          canRegister: false,
          message: ''
        }
      }

      const player = selectedPlayer.value
      const event = selectedEvent.value

      const ageValid = player.age >= event.minAge && player.age <= event.maxAge
      const levelValid = event.allowedLevels.includes(player.level)
      const hasAvailability = event.currentSlots < event.maxSlots

      let message = ''
      if (!ageValid) {
        message = `年龄不符合要求，该赛事要求年龄: ${event.minAge}-${event.maxAge}岁`
      } else if (!levelValid) {
        message = `等级不符合要求，该赛事允许等级: ${event.allowedLevels.join(', ')}`
      } else if (!event.isRegistrationOpen) {
        message = '该赛事报名已关闭'
      }

      return {
        age: {
          valid: ageValid,
          message: ageValid ? '年龄符合' : '年龄不符合'
        },
        level: {
          valid: levelValid,
          message: levelValid ? '等级符合' : '等级不符合'
        },
        availability: {
          valid: hasAvailability,
          message: hasAvailability ? '有名额' : '名额已满，可候补'
        },
        canRegister: ageValid && levelValid && event.isRegistrationOpen,
        message: message
      }
    })

    const canRegister = computed(() => {
      return selectedPlayer.value && selectedEvent.value && validationResult.value.canRegister
    })

    const confirmedRegistrations = computed(() => {
      return registrations.value.filter(r => r.status === 'CONFIRMED')
    })

    const waitlistRegistrations = computed(() => {
      return registrations.value.filter(r => r.isWaitlisted)
    })

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

    const fetchRegistrations = async () => {
      if (!registrationForm.value.eventId) return
      try {
        const res = await axios.get(`/api/events/${registrationForm.value.eventId}/registrations`)
        if (res.data.success) {
          registrations.value = res.data.data
        }
      } catch (e) {
        ElMessage.error('获取报名列表失败')
      }
    }

    const register = async () => {
      if (!registrationForm.value.playerId || !registrationForm.value.eventId) {
        ElMessage.warning('请选择选手和赛事')
        return
      }
      const oldSlots = selectedEvent.value?.currentSlots
      const oldMaxSlots = selectedEvent.value?.maxSlots
      
      try {
        const res = await axios.post(`/api/events/${registrationForm.value.eventId}/register`, {
          playerId: registrationForm.value.playerId
        })
        if (res.data.success) {
          const resultMsg = res.data.data
          
          await fetchEvents()
          await fetchRegistrations()
          
          await new Promise(resolve => setTimeout(resolve, 100))
          
          const newSlots = selectedEvent.value?.currentSlots
          if (oldSlots !== undefined && newSlots !== undefined) {
            const remainSlots = oldMaxSlots - newSlots
            if (resultMsg.includes('候补')) {
              ElMessage.warning({
                message: `${resultMsg}，当前名额已用完`,
                duration: 3000
              })
            } else {
              ElMessage.success({
                message: `${resultMsg}，剩余名额: ${remainSlots} 个`,
                duration: 3000
              })
            }
          } else {
            ElMessage.success(resultMsg)
          }
        } else {
          ElMessage.error(res.data.message)
        }
      } catch (e) {
        ElMessage.error(e.response?.data?.message || '报名失败')
      }
    }

    const cancelRegistration = async (reg) => {
      try {
        await ElMessageBox.confirm(
          '确定要取消该选手的报名吗？退赛后将自动调整候补选手并重新分组',
          '确认退赛',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        const res = await axios.post(`/api/events/${reg.eventId}/cancel-registration`, {
          registrationId: reg.id
        })
        if (res.data.success) {
          await fetchEvents()
          await fetchRegistrations()
          
          const event = events.value.find(e => e.id === reg.eventId)
          if (event) {
            const remainSlots = event.maxSlots - event.currentSlots
            ElMessage.success({
              message: `${res.data.data}，剩余名额: ${remainSlots} 个`,
              duration: 3000
            })
          } else {
            ElMessage.success(res.data.data)
          }
        }
      } catch (e) {
        if (e !== 'cancel') {
          ElMessage.error('操作失败')
        }
      }
    }

    onMounted(() => {
      fetchPlayers()
      fetchEvents()
    })

    return {
      players,
      events,
      registrations,
      activeTab,
      registrationForm,
      availablePlayers,
      selectedPlayer,
      selectedEvent,
      validationResult,
      canRegister,
      confirmedRegistrations,
      waitlistRegistrations,
      register,
      cancelRegistration,
      fetchRegistrations
    }
  }
}
</script>
