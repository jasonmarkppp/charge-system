<template>
  <el-card>
    <template #header>
      <div class="card-header"><span>评分标准管理</span><el-button type="primary" @click="showDialog()">添加标准</el-button></div>
    </template>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="criteriaName" label="维度名称" />
      <el-table-column prop="maxScore" label="满分" width="80" />
      <el-table-column prop="weight" label="权重" width="80" />
      <el-table-column prop="description" label="说明" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑标准' : '添加标准'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="维度名称"><el-input v-model="form.criteriaName" /></el-form-item>
        <el-form-item label="满分"><el-input-number v-model="form.maxScore" :min="1" :max="100" /></el-form-item>
        <el-form-item label="权重"><el-input-number v-model="form.weight" :min="0" :max="1" :step="0.1" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
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
import { getCriteria, addCriteria, updateCriteria, deleteCriteria } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ criteriaName: '', maxScore: 10, weight: 1.0, description: '', sortOrder: 0 })

onMounted(() => loadData())
async function loadData() { const res = await getCriteria({}); tableData.value = res.data }
function showDialog(row) {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, criteriaName: '', maxScore: 10, weight: 1.0, description: '', sortOrder: 0 })
  dialogVisible.value = true
}
async function handleSubmit() {
  if (isEdit.value) await updateCriteria(form); else await addCriteria(form)
  ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
}
async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteCriteria(id); ElMessage.success('删除成功'); loadData()
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
