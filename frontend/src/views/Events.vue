<template>
  <div>
    <h2 style="margin-bottom: 20px;">🎯 赛事管理</h2>

    <el-table :data="events" border stripe style="width: 100%">
      <el-table-column prop="id" label="赛事ID" width="120" />
      <el-table-column prop="name" label="赛事名称" width="180" />
      <el-table-column label="年龄限制" width="120">
        <template #default="{ row }">
          {{ row.minAge }} - {{ row.maxAge }}岁
        </template>
      </el-table-column>
      <el-table-column prop="allowedLevels" label="允许等级" width="180">
        <template #default="{ row }">
          <el-tag v-for="level in row.allowedLevels" :key="level" size="small" style="margin-right: 5px;">
            {{ level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名情况" width="150">
        <template #default="{ row }">
          <el-progress
            :percentage="Math.round((row.currentSlots / row.maxSlots) * 100)"
            :format="() => `${row.currentSlots}/${row.maxSlots}`"
          />
        </template>
      </el-table-column>
      <el-table-column label="报名状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isRegistrationOpen ? 'success' : 'info'">
            {{ row.isRegistrationOpen ? '报名中' : '已截止' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'Events',
  setup() {
    const events = ref([])

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

    onMounted(fetchEvents)

    return {
      events
    }
  }
}
</script>
