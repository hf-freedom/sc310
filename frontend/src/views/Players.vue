<template>
  <div>
    <h2 style="margin-bottom: 20px;">👤 选手管理</h2>
    
    <el-button type="primary" @click="showAddDialog" style="margin-bottom: 20px;">
      + 添加选手
    </el-button>

    <el-table :data="players" border stripe style="width: 100%">
      <el-table-column prop="id" label="选手ID" width="150" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="level" label="等级" width="100" />
      <el-table-column prop="region" label="地区" width="120" />
      <el-table-column prop="teamId" label="队伍ID" width="120" />
      <el-table-column prop="historicalScore" label="历史成绩" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isViolated ? 'danger' : 'success'">
            {{ row.isViolated ? '已违规' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" type="warning" @click="disqualifyPlayer(row)">
            违规处理
          </el-button>
          <el-button size="small" type="danger" @click="deletePlayer(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="addDialogVisible" title="添加选手" width="50%">
      <el-form :model="newPlayer" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="newPlayer.name" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="newPlayer.age" :min="5" :max="100" />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="newPlayer.level" style="width: 100%">
            <el-option label="一级" value="一级" />
            <el-option label="二级" value="二级" />
            <el-option label="三级" value="三级" />
            <el-option label="四级" value="四级" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="newPlayer.region" />
        </el-form-item>
        <el-form-item label="队伍ID">
          <el-input v-model="newPlayer.teamId" />
        </el-form-item>
        <el-form-item label="历史成绩">
          <el-input-number v-model="newPlayer.historicalScore" :min="0" :max="1000" :step="0.1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addPlayer">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="disqualifyDialogVisible" title="违规处理" width="40%">
      <p>确定将选手 <strong>{{ currentPlayer?.name }}</strong> 标记为违规吗？</p>
      <p style="color: #f56c6c; margin-top: 10px;">此操作将取消该选手的所有参赛资格并重算晋级结果！</p>
      <el-input
        v-model="disqualifyReason"
        type="textarea"
        :rows="3"
        placeholder="请输入违规原因"
        style="margin-top: 20px;"
      />
      <template #footer>
        <el-button @click="disqualifyDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDisqualify">确定违规</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'Players',
  setup() {
    const players = ref([])
    const addDialogVisible = ref(false)
    const disqualifyDialogVisible = ref(false)
    const currentPlayer = ref(null)
    const disqualifyReason = ref('')
    const newPlayer = ref({
      name: '',
      age: 20,
      level: '二级',
      region: '',
      teamId: '',
      historicalScore: 0
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

    const showAddDialog = () => {
      newPlayer.value = {
        name: '',
        age: 20,
        level: '二级',
        region: '',
        teamId: '',
        historicalScore: 0
      }
      addDialogVisible.value = true
    }

    const addPlayer = async () => {
      if (!newPlayer.value.name) {
        ElMessage.warning('请输入姓名')
        return
      }
      try {
        const res = await axios.post('/api/players', newPlayer.value)
        if (res.data.success) {
          ElMessage.success('选手添加成功')
          addDialogVisible.value = false
          fetchPlayers()
        } else {
          ElMessage.error(res.data.message)
        }
      } catch (e) {
        ElMessage.error('添加失败')
      }
    }

    const disqualifyPlayer = (player) => {
      currentPlayer.value = player
      disqualifyReason.value = ''
      disqualifyDialogVisible.value = true
    }

    const confirmDisqualify = async () => {
      if (!disqualifyReason.value) {
        ElMessage.warning('请输入违规原因')
        return
      }
      try {
        const res = await axios.post('/api/matches/disqualify', {
          playerId: currentPlayer.value.id,
          reason: disqualifyReason.value
        })
        if (res.data.success) {
          ElMessage.success(res.data.data)
          disqualifyDialogVisible.value = false
          fetchPlayers()
        }
      } catch (e) {
        ElMessage.error('处理失败')
      }
    }

    const deletePlayer = async (id) => {
      try {
        await axios.delete(`/api/players/${id}`)
        ElMessage.success('删除成功')
        fetchPlayers()
      } catch (e) {
        ElMessage.error('删除失败')
      }
    }

    onMounted(fetchPlayers)

    return {
      players,
      addDialogVisible,
      disqualifyDialogVisible,
      currentPlayer,
      disqualifyReason,
      newPlayer,
      showAddDialog,
      addPlayer,
      disqualifyPlayer,
      confirmDisqualify,
      deletePlayer
    }
  }
}
</script>
