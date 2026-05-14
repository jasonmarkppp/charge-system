<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon size="24"><Experiment /></el-icon>
        <span v-show="!isCollapse">实验报告评价系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="isCollapse"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <!-- 管理员菜单 -->
        <template v-if="userInfo.role === 'ADMIN'">
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据总览</span>
          </el-menu-item>
          <el-menu-item index="/admin/grades">
            <el-icon><School /></el-icon>
            <span>年级管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/classes">
            <el-icon><House /></el-icon>
            <span>班级管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/teachers">
            <el-icon><Avatar /></el-icon>
            <span>教师管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/students">
            <el-icon><User /></el-icon>
            <span>学生管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/subjects">
            <el-icon><Reading /></el-icon>
            <span>学科管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/projects">
            <el-icon><Notebook /></el-icon>
            <span>实验项目</span>
          </el-menu-item>
          <el-menu-item index="/admin/criteria">
            <el-icon><List /></el-icon>
            <span>评分标准</span>
          </el-menu-item>
        </template>

        <!-- 教师菜单 -->
        <template v-if="userInfo.role === 'TEACHER'">
          <el-menu-item index="/teacher/tasks">
            <el-icon><Document /></el-icon>
            <span>实验任务</span>
          </el-menu-item>
          <el-menu-item index="/teacher/task/publish">
            <el-icon><Plus /></el-icon>
            <span>发布任务</span>
          </el-menu-item>
        </template>

        <!-- 学生菜单 -->
        <template v-if="userInfo.role === 'STUDENT'">
          <el-menu-item index="/student/tasks">
            <el-icon><Document /></el-icon>
            <span>实验任务</span>
          </el-menu-item>
          <el-menu-item index="/student/history">
            <el-icon><Clock /></el-icon>
            <span>历史成绩</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>{{ roleText }}</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="user-name">{{ userInfo.realName }}</span>
          <el-dropdown @command="handleCommand">
            <el-avatar :size="36" :src="userInfo.avatar">
              {{ userInfo.realName?.charAt(0) }}
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const userInfo = computed(() => userStore.userInfo)

const roleText = computed(() => {
  const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
  return map[userInfo.value.role] || ''
})

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    })
  } else if (command === 'password') {
    // 可以打开修改密码弹窗
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #3d4a5a;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  padding: 0 20px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  cursor: pointer;
  font-size: 20px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-name {
  font-size: 14px;
  color: #606266;
}
.main {
  background: #f5f7fa;
  padding: 20px;
}
.el-menu {
  border-right: none;
}
</style>
