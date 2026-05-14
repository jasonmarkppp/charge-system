import request from '../utils/request'

// =================== 认证相关 ===================
export const login = (data) => request.post('/auth/login', data)
export const getUserInfo = () => request.get('/auth/info')
export const changePassword = (data) => request.post('/auth/password', data)

// =================== 管理员接口 ===================
// 教师管理
export const getTeachers = (params) => request.get('/admin/teachers', { params })
export const addTeacher = (data) => request.post('/admin/teacher', data)
export const updateTeacher = (data) => request.put('/admin/teacher', data)
export const deleteTeacher = (id) => request.delete(`/admin/teacher/${id}`)
export const resetTeacherPassword = (id) => request.post(`/admin/teacher/reset-password/${id}`)

// 学生管理
export const getStudents = (params) => request.get('/admin/students', { params })
export const addStudent = (data) => request.post('/admin/student', data)
export const updateStudent = (data) => request.put('/admin/student', data)
export const deleteStudent = (id) => request.delete(`/admin/student/${id}`)

// 年级管理
export const getGrades = () => request.get('/admin/grades')
export const addGrade = (data) => request.post('/admin/grade', data)
export const updateGrade = (data) => request.put('/admin/grade', data)
export const deleteGrade = (id) => request.delete(`/admin/grade/${id}`)

// 班级管理
export const getClasses = (params) => request.get('/admin/classes', { params })
export const getAllClasses = () => request.get('/admin/classes/all')
export const addClass = (data) => request.post('/admin/class', data)
export const updateClass = (data) => request.put('/admin/class', data)
export const deleteClass = (id) => request.delete(`/admin/class/${id}`)

// 学科管理
export const getSubjects = () => request.get('/admin/subjects')
export const addSubject = (data) => request.post('/admin/subject', data)
export const updateSubject = (data) => request.put('/admin/subject', data)
export const deleteSubject = (id) => request.delete(`/admin/subject/${id}`)

// 实验项目管理
export const getProjects = (params) => request.get('/admin/projects', { params })
export const addProject = (data) => request.post('/admin/project', data)
export const updateProject = (data) => request.put('/admin/project', data)
export const deleteProject = (id) => request.delete(`/admin/project/${id}`)

// 评分标准
export const getCriteria = (params) => request.get('/admin/criteria', { params })
export const addCriteria = (data) => request.post('/admin/criteria', data)
export const updateCriteria = (data) => request.put('/admin/criteria', data)
export const deleteCriteria = (id) => request.delete(`/admin/criteria/${id}`)

// 统计
export const getAdminStatistics = () => request.get('/admin/statistics')

// =================== 教师接口 ===================
export const getTeacherTasks = (params) => request.get('/teacher/tasks', { params })
export const publishTask = (data) => request.post('/teacher/task', data)
export const updateTask = (data) => request.put('/teacher/task', data)
export const deleteTask = (id) => request.delete(`/teacher/task/${id}`)
export const getTaskReports = (taskId, params) => request.get(`/teacher/task/${taskId}/reports`, { params })
export const getReportDetail = (reportId) => request.get(`/teacher/report/${reportId}`)
export const reviewReport = (data) => request.post('/teacher/report/review', data)
export const getTeacherProjects = () => request.get('/teacher/projects')
export const getTeacherClasses = () => request.get('/teacher/my-classes')
export const getTeacherCriteria = (projectId) => request.get(`/teacher/criteria/${projectId}`)
export const getTaskStatistics = (taskId) => request.get(`/teacher/statistics/${taskId}`)
export const analyzeReport = (reportId, projectId = 0) => request.post(`/teacher/report/analyze/${reportId}?projectId=${projectId}`, null, { timeout: 120000 })

// =================== 学生接口 ===================
export const getStudentTasks = (params) => request.get('/student/tasks', { params })
export const submitReport = (data) => request.post('/student/report/submit', data)
export const saveDraft = (data) => request.post('/student/report/draft', data)
export const getMyReport = (taskId) => request.get(`/student/report/task/${taskId}`)
export const getMyReports = (params) => request.get('/student/reports', { params })
export const uploadFile = (formData) => request.post('/student/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
export const deleteAttachment = (id) => request.delete(`/student/attachment/${id}`)
