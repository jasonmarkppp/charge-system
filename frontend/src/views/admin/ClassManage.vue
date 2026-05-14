<template>
  <el-card>
    <template #header>
      <div class="card-header"><span>班级管理</span><el-button type="primary" @click="showDialog()">添加班级</el-button></div>
    </template>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="className" label="班级名称" />
      <el-table-column prop="gradeName" label="所属年级" />
      <el-table-column prop="teacherName" label="班主任" />
      <el-table-column prop="studentCount" label="人数" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pagination" background layout="total, prev, pager, next"
                   :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑班级' : '添加班级'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="班级名称"><el-input v-model="form.className" /></el-form-item>
        <el-form-item label="年级ID"><el-input-number v-model="form.gradeId" :min="1" /></el-form-item>
        <el-form-item label="班主任ID"><el-input-number v-model="form.teacherId" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getClasses, addClass, updateClass, deleteClass } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ className: '', gradeId: 1, teacherId: null })

onMounted(() => loadData())
async function loadData() {
  const res = await getClasses({ page: currentPage.value, size: 10 })
  tableData.value = res.data.records; total.value = res.data.total
}
function showDialog(row) {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, className: '', gradeId: 1, teacherId: null })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (isEdit.value) await updateClass(form); else await addClass(form)
  ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
}
async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteClass(id); ElMessage.success('删除成功'); loadData()
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
