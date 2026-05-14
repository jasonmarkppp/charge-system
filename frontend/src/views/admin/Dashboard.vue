<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">学生总数</p>
              <h3 class="stat-value">{{ stats.totalStudents || 0 }}</h3>
            </div>
            <el-icon class="stat-icon" color="#409eff"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">教师总数</p>
              <h3 class="stat-value">{{ stats.totalTeachers || 0 }}</h3>
            </div>
            <el-icon class="stat-icon" color="#67c23a"><Avatar /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">实验任务</p>
              <h3 class="stat-value">{{ stats.totalTasks || 0 }}</h3>
            </div>
            <el-icon class="stat-icon" color="#e6a23c"><Document /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">已批阅报告</p>
              <h3 class="stat-value">{{ stats.reviewedReports || 0 }}</h3>
            </div>
            <el-icon class="stat-icon" color="#f56c6c"><Finished /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>实验报告提交统计</template>
          <div ref="chartRef1" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>各学科实验分布</template>
          <div ref="chartRef2" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminStatistics } from '../../api'
import * as echarts from 'echarts'

const stats = ref({})
const chartRef1 = ref(null)
const chartRef2 = ref(null)

onMounted(async () => {
  try {
    const res = await getAdminStatistics()
    stats.value = res.data
  } catch (e) {}

  // 初始化图表
  initChart1()
  initChart2()
})

function initChart1() {
  const chart = echarts.init(chartRef1.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [
      { name: '提交数', type: 'bar', data: [12, 25, 38, 45, 52, 60], color: '#409eff' },
      { name: '批阅数', type: 'bar', data: [10, 22, 35, 40, 48, 55], color: '#67c23a' }
    ]
  })
}

function initChart2() {
  const chart = echarts.init(chartRef2.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 35, name: '信息技术' },
        { value: 28, name: '物理' },
        { value: 22, name: '化学' },
        { value: 15, name: '生物' }
      ]
    }]
  })
}
</script>

<style scoped>
.stat-cards { margin-bottom: 10px; }
.stat-card { border-radius: 8px; }
.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stat-label { color: #909399; font-size: 14px; margin-bottom: 8px; }
.stat-value { font-size: 28px; color: #303133; }
.stat-icon { font-size: 48px; opacity: 0.8; }
</style>
