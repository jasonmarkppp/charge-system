import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/',
    redirect: '/login'
  },
  // 管理员路由
  {
    path: '/admin',
    component: () => import('../components/Layout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'teachers', name: 'TeacherManage', component: () => import('../views/admin/TeacherManage.vue') },
      { path: 'students', name: 'StudentManage', component: () => import('../views/admin/StudentManage.vue') },
      { path: 'grades', name: 'GradeManage', component: () => import('../views/admin/GradeManage.vue') },
      { path: 'classes', name: 'ClassManage', component: () => import('../views/admin/ClassManage.vue') },
      { path: 'subjects', name: 'SubjectManage', component: () => import('../views/admin/SubjectManage.vue') },
      { path: 'projects', name: 'ProjectManage', component: () => import('../views/admin/ProjectManage.vue') },
      { path: 'criteria', name: 'CriteriaManage', component: () => import('../views/admin/CriteriaManage.vue') },
    ]
  },
  // 教师路由
  {
    path: '/teacher',
    component: () => import('../components/Layout.vue'),
    meta: { role: 'TEACHER' },
    children: [
      { path: '', redirect: '/teacher/tasks' },
      { path: 'tasks', name: 'TeacherTasks', component: () => import('../views/teacher/TaskList.vue') },
      { path: 'task/publish', name: 'PublishTask', component: () => import('../views/teacher/PublishTask.vue') },
      { path: 'reports/:taskId', name: 'ReportList', component: () => import('../views/teacher/ReportList.vue') },
      { path: 'review/:reportId', name: 'ReviewReport', component: () => import('../views/teacher/ReviewReport.vue') },
    ]
  },
  // 学生路由
  {
    path: '/student',
    component: () => import('../components/Layout.vue'),
    meta: { role: 'STUDENT' },
    children: [
      { path: '', redirect: '/student/tasks' },
      { path: 'tasks', name: 'StudentTasks', component: () => import('../views/student/TaskList.vue') },
      { path: 'report/:taskId', name: 'WriteReport', component: () => import('../views/student/WriteReport.vue') },
      { path: 'history', name: 'ReportHistory', component: () => import('../views/student/ReportHistory.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  if (to.path === '/login') {
    if (token) {
      // 已登录，跳转到对应首页
      const role = userInfo.role
      if (role === 'ADMIN') next('/admin/dashboard')
      else if (role === 'TEACHER') next('/teacher/tasks')
      else if (role === 'STUDENT') next('/student/tasks')
      else next()
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      // 检查角色权限
      if (to.matched.some(record => record.meta.role)) {
        const requiredRole = to.matched.find(record => record.meta.role)?.meta.role
        if (requiredRole && userInfo.role !== requiredRole) {
          next('/login')
        } else {
          next()
        }
      } else {
        next()
      }
    }
  }
})

export default router
