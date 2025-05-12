<template>
    <div class="book-upload-container">
      <h2 class="title">上传书籍</h2>
      <form @submit.prevent="handleUpload" enctype="multipart/form-data">
        <!-- 基础信息 -->
        <div class="form-section">
          <h3>书籍基础信息</h3>
          <div class="form-group">
            <label>书籍标题</label>
            <input
              type="text"
              v-model.trim="form.title"
              :class="{ error: errors.title }"
              placeholder="请输入书籍标题（必填）"
            />
            <p v-if="errors.title" class="error-message">{{ errors.title }}</p >
          </div>
  
          <div class="form-group">
            <label>作者</label>
            <input
              type="text"
              v-model.trim="form.author"
              :class="{ error: errors.author }"
              placeholder="请输入作者"
            />
          </div>
  
          <div class="form-group">
            <label>类别</label>
            <select v-model="form.category" :class="{ error: errors.category }">
              <option value="">请选择类别</option>
              <option value="文学">文学</option>
              <option value="科幻">科幻</option>
              <option value="技术">技术</option>
            </select>
            <p v-if="errors.category" class="error-message">请选择书籍类别</p >
          </div>
        </div>
  
        <!-- 盲盒选项 -->
        <div class="form-section" v-if="showBlindBoxOptions">
          <h3>创建盲盒</h3>
          <div class="form-group">
            <label>
              <input type="checkbox" v-model="form.isBlindBox" @change="toggleBlindBox" />
              设为盲盒（隐藏书籍详情，需上传封面）
            </label>
          </div>
  
          <div v-show="form.isBlindBox" class="blind-box-note">
            盲盒书籍将随机分配类别，打开后显示真实信息
          </div>
        </div>
  
        <!-- 文件上传 -->
        <div class="form-section">
          <h3>上传封面</h3>
          <div class="file-upload">
            <label class="file-input-label">
              选择图片文件（支持JPG/PNG，最大2MB）
              <input
                type="file"
                accept="image/*"
                @change="handleFileUpload"
                :disabled="isUploading"
              />
            </label>
            <div v-if="form.coverUrl" class="preview-image">
              < img :src="form.coverUrl" alt="封面预览" />
              <button @click="clearCover">删除预览</button>
            </div>
          </div>
        </div>
  
        <!-- 提交按钮 -->
        <button
          type="submit"
          class="upload-btn"
          :disabled="!isFormValid || isUploading"
        >
          {{ isUploading ? '上传中...' : '提交审核' }}
        </button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, watch } from 'vue';
  import { uploadBook } from '@/api/book'; // 引入书籍上传接口
  import { useUserStore } from '@/stores/user'; // 引入用户状态存储
  import { generateUUID } from '@/utils/uuid'; // 引入UUID生成工具
  
  const userStore = useUserStore();
  const form = reactive({
    title: '',
    author: '',
    category: '',
    isBlindBox: false,
    coverUrl: null,
    blockchainTxHash: '' // 区块链交易哈希
  });
  
  const errors = reactive({
    title: '',
    category: ''
  });
  
  const isUploading = ref(false);
  const showBlindBoxOptions = ref(true); // 可根据业务需求控制是否显示盲盒选项
  
  // 表单验证
  const isFormValid = computed(() => {
    errors.title = form.title ? '' : '请输入书籍标题';
    errors.category = form.category ? '' : '请选择书籍类别';
    if (form.isBlindBox && !form.coverUrl) {
      errors.cover = '盲盒书籍需上传封面';
      return false;
    }
    return !Object.values(errors).some(msg => msg);
  });
  
  // 切换盲盒选项
  const toggleBlindBox = () => {
    if (form.isBlindBox) {
      form.category = ''; // 盲盒自动分配类别，清空用户选择
    }
  };
  
  // 文件上传处理
  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        form.coverUrl = event.target.result;
      };
      reader.readAsDataURL(file);
    }
  };
  
  // 清除封面预览
  const clearCover = () => {
    form.coverUrl = null;
    form.coverFile = null;
  };
  
  // 提交上传
  const handleUpload = async () => {
    if (!isFormValid.value) return;
  
    try {
      isUploading.value = true;
      // 生成唯一书籍ID
      const bookId = generateUUID();
      // 构造上传数据
      const uploadData = new FormData();
      uploadData.append('bookId', bookId);
      uploadData.append('title', form.title);
      uploadData.append('author', form.author);
      uploadData.append('category', form.isBlindBox ? 'BLIND_BOX' : form.category);
      uploadData.append('isBlindBox', form.isBlindBox ? 'true' : 'false');
      form.coverFile && uploadData.append('cover', form.coverFile);
  
      // 调用后端接口上传书籍
      const response = await uploadBook(uploadData);
      form.blockchainTxHash = response.data.txHash; // 存储区块链交易哈希
      userStore.addBook(bookId); // 更新用户书架
  
      // 跳转至书籍详情页或提示成功
      console.log('书籍上传成功，交易哈希:', form.blockchainTxHash);
      // router.push(`/book/${bookId}`);
    } catch (error) {
      console.error('上传失败:', error.response?.data?.message || '上传过程中出现错误');
      if (error.response?.status === 400) {
        errors.title = '书籍标题已存在';
      }
    } finally {
      isUploading.value = false;
    }
  };
  
  // 监听文件输入框变化
  watch(() => form.coverUrl, (val) => {
    if (val) {
      form.coverFile = document.querySelector('input[type="file"]').files[0];
    }
  });
  </script>
  
  <style scoped>
  .book-upload-container {
    max-width: 600px;
    margin: 30px auto;
    padding: 25px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 15px rgba(0,0,0,0.08);
  }
  
  .title {
    text-align: center;
    margin-bottom: 25px;
    color: #1a73e8;
  }
  
  .form-section {
    margin-bottom: 30px;
  }
  
  .form-group {
    margin-bottom: 18px;
  }
  
  label {
    display: block;
    margin-bottom: 8px;
    color: #333;
    font-weight: 500;
  }
  
  input, select {
    width: 100%;
    padding: 12px;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    font-size: 16px;
    transition: border-color 0.3s;
  }
  
  input:focus, select:focus {
    border-color: #1a73e8;
    outline: none;
  }
  
  .error {
    border-color: #ff4444;
  }
  
  .error-message {
    color: #ff4444;
    font-size: 0.9em;
    margin-top: 5px;
  }
  
  .file-upload {
    border: 2px dashed #e0e0e0;
    border-radius: 8px;
    padding: 40px 20px;
    text-align: center;
    cursor: pointer;
    transition: border-color 0.3s;
  }
  
  .file-upload:hover {
    border-color: #1a73e8;
  }
  
  .file-input-label input {
    display: none;
  }
  
  .preview-image {
    margin-top: 20px;
    border: 1px solid #e0e0e0;
    padding: 10px;
    border-radius: 4px;
  }
  
  .preview-image img {
    max-width: 200px;
    max-height: 300px;
    margin-bottom: 10px;
  }
  
  .preview-image button {
    background: #ff4444;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .upload-btn {
    width: 100%;
    padding: 14px;
    background: #1a73e8;
    color: #fff;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background 0.3s;
    margin-top: 25px;
  }
  
  .upload-btn:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
  
  .blind-box-note {
    color: #666;
    font-size: 0.95em;
    margin-top: 10px;
    padding: 10px;
    background: #f9f9f9;
    border-radius: 4px;
  }
  </style>