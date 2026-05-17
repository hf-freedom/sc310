<template>
  <div class="home">
    <h1 style="margin-bottom: 30px; color: #409EFF;">🏆 欢迎使用赛事报名分组系统</h1>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" style="text-align: center; cursor: pointer;" @click="$router.push('/players')">
          <div style="font-size: 48px;">👤</div>
          <h3>选手管理</h3>
          <p>管理参赛选手信息</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" style="text-align: center; cursor: pointer;" @click="$router.push('/events')">
          <div style="font-size: 48px;">🎯</div>
          <h3>赛事管理</h3>
          <p>查看所有比赛项目</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" style="text-align: center; cursor: pointer;" @click="$router.push('/registration')">
          <div style="font-size: 48px;">📝</div>
          <h3>报名管理</h3>
          <p>处理选手报名申请</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" style="text-align: center; cursor: pointer;" @click="$router.push('/groups')">
          <div style="font-size: 48px;">📊</div>
          <h3>分组管理</h3>
          <p>自动生成分组结果</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>📋 系统功能说明</span>
          </template>
          <ul style="line-height: 2;">
            <li>✅ 报名时自动校验年龄、等级、项目资格</li>
            <li>✅ 报名成功后占用项目名额，名额满自动进入候补</li>
            <li>✅ 按等级、历史成绩、地区进行智能分组</li>
            <li>✅ 同队选手尽量避免第一轮相遇</li>
            <li>✅ 退赛后自动调整候补选手并重新分组</li>
            <li>✅ 比赛成绩录入后自动推进晋级名单</li>
            <li>✅ 定时扫描报名截止、分组未完成、成绩未录入</li>
            <li>✅ 违规选手取消资格并重算晋级结果</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>📈 操作流程</span>
          </template>
          <el-steps direction="vertical" :active="4">
            <el-step title="添加选手" description="在选手管理中录入参赛选手信息" />
            <el-step title="赛事报名" description="为选手报名符合条件的比赛项目" />
            <el-step title="生成分组" description="报名截止后自动或手动生成分组" />
            <el-step title="录入成绩" description="比赛结束后录入比赛成绩" />
            <el-step title="查看晋级" description="系统自动计算晋级名单" />
          </el-steps>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home {
  padding: 20px;
}
</style>
